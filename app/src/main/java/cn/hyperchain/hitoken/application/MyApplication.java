package cn.hyperchain.hitoken.application;

import android.app.Application;
import android.content.Context;

import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import cn.hyperchain.hitoken.utils.TypefaceUtil;
import com.umeng.commonsdk.UMConfigure;

import java.io.IOException;


/**
 * Created by admin on 2017/11/7.
 */

public class MyApplication extends Application {

    public static String phoneType = "Android";
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.replaceSystemDefaultFont(this,"font/Roboto-Regular.ttf");

        UMConfigure.init(mContext, "5b73f1e4f43e480cc70000e3", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
//        TypefaceUtil.setDefaultFont(this, "DEFAULT", "font/Roboto-Bold.ttf");
        mContext = getApplicationContext();
        try {
            RetrofitUtil.init(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Context getmContext() {
        return mContext;
    }
}
