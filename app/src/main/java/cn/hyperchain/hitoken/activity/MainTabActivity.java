package cn.hyperchain.hitoken.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.Center;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.interf.ManagerFragment;
import cn.hyperchain.hitoken.utils.DataUtils;
import cn.hyperchain.hitoken.view.CommonPopupWindow;
import cn.hyperchain.hitoken.view.SlidingMenu;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hyperchain.hitoken.fragment.maintab.community.CommunityFragment;
import cn.hyperchain.hitoken.fragment.maintab.dapp.DappFragment;
import cn.hyperchain.hitoken.fragment.maintab.market.MarketFragment;
import cn.hyperchain.hitoken.fragment.maintab.wallet.MainFragment;
import cn.hyperchain.hitoken.imagecrop.Constants;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainTabActivity extends CheckPermissionsActivity implements ManagerFragment {

    @BindView(R.id.tab_wallet)
    TextView tab_wallet;

    @BindView(R.id.tab_market)
    TextView tab_market;

    @BindView(R.id.tab_community)
    TextView tab_community;

    @BindView(R.id.tab_dapp)
    TextView tab_dapp;

    @BindView(R.id.id_menu)
    SlidingMenu mMenu;

    @BindView(R.id.root)
    LinearLayout root;


    @BindView(R.id.ll_head)
    LinearLayout llHead;


    @BindView(R.id.iv_headImg)
    ImageView ivHeadImg;

    @BindView(R.id.tv_nickname)
    TextView tvNickname;

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @BindView(R.id.rl_message)
    RelativeLayout rlMessage;

    @BindView(R.id.rl_setting)
    RelativeLayout rlSetting;

    @BindView(R.id.rl_help)
    RelativeLayout rlHelp;

    @BindView(R.id.rl_exit)
    RelativeLayout rlExit;

    public static final int LOADING_DURATION = 500;

    private Fragment currentFragment;
    private MainFragment mainFragment;
    private MarketFragment marketFragment;
    private CommunityFragment communityFragment;
    private DappFragment dappFragment;

    private CommonPopupWindow logoutWindow;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        ButterKnife.bind(this);
        initFragmentList();
        loadFirstFragment();
        initLogoutPopupWindow();

    }


    @Override
    protected void onResume() {
        super.onResume();
        RetrofitUtil.getService().getCenter().enqueue(new Callback<MyResult<Center>>() {
            @Override
            public void onResponse(Response<MyResult<Center>> response, Retrofit retrofit) {
                MyResult result = response.body();
                if(result.getStatusCode() == 200) {
                    Center center = (Center)result.getData();
                    if(center.getPortrait() != null && !center.getPortrait().isEmpty()) {
                        url = Constants.NETPICTAILAPPHEAD + center.getPortrait();
                        Glide.with(MainTabActivity.this).load(url).into(ivHeadImg);
                    }
                    tvNickname.setText(center.getUser_name());
                    tvPhone.setText(center.getPhone_number());
                } else {
                    showToast((String)result.getData());
                }

            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }


    //初始化加载
    private void loadFirstFragment() {
        tab_wallet.setSelected(true);
        showFragment(mainFragment);
    }

    //重置选择
    private void setSelect() {
        tab_wallet.setSelected(false);
        tab_market.setSelected(false);
        tab_community.setSelected(false);
        tab_dapp.setSelected(false);
    }

    private void initLogoutPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight=metrics.heightPixels;
        int screenWidth=metrics.widthPixels;
        // create popup window
        logoutWindow = new CommonPopupWindow(this, R.layout.popup_logout, DataUtils.dip2px(this,280),
                DataUtils.dip2px(this,120)) {
            @Override
            protected void initView() {
                View view = getContentView();
                TextView tvSubmit = view.findViewById(R.id.tv_submit);
                TextView tvCancel = view.findViewById(R.id.tv_cancel);



                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                        getPopupWindow().dismiss();
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

    @OnClick({
            R.id.tab_wallet, R.id.tab_market, R.id.tab_community, R.id.tab_dapp,
            R.id.ll_head,
            R.id.rl_message,R.id.rl_setting,R.id.rl_help,R.id.rl_exit
    })
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tab_wallet:
                setSelect();
                showFragment(mainFragment);
                tab_wallet.setSelected(true);
                break;
            case R.id.tab_market:
                setSelect();
                showFragment(marketFragment);
                tab_market.setSelected(true);

                break;
            case R.id.tab_community:
                setSelect();
                showFragment(communityFragment);
                tab_community.setSelected(true);

                break;
            case R.id.tab_dapp:
                setSelect();
                showFragment(dappFragment);
                tab_dapp.setSelected(true);
                break;
            case R.id.ll_head:
                intent = new Intent(MainTabActivity.this,PersonalActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
                break;


            case R.id.rl_message:
                intent = new Intent(MainTabActivity.this,MessageActivity.class);
                startActivity(intent);
                break;

            case R.id.rl_setting:
                intent = new Intent(MainTabActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_help:
//                intent = new Intent(MainTabActivity.this,FileDisplayActivity.class);
//                intent.putExtra("from",1);
//                startActivity(intent);
                intent = new Intent(MainTabActivity.this,HelpActivity.class);
                startActivity(intent);
                break;

            case R.id.rl_exit:
                WindowManager.LayoutParams lp3=MainTabActivity.this.getWindow().getAttributes();
                lp3.alpha=0.3f;
                logoutWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
                MainTabActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                MainTabActivity.this.getWindow().setAttributes(lp3);
                break;
        }
    }


    @Override
    public void showFragment(Fragment fg) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!fg.isAdded()) {
            transaction.hide(currentFragment).add(R.id.fl_content, fg);
        } else {
            transaction.hide(currentFragment).show(fg);
        }

        currentFragment = fg;

        transaction.commit();

    }

    @Override
    public void initFragmentList() {
        currentFragment = new Fragment();
        mainFragment = new MainFragment();
        marketFragment = new MarketFragment();
        communityFragment = new CommunityFragment();
        dappFragment = new DappFragment();
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
