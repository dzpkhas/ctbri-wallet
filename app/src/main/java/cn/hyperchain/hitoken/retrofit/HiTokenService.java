package cn.hyperchain.hitoken.retrofit;


import cn.hyperchain.hitoken.entity.Address;
import cn.hyperchain.hitoken.entity.Center;
import cn.hyperchain.hitoken.entity.Gas;
import cn.hyperchain.hitoken.entity.HistoryInfo;
import cn.hyperchain.hitoken.entity.LoginToken;
import cn.hyperchain.hitoken.entity.Message;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.TranDetail;
import cn.hyperchain.hitoken.entity.Wallet;
import com.squareup.okhttp.RequestBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.HTTP;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


/**
 * Created by yanceywang on 3/27/16.
 */
public interface HiTokenService {

    /**
     * 注解中的路径不需要带/
     */
    /**
       * 获取交易记录
     */
    @GET("txlist")
    Call<MyResult<List<HistoryInfo>>> getTxlist(@Query("address") String address, @Query("type") String type,
                                                @Query("index") int index, @Query("size") int size,
                                                @Query("send") boolean send, @Query("receive") boolean receive);

    /**
     * 获取验证码
     */
    @POST("sms")
    Call<MyResult> getAuthCode(@Body RequestBody body);

    /**
     * 注册
     */
    @POST("register")
    Call<MyResult> register(@Body RequestBody body);

    /**
     * 登入
     */
    @POST("login")
    Call<MyResult<LoginToken>> login(@Body RequestBody body);


    /**
     * 个人中心
     */
    @GET("center")
    Call<MyResult<Center>> getCenter();

    /**
     * 添加钱包
     */
    @POST("addWallet")
    Call<MyResult> addWallet(@Body RequestBody body);


    /**
     * 注册钱包
     */
    @POST("registerWallet")
    Call<MyResult> registerWallet(@Body RequestBody body);

    /**
     * 获取钱包主页
     */
    @GET("wallet")
    Call<MyResult<List<Wallet>>> getWallet();


    /**
     * 获取地址簿
     */
    @GET("book")
    Call<MyResult<Address>> getAddress(@Query("index") Integer index,@Query("size") Integer size,@Query("type") String type);



    /**
     * 添加记录
     */
    @POST("book")
    Call<MyResult> addAddress(@Body RequestBody body);

    /**
     * 删除记录
     */
    @HTTP(method = "DELETE",path = "book",hasBody = true)
    Call<MyResult> deleteAddress(@Body RequestBody body);

    /**
     * 修改记录
     */
    @POST("editBook")
    Call<MyResult> editAddress(@Body RequestBody body);


    /**
     * 获取汇率
     */
    @GET("coinmarket")
    Call<MyResult> getCoinmarket(@Query("address") String address);

    /**
     * 获取汇率
     */
    @GET("gas")
    Call<MyResult<Gas>> getGas();

    /**
     * 交易转账
     */
    @POST("transaction")
    Call<MyResult> transaction(@Body RequestBody body);

    /**
     * 修改昵称
     */
    @POST("nickname")
    Call<MyResult> updateNickname(@Body RequestBody body);

    /**
     * 图片上传
     */
    @POST("portrait")
    Call<MyResult> postImage(@Body RequestBody body);


    /**
     * 修改交易密码
     */
    @POST("password")
    Call<MyResult> updatePassword(@Body RequestBody body);

    /**
     * 修改支付密码
     */
    @POST("payword")
    Call<MyResult> updatePayword(@Body RequestBody body);

    /**
     * 获取交易详情
     */
    @GET("detail/{id}")
    Call<MyResult<TranDetail>> tranDetail(@Path("id") Long id);

    /**
     * 获取交易详情
     */
    @GET("messagelist")
    Call<MyResult<List<Message>>> getMessageList(@Query("index") Integer index, @Query("size") Integer size);

//    Call<Tngou> getCook(@Query("page") int page, @Query("rows") int rows);
}
