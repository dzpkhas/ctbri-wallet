package cn.hyperchain.hitoken.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

import cn.hyperchain.hitoken.R;


/**
 * Created by yanceywang on 3/29/16.
 */
public class VerCodeTimer extends CountDownTimer {
    private int seconds;
    private int interval;
    private Button btn;
    private Context context;

    //millisInFuture为你设置的此次倒计时的总时长，比如60秒就设置为60000
    //countDownInterval为你设置的时间间隔，比如一般为1秒,根据需要自定义。
    public VerCodeTimer(long millisInFuture, long countDownInterval, Button btn, Context context) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
        this.context = context;
        seconds = (int) (millisInFuture / 1000);
        interval = (int) (countDownInterval / 1000);
    }

    //每过你规定的时间间隔做的操作
    @Override
    public void onTick(long millisUntilFinished) {
        btn.setBackgroundResource(R.drawable.bg_authcode_grey_rounded_rectangle);
        btn.setText(( millisUntilFinished / 1000 ) + "秒后获取");
    }

    //倒计时结束时做的操作
    @Override
    public void onFinish() {
        btn.setClickable(true);
        btn.setText("获取验证码");
    }
}


