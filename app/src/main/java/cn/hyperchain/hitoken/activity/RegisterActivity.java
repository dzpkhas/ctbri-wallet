package cn.hyperchain.hitoken.activity;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.utils.VerCodeTimer;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RegisterActivity extends TemplateActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.et_account)
    EditText etAccout;

    @BindView(R.id.et_authcode)
    EditText etAuthcode;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.et_password_verify)
    EditText etPasswordVerify;

    @BindView(R.id.tv_register)
    TextView tvRegister;

    @BindView(R.id.bt_auth_code)
    Button btAuthcode;

    @BindView(R.id.ll_radio)
    LinearLayout llRadio;

    @BindView(R.id.iv_agreement)
    ImageView ivAgreement;

    @BindView(R.id.tv_agreement)
    TextView tvAgreement;

    int agreementClicked = 1;

    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
        etAccout.setCursorVisible(false);
        etAccout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAccout.setCursorVisible(true);
            }
        });
    }

    @OnClick({
            R.id.bt_auth_code,R.id.tv_register,R.id.tv_agreement,R.id.ll_back,R.id.ll_radio
    })
    @Override
    public void onClick(View v) {
        String account = etAccout.getText().toString().trim();
        String authCode = etAuthcode.getText().toString().trim();
        String password = etPassword.getText().toString();
        String passwordVerify = etPasswordVerify.getText().toString();
        String json;
        RequestBody body;
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_auth_code:


                if(account.isEmpty() || account.length() !=  11) {
                    showToast("请输入正确手机号");
                    return;
                }
                VerCodeTimer countDown = new VerCodeTimer(60 * 1000, 1000, btAuthcode, RegisterActivity.this);
                countDown.start();
                btAuthcode.setClickable(false);

                json = "{\"phone_number\":\"" + account +  "\"}";
                body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                RetrofitUtil.getService().getAuthCode(body).enqueue(new Callback<MyResult>() {
                    @Override
                    public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                        MyResult myResult = response.body();
                        if(myResult.getStatusCode() == 200) {
                            showToast("获取验证码成功");
                        } else {
                            showToast((String) myResult.getData());
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.tv_register:
                //do register
                if(account.isEmpty() || account.trim().length() !=  11) {
                    showToast("请输入正确手机号");
                    return;
                }
                if(authCode.isEmpty()) {
                    showToast("请输入验证码");
                    return;
                }
                if(password.isEmpty()) {
                    showToast("请输入密码");
                    return;
                }
                if(passwordVerify.isEmpty()) {
                    showToast("请输入确认密码");
                    return;
                }

                if(!(password.length() >= 8)) {
                    showToast("8位以上密码，需要包含字母和数字，支持使用字母（区分大小写）、数字、特殊字符。");
                    return;
                }

                if(!passwordVerify.equals(password)) {
                    showToast("两次密码输入不一致");
                    return;
                }
                if(agreementClicked == 0) {
                    showToast("请同意用户协议");
                    return;
                }
                json = "{"+"\"phone_number\":\"" + account +  "\""+ "," +
                        "\"password\":\"" + password +  "\""+ "," +
                        "\"check_num\":\"" + authCode +  "\""+
                        "}";
                Log.d("test",json);
                body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                showDialog();
                RetrofitUtil.getService().register(body).enqueue(new Callback<MyResult>() {
                    @Override
                    public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                        hideDialog();
                        MyResult myResult = response.body();
                        if(myResult.getStatusCode() == 200) {
                            showToast("注册成功");
                            finish();
                        } else {
                            showToast((String) myResult.getData());
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        hideDialog();
                        Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.tv_agreement:
//                Intent intent = new Intent(RegisterActivity.this,FileDisplayActivity.class);
//                intent.putExtra("from",0);
//                startActivity(intent);
                Intent intent = new Intent(RegisterActivity.this,AgreementActivity.class);
                startActivity(intent);

                break;

            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_radio:
                if(agreementClicked == 1) {
                    agreementClicked = 0;
                    ivAgreement.setImageResource(R.mipmap.radio_agreement);
                } else {
                    agreementClicked = 1;
                    ivAgreement.setImageResource(R.mipmap.radio_agreement_clicked);
                }
                break;


        }
    }


    @Override
    protected SHOW_TYPE showBackBtn() {
        return SHOW_TYPE.SHOW_ALL;
    }

    @Override
    protected SHOW_TYPE showRightBtn() {
        return SHOW_TYPE.BLANK;

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
