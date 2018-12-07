package cn.hyperchain.hitoken.activity;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import cn.hyperchain.hitoken.R;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.fragment.maintab.wallet.MessageFragment;

public class MessageActivity extends TemplateActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.root)
    LinearLayout root;

    Fragment messageFragment;

    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
        messageFragment = new MessageFragment();
        loadFragment();

    }

    private void  loadFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, messageFragment);
        transaction.commit();
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @OnClick({
            R.id.ll_back
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
        }
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
        return R.layout.activity_message;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
