package cn.hyperchain.hitoken.thread;

import android.os.Handler;
import android.os.Message;

import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.Wallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class WalletRefreshThread implements Runnable {

    public static final int SUCCESSFORINDUCTIONCOUNT = 10;
    public static final int FAILFORINDUCTIONCOUNT = 11;

    HashMap<String, String> body;
    private Handler handler;

    public WalletRefreshThread(Handler handler) {

        this.handler = handler;
    }

    @Override
    public void run() {

        RetrofitUtil.getService().getWallet().enqueue(new Callback<MyResult<List<Wallet>>>() {
            @Override
            public void onResponse(Response<MyResult<List<Wallet>>> response, Retrofit retrofit) {
                MyResult myResult = response.body();

                if(myResult.getStatusCode() == 200) {
                    ArrayList<Wallet> wallets = (ArrayList<Wallet>) myResult.getData();
                    Message msg = new Message();
                    msg.what = SUCCESSFORINDUCTIONCOUNT;
                    msg.obj = wallets;
                    handler.sendMessage(msg);
                } else {

                }

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        handler.postDelayed(this, 5000);
    }
}
