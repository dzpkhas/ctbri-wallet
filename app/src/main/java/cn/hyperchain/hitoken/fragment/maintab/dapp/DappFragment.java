package cn.hyperchain.hitoken.fragment.maintab.dapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.activity.GoldenTicketsActivity;
import cn.hyperchain.hitoken.activity.MainTabActivity;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;
import cn.hyperchain.hitoken.utils.SPHelper;


public class DappFragment extends BaseBarFragment  {


    @BindView(R.id.root)
    ImageView root;



    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_dapp);
        ButterKnife.bind(this, rootView);
        //ButterKnife.bind(this,webview);
        hideActionBar();

    }

    @Override
    public void initData() {
        super.initData();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({
            R.id.root
    })
    @Override
    public void onClick(View view) {
        Intent intent;
        super.onClick(view);
        switch (view.getId()) {
            case R.id.root:
                String walletState = (String)SPHelper.get(getContext(),"walletCreated","");
                if("no".equals(walletState)){
                    showToast("未创建钱包，请先创建钱包");

                }
                else {
                    intent = new Intent(getActivity(),GoldenTicketsActivity.class);
                    startActivity(intent);}

                break;
        }
    }

    @Override
    protected boolean hasBackBtn() {
        return false;
    }

    @Override
    protected boolean hasRightBtn() {
        return false;
    }

    @Override
    protected boolean isRightImg() {
        return false;
    }



}
