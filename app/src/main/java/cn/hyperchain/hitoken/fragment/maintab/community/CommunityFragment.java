package cn.hyperchain.hitoken.fragment.maintab.community;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CommunityFragment extends BaseBarFragment {

    @BindView(R.id.root)
    ImageView root;

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_community);
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
        Intent intent;
        super.onClick(view);
        switch (view.getId()) {
            case R.id.root:
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
