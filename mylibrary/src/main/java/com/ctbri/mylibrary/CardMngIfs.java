package com.ctbri.mylibrary;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class  CardMngIfs<T> {
    OkHttpClient mOkHttpClient;
    final  String Url = "http://10.9.58.159:8080/crmService/submitInfo";
    final  String Url2="http://10.9.58.159:8080/crmService/submitSeed";
    final  String Url3="http://10.9.58.159:8080/crmService/submitReplaceInfoToSmart";
    final  String Url4="http://10.9.58.159:8080/crmService/submitReplaceInfoFullyToSmart";
    final  String Url5="http://10.9.58.159:8080/crmService/requestSeedToSmart";
    final  String Url6="http://10.9.58.159:8080/crmService/requestSimStatus";
    final  String Url7="http://10.9.58.159:8080/crmService/directCancelSim";

    /**
     * Ansy
     * @param Url 访问链接
     * @param ansyCallback 回调接口
     */
    public void submitInfo(final AnsyCallback<T> ansyCallback){
        String phone;
        String idNum;
        String iccid;
        phone = "13012345678";
        idNum = "120104201811160113";
        iccid = "QQ11WW22EE33RR44TT";
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ansyCallback.AnsyLoader((T)msg.obj,Url);
            }
        };
        final RequestBody requestbody = new FormBody.Builder()
                .add("phoneNumber", phone)
                .add("ICCID", iccid)
                .add("idNum", idNum)
                .build();
        mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Url);
        requestBuilder.method("POST",requestbody);
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Message message = handler.obtainMessage(0,str);
                    handler.sendMessage(message);
                    Log.i("wangshu", "cache---" + str);
                } else {
                    String d = response.body().string();
                    String str = response.networkResponse().toString();
                    Message message = handler.obtainMessage(0,d);
                    handler.sendMessage(message);
                    Log.i("wangshu", "network---" + str);
                }
            }
        });
    }
    //接口
    public interface AnsyCallback<T> {
        public void AnsyLoader(T loder,String Url);
    }


    public void submitSeed(final String phone,final  String iccid,final  String seed,final AnsyCallback<T> ansyCallback){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ansyCallback.AnsyLoader((T)msg.obj,Url2);
            }
        };
        final RequestBody requestbody = new FormBody.Builder()
                .add("phoneNumber", phone)
                .add("ICCID", iccid)
                .add("seed", seed)
                .build();
        mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Url2);
        requestBuilder.method("POST",requestbody);
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Message message = handler.obtainMessage(0,str);
                    handler.sendMessage(message);
                    Log.i("wangshu", "cache---" + str);
                } else {
                    String d = response.body().string();
                    String str = response.networkResponse().toString();
                    Message message = handler.obtainMessage(0,d);
                    handler.sendMessage(message);
                    Log.i("wangshu", "network---" + str);
                }
            }
        });
    }
    //接口
    public interface AnsyCallback2<T> {
        public void AnsyLoader(T loder,String Url2);
    }




    public void submitReplaceInfoToSmart(final String phone,final  String iccid,final AnsyCallback3<T> ansyCallback){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ansyCallback.AnsyLoader((T)msg.obj,Url3);
            }
        };
        final RequestBody requestbody = new FormBody.Builder()
                .add("phoneNumber", phone)
                .add("ICCID", iccid)
                .build();
        mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Url3);
        requestBuilder.method("POST",requestbody);
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Message message = handler.obtainMessage(0,str);
                    handler.sendMessage(message);
                    Log.i("wangshu", "cache---" + str);
                } else {
                    String d = response.body().string();
                    String str = response.networkResponse().toString();
                    Message message = handler.obtainMessage(0,d);
                    handler.sendMessage(message);
                    Log.i("wangshu", "network---" + str);
                }
            }
        });
    }
    //接口


    public interface AnsyCallback3<T> {
        public void AnsyLoader(T loder,String Url3);
    }


    public void submitReplaceInfoFullyToSmart(final String phone,final  String iccid,String idNum,final AnsyCallback4<T> ansyCallback){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ansyCallback.AnsyLoader((T)msg.obj,Url4);
            }
        };
        final RequestBody requestbody = new FormBody.Builder()
                .add("phoneNumber", phone)
                .add("ICCID", iccid)
                .add("idNum",idNum)
                .build();
        mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Url4);
        requestBuilder.method("POST",requestbody);
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Message message = handler.obtainMessage(0,str);
                    handler.sendMessage(message);
                    Log.i("wangshu", "cache---" + str);
                } else {
                    String d = response.body().string();
                    String str = response.networkResponse().toString();
                    Message message = handler.obtainMessage(0,d);
                    handler.sendMessage(message);
                    Log.i("wangshu", "network---" + str);
                }
            }
        });
    }
    //接口


    public interface AnsyCallback4<T> {
        public void AnsyLoader(T loder,String Url4);
    }

    public void requestSeedToSmart(final String phone,final  String iccid,final AnsyCallback5<T> ansyCallback){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ansyCallback.AnsyLoader((T)msg.obj,Url5);
            }
        };
        final RequestBody requestbody = new FormBody.Builder()
                .add("phoneNumber", phone)
                .add("ICCID", iccid)
                .build();
        mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Url5);
        requestBuilder.method("POST",requestbody);
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Message message = handler.obtainMessage(0,str);
                    handler.sendMessage(message);
                    Log.i("wangshu", "cache---" + str);
                } else {
                    String d = response.body().string();
                    String str = response.networkResponse().toString();
                    Message message = handler.obtainMessage(0,d);
                    handler.sendMessage(message);
                    Log.i("wangshu", "network---" + str);
                }
            }
        });
    }
    //接口


    public interface AnsyCallback5<T> {
        public void AnsyLoader(T loder,String Url5);
    }


    public void requestSimStatus(final  String iccid,final AnsyCallback6<T> ansyCallback){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ansyCallback.AnsyLoader((T)msg.obj,Url6);
            }
        };
        final RequestBody requestbody = new FormBody.Builder()
                .add("ICCID", iccid)
                .build();
        mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Url6);
        requestBuilder.method("POST",requestbody);
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Message message = handler.obtainMessage(0,str);
                    handler.sendMessage(message);
                    Log.i("wangshu", "cache---" + str);
                } else {
                    String d = response.body().string();
                    String str = response.networkResponse().toString();
                    Message message = handler.obtainMessage(0,d);
                    handler.sendMessage(message);
                    Log.i("wangshu", "network---" + str);
                }
            }
        });
    }
    //接口


    public interface AnsyCallback6<T> {
        public void AnsyLoader(T loder,String Url6);
    }

    public void directCancelSim(final String phone,final   String iccid,final AnsyCallback7<T> ansyCallback){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ansyCallback.AnsyLoader((T)msg.obj,Url7);
            }
        };
        final RequestBody requestbody = new FormBody.Builder()
                .add("phoneNumber", phone)
                .add("ICCID", iccid)
                .build();
        mOkHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(Url7);
        requestBuilder.method("POST",requestbody);
        Request request = requestBuilder.build();
        Call mcall = mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Message message = handler.obtainMessage(0,str);
                    handler.sendMessage(message);
                    Log.i("wangshu", "cache---" + str);
                } else {
                    String d = response.body().string();
                    String str = response.networkResponse().toString();
                    Message message = handler.obtainMessage(0,d);
                    handler.sendMessage(message);
                    Log.i("wangshu", "network---" + str);
                }
            }
        });
    }
    //接口


    public interface AnsyCallback7<T> {
        public void AnsyLoader(T loder,String Url7);
    }

    public JSONObject parseJSONWithJSONObject(String jsonData){
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(jsonData);
            return jsonObject;
        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}

