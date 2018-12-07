package cn.hyperchain.hitoken.activity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hyperchain.hitoken.R;

import butterknife.BindView;
import butterknife.OnClick;

public class AddressDetailActivity extends TemplateActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.tv_nickname)
    TextView tvNickname;

    @BindView(R.id.tv_btc_account)
    TextView tvBtcAccount;

    @BindView(R.id.tv_eth_account)
    TextView tvEthAccount;

    @BindView(R.id.iv_eth_copy)
    ImageView ivEthCopy;

    @BindView(R.id.iv_btc_copy)
    ImageView ivBtcCopy;

    @Override
    public void initView() {
        super.initView();
        hideTitleBar();

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");
        String ethAddress = intent.getStringExtra("ethAddress");
        String btcAddress = intent.getStringExtra("btcAddress");

        tvNickname.setText(nickname);
        tvBtcAccount.setText(btcAddress);
        tvEthAccount.setText(ethAddress);

    }

    @Override
    protected void initData() {
        super.initData();
    }


    @OnClick({
            R.id.ll_back,R.id.iv_eth_copy,R.id.iv_btc_copy
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;

            case R.id.iv_eth_copy:
                String btcAccount = tvBtcAccount.getText().toString();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", btcAccount);
                cm.setPrimaryClip(mClipData);
                showToast("已经复制到剪贴板");
                break;

            case R.id.iv_btc_copy:
                String ethAccount = tvEthAccount.getText().toString();
                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText("Label", ethAccount);
                cm.setPrimaryClip(mClipData);
                showToast("已经复制到剪贴板");
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
        return R.layout.activity_address_detail;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
