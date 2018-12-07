package cn.hyperchain.hitoken.fragment.maintab.market;

import android.view.View;
import android.widget.ImageView;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MarketFragment extends BaseBarFragment {

    @BindView(R.id.root)
    ImageView root;

    boolean flag = true;

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_market);
        ButterKnife.bind(this, rootView);
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


    @OnClick({
            R.id.root
    })
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.root:
                if(flag) {
                   root.setImageResource(R.mipmap.page_market_1);
                   flag = false;
                } else {
                    root.setImageResource(R.mipmap.page_market_0);
                    flag = true;
                }
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
