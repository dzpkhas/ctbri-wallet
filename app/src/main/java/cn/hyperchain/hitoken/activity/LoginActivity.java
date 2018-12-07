package cn.hyperchain.hitoken.activity;


import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.LoginToken;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.utils.SPHelper;

import com.shareopt.lcl.businessapilib.Logic;
import com.shareopt.lcl.businessapilib.SubmitInfoPostRequest;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import cn.hyperchain.hitoken.utils.ToastUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends TemplateActivity {

    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.et_account)
    EditText etAccout;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.tv_login)
    TextView tvLogin;

    @BindView(R.id.tv_register)
    TextView tvRegister;

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
        addLayoutListener(root,tvLogin);
//        SoftHideKeyBoardUtil.assistActivity(this);
    }

    @Override
    public void initData() {
        super.initData();
        SPHelper.clear(LoginActivity.this);
    }


    /**
     * addLayoutListener方法如下
     * @param main 根布局
     * @param scroll 需要显示的最下方View
     */
    public void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //1、获取main在窗体的可视区域
                main.getWindowVisibleDisplayFrame(rect);
                //2、获取main在窗体的不可视区域高度，在键盘没有弹起时，main.getRootView().getHeight()调节度应该和rect.bottom高度一样
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                int screenHeight = main.getRootView().getHeight();//屏幕高度
                //3、不可见区域大于屏幕本身高度的1/4：说明键盘弹起了
                if (mainInvisibleHeight > screenHeight / 4) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    // 4､获取Scroll的窗体坐标，算出main需要滚动的高度
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    //5､让界面整体上移键盘的高度
                    main.scrollTo(0, srollHeight);
                } else {
                    //3、不可见区域小于屏幕高度1/4时,说明键盘隐藏了，把界面下移，移回到原有高度
                    main.scrollTo(0, 0);
                }
            }
        });
    }

    @OnClick({
            R.id.tv_login,R.id.tv_register
    })
    @Override
    public void onClick(View v) {
        final Intent intent;
        String json;
        RequestBody body;
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_register:
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login:
                String account = etAccout.getText().toString().trim();
                String password = etPassword.getText().toString();
//                account = "17342053495";
//                password = "Test123!";
                //http://139.219.4.181:8080账户
//                account = "15088403027";
//                password = "zxc123456";
                //http://132.232.101.233:8080账户
//                account = "12345678902";
//                password = "123456";
                if(account.isEmpty() || account.length() !=  11) {
                    showToast("请输入正确手机号");
                    return;
                }
                if(password.isEmpty()) {
                    showToast("请输入密码");
                    return;
                }

                json = "{"+"\"phone_number\":\"" + account +  "\""+ "," +
                        "\"password\":\"" + password +  "\""+
                        "}";
                Log.d("test",json);
                body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                intent = new Intent(LoginActivity.this,MainTabActivity.class);
//                startActivity(intent);
//                finish();
                showDialog();
                RetrofitUtil.getService().login(body).enqueue(new Callback<MyResult<LoginToken>>() {
                    @Override
                    public void onResponse(Response<MyResult<LoginToken>> response, Retrofit retrofit) {
                        hideDialog();
                        MyResult myResult = response.body();
                        if(myResult.getStatusCode() == 200) {
                            showToast("登入成功");
                            LoginToken loginToken = (LoginToken)myResult.getData();
                            SPHelper.put(LoginActivity.this,"token",loginToken.getToken());
                            SPHelper.put(LoginActivity.this,"bluetooth",loginToken.getBlue_tooth());
                            Log.d("LoginActivity",loginToken.getBlue_tooth());

//                            SPHelper.put(LoginActivity.this,"bluetooth","D0:B5:C2:B8:95:C2");

                            //方便调试，修改蓝牙地址。author:liunan 737720233@qq.com
                            SPHelper.put(LoginActivity.this,"bluetooth","D0:B5:C2:B8:95:C6");
                            startActivity(intent);
                            finish();

                        } else {
                            showToast((String) myResult.getData());
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        hideDialog();
                        Toast.makeText(getBaseContext(), "请检查您的密码", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }


    @Override
    protected SHOW_TYPE showBackBtn() {
        return SHOW_TYPE.BLANK;
    }

    @Override
    protected SHOW_TYPE showRightBtn() {
        return SHOW_TYPE.BLANK;

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

    long firstTime = 0;
    //双击退出
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            long secondTime = System.currentTimeMillis();
            if (secondTime-firstTime>2000){
                Toast.makeText(this,"再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
                return true;
            }else {
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

}
