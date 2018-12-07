package cn.hyperchain.hitoken.retrofit;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cn.hyperchain.hitoken.BuildConfig;
import cn.hyperchain.hitoken.utils.SPHelper;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.hyperchain.hitoken.application.MyApplication;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


public class RetrofitUtil {
    public static final String HOST = "http://42.159.93.214:8080/api/v1/";//可用金票
//    public static final String HOST = "http://139.219.4.181:8080/api/v1/";//可用以太坊

    private static Retrofit retrofit;
    private static HiTokenService apiService;
    private static boolean isInit = false;

    public static void init(Context context) throws IOException {
        if (!isInit) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

                @Override
                public void log(String message) {
                    if (BuildConfig.DEBUG) {
                        Log.i("OKHttp", message);
                    }
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient httpClient = new OkHttpClient();
            httpClient.setConnectTimeout(15, TimeUnit.SECONDS);
            httpClient.setReadTimeout(15, TimeUnit.SECONDS);
            httpClient.setWriteTimeout(15, TimeUnit.SECONDS);
            httpClient.setRetryOnConnectionFailure(true);
            httpClient.interceptors().add(logging);
            httpClient.interceptors().add(new MyInterceptor());
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:s").create();
            retrofit = new Retrofit.Builder().baseUrl(HOST).addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient).build();
            isInit = true;
        }
    }

    public static HiTokenService getService() {
        if (apiService == null) {
            apiService = retrofit.create(HiTokenService.class);
        }
        return apiService;
    }

    public static class MyInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            String token = (String) SPHelper.get(MyApplication.getmContext(),"token","");
            String tokenStr = "token=" + token;
            Request request = chain.request();
            Response response;
            if(!token.equals("")) {
                Request compressedRequest = request.newBuilder()
                        .header("Content-type","application/x-www-form-urlencoded; charset=UTF-8")
                        .header("cookie", tokenStr)
                        .build();

                response = chain.proceed(compressedRequest);
            } else {
                response = chain.proceed(request);
            }


            return response;
        }
    }
}

