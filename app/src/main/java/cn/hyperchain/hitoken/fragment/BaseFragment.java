package cn.hyperchain.hitoken.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by admin on 2017/11/6.
 */

public class BaseFragment extends Fragment implements View.OnClickListener{

    protected LayoutInflater _inflate;
    protected View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this._inflate = inflater;
        rootView = inflater.inflate(getLayoutId(),container,false);
        initViews(rootView);
        initData();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void initData() {

    }

    public void initViews(View rootView) {

    }
    public void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
    protected int getLayoutId() {
        return 0;
    }

    protected View inflateView(int resId) {
        return _inflate.inflate(resId, null);
    }

    @Override
    public void onClick(View view) {

    }
}
