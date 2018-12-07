package cn.hyperchain.hitoken.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;

import cn.hyperchain.hitoken.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HistoryFragment extends BaseBarFragment {

    @BindView(R.id.ll_all)
    RelativeLayout ll_all;

    @BindView(R.id.ll_gathering)
    RelativeLayout ll_gathering;

    @BindView(R.id.ll_payment)
    RelativeLayout ll_payment;

    private Fragment currentFragment;
    private HistoryAllFragment historyAllFragment;
    private HistoryGatheringFragment historyGatheringFragment;
    private HistoryPaymentFragment historyPaymentFragment;

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_history);
        hideActionBar();
        ButterKnife.bind(this, rootView);
        initFragmentList();
        loadFirstFragment();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initFragmentList() {
        currentFragment = new Fragment();
        Bundle bundle = getArguments();

        historyAllFragment = new HistoryAllFragment();
        historyAllFragment.setArguments(bundle);

        historyGatheringFragment = new HistoryGatheringFragment();
        historyGatheringFragment.setArguments(bundle);

        historyPaymentFragment = new HistoryPaymentFragment();
        historyPaymentFragment.setArguments(bundle);
    }

    private void loadFirstFragment() {
        ll_all.setSelected(true);
        showFragment(historyAllFragment);
    }

    private void showFragment(Fragment fg) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (!fg.isAdded()) {
            transaction.hide(currentFragment).add(R.id.fl_content, fg);
        } else {
            transaction.hide(currentFragment).show(fg);
        }

        currentFragment = fg;

        transaction.commit();

    }

    //重置选择
    private void setSelect() {
        ll_all.setSelected(false);
        ll_gathering.setSelected(false);
        ll_payment.setSelected(false);
    }

    @OnClick({
            R.id.ll_all, R.id.ll_gathering, R.id.ll_payment
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_all:
                setSelect();
                showFragment(historyAllFragment);
                ll_all.setSelected(true);
                break;
            case R.id.ll_gathering:
                setSelect();
                showFragment(historyGatheringFragment);
                ll_gathering.setSelected(true);
                break;
            case R.id.ll_payment:
                setSelect();
                showFragment(historyPaymentFragment);
                ll_payment.setSelected(true);
                break;

        }
    }

    @Override
    protected boolean hasBackBtn() {
        return false;
    }

    @Override
    protected boolean hasRightBtn() {
        return true;
    }

    @Override
    protected boolean isRightImg() {
        return true;
    }
}
