package cn.hyperchain.hitoken.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import cn.hyperchain.hitoken.R;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.fragment.maintab.wallet.AddWalletFragment;


public class AddWalletActivity extends TemplateActivity {


    @BindView(R.id.ll_back)
    LinearLayout llBack;

    Fragment addWalletFragment;

    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
    }
    @Override
    protected void initData() {
        super.initData();
        addWalletFragment = new AddWalletFragment();
        loadFragment();
    }

    private void  loadFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, addWalletFragment);
        transaction.commit();
    }
    @OnClick({
            R.id.ll_back
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
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
        return R.layout.activity_add_wallet;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}