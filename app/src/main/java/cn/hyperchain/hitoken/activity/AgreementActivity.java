package cn.hyperchain.hitoken.activity;


import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import cn.hyperchain.hitoken.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AgreementActivity extends TemplateActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;


    @Override
    public void initView() {
        super.initView();
        hideTitleBar();

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
        return R.layout.activity_agreement;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
