package cn.hyperchain.hitoken.thread;

import android.os.Handler;

public class CheckDataThread implements Runnable {

    private Handler handler;
    private String data;

    public CheckDataThread(Handler handler,String data) {

        this.handler = handler;
        this.data = data;
    }

    @Override
    public void run() {

    }
}
