package cn.hyperchain.hitoken.activity;


import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.post.UpdatePassword;
import cn.hyperchain.hitoken.utils.DataUtils;
import cn.hyperchain.hitoken.view.CommonPopupWindow;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SettingActivity extends TemplateActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.ll_account_password)
    LinearLayout llAccountPassword;

    @BindView(R.id.ll_trans_password)
    LinearLayout llTransPassword;

    @BindView(R.id.ll_language)
    LinearLayout llLanguage;

    @BindView(R.id.ll_currency_unit)
    LinearLayout llCurrencyUnit;

    private CommonPopupWindow accountPasswordwindow;
    private CommonPopupWindow tranPasswordwindow;
    private CommonPopupWindow tranMonetaryUnit;


    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
        initAccountPasswordPopupWindow();
        initTranPasswordPopupWindow();
        initMonetaryUnitPopupWindow();

    }

    @Override
    protected void initData() {
        super.initData();
    }


    @OnClick({
            R.id.ll_back,R.id.ll_account_password,R.id.ll_trans_password,R.id.ll_language,R.id.ll_currency_unit
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_account_password:
                WindowManager.LayoutParams lp2=getWindow().getAttributes();
                lp2.alpha=0.3f;
                accountPasswordwindow.showAtLocation(root, Gravity.CENTER, 0, 0);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp2);
                break;
            case R.id.ll_trans_password:
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha=0.3f;
                tranPasswordwindow.showAtLocation(root, Gravity.CENTER, 0, 0);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp);
                break;
            case R.id.ll_language:
                break;
            case R.id.ll_currency_unit:
                WindowManager.LayoutParams lp1=getWindow().getAttributes();
                lp1.alpha=0.3f;
                tranMonetaryUnit.showAtLocation(root, Gravity.CENTER, 0, 0);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp1);
                break;
        }
    }

    private void initAccountPasswordPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight=metrics.heightPixels;
        int screenWidth=metrics.widthPixels;
        // create popup window
        accountPasswordwindow = new CommonPopupWindow(this, R.layout.popup_change_password, DataUtils.dip2px(this,280), DataUtils.dip2px(this,326)) {
            @Override
            protected void initView() {
                View view = getContentView();
                final EditText etOldPassword = view.findViewById(R.id.et_old_password);
                final EditText etPassword = view.findViewById(R.id.et_password);
                final EditText edPasswordVerify = view.findViewById(R.id.et_password_verify);
                TextView tvSubmit = view.findViewById(R.id.tv_submit);
                TextView tvCancel = view.findViewById(R.id.tv_cancel);



                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String oldPassword = etOldPassword.getText().toString();
                        String password = etPassword.getText().toString();
                        final String passwordVerify = edPasswordVerify.getText().toString();
                        if(oldPassword.isEmpty()) {
                            showToast("请输入原密码");
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

                        if(!passwordVerify.equals(password)) {
                            showToast("两次密码输入不一致");
                            return;
                        }

                        UpdatePassword updatePassword = new UpdatePassword();
                        updatePassword.setNew_password(password);
                        updatePassword.setOld_password(oldPassword);

                        Gson gson = new Gson();
                        String json = gson.toJson(updatePassword);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                        showDialog();
                        RetrofitUtil.getService().updatePassword(body).enqueue(new Callback<MyResult>() {
                            @Override
                            public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                                hideDialog();
                                MyResult result = response.body();
                                if(result.getStatusCode() == 200) {
                                    showToast("修改密码成功");
                                    getPopupWindow().dismiss();
                                } else {
                                    showToast((String)result.getData());
                                }

                            }

                            @Override
                            public void onFailure(Throwable t) {
                                hideDialog();
                                Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPopupWindow().dismiss();
                    }
                });

            }

            @Override
            protected void initEvent() {

            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance=getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        WindowManager.LayoutParams lp=getWindow().getAttributes();
                        lp.alpha=1.0f;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(lp);

                    }
                });
            }
        };
    }


    private void initTranPasswordPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight=metrics.heightPixels;
        int screenWidth=metrics.widthPixels;
        // create popup window
        tranPasswordwindow = new CommonPopupWindow(this, R.layout.popup_change_tran_password, DataUtils.dip2px(this,280), DataUtils.dip2px(this,326)) {
            @Override
            protected void initView() {
                View view = getContentView();
                final EditText etOldPassword = view.findViewById(R.id.et_old_password);
                final EditText etPassword = view.findViewById(R.id.et_password);
                final EditText edPasswordVerify = view.findViewById(R.id.et_password_verify);
                TextView tvSubmit = view.findViewById(R.id.tv_submit);
                TextView tvCancel = view.findViewById(R.id.tv_cancel);



                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String oldPassword = etOldPassword.getText().toString();
                        String password = etPassword.getText().toString();
                        final String passwordVerify = edPasswordVerify.getText().toString();
                        if(oldPassword.isEmpty()) {
                            showToast("请输入原密码");
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

                        if(!passwordVerify.equals(password)) {
                            showToast("两次密码输入不一致");
                            return;
                        }

                        UpdatePassword updatePassword = new UpdatePassword();
                        updatePassword.setNew_password(password);
                        updatePassword.setOld_password(oldPassword);

                        Gson gson = new Gson();
                        String json = gson.toJson(updatePassword);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                        showDialog();
                        RetrofitUtil.getService().updatePayword(body).enqueue(new Callback<MyResult>() {
                            @Override
                            public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                                hideDialog();
                                MyResult result = response.body();
                                if(result.getStatusCode() == 200) {
                                    showToast("修改支付密码成功");
                                    getPopupWindow().dismiss();
                                } else {
                                    showToast((String)result.getData());
                                }

                            }

                            @Override
                            public void onFailure(Throwable t) {
                                hideDialog();
                                Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPopupWindow().dismiss();
                    }
                });

            }

            @Override
            protected void initEvent() {

            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance=getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        WindowManager.LayoutParams lp=getWindow().getAttributes();
                        lp.alpha=1.0f;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(lp);

                    }
                });
            }
        };
    }

    private void initMonetaryUnitPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // create popup window
        tranMonetaryUnit = new CommonPopupWindow(this, R.layout.popup_money_unit, DataUtils.dip2px(this,280), DataUtils.dip2px(this,180)) {
            @Override
            protected void initView() {
                View view = getContentView();


            }

            @Override
            protected void initEvent() {

            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance=getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        WindowManager.LayoutParams lp=getWindow().getAttributes();
                        lp.alpha=1.0f;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(lp);

                    }
                });
            }
        };
    }


    @Override
    protected SHOW_TYPE showBackBtn() {
        return SHOW_TYPE.SHOW_IMG;
    }

    @Override
    protected SHOW_TYPE showRightBtn() {
        return SHOW_TYPE.BLANK;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
