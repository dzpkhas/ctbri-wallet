package cn.hyperchain.hitoken.fragment;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.utils.DialogHelp;
import cn.hyperchain.hitoken.utils.LoadingDialog;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by admin on 2017/11/6.
 */

public abstract class BaseBarFragment extends BaseFragment {

    protected LinearLayout layout;
    protected ImageView leftImg;
    protected TextView title;
    protected ImageView rightImg;
    protected TextView rightTxt;
    protected FrameLayout contentView;
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();

    LoadingDialog dialog;
    //对话框是否可见
    protected boolean _isVisiable = true;

    //进度对话框
    private ProgressDialog _waitDialog;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        layout = rootView.findViewById(R.id.my_actionbar);
        leftImg = rootView.findViewById(R.id.my_actionbar_img_left);
        title = rootView.findViewById(R.id.my_actionbar_title);
        rightImg = rootView.findViewById(R.id.my_actionbar_img_right);
        rightTxt = rootView.findViewById(R.id.my_actionbar_txt_right);
        contentView = rootView.findViewById(R.id.content_view);
        if (hasBackBtn()) {
            leftImg.setVisibility(View.VISIBLE);
        } else {
            leftImg.setVisibility(View.GONE);
        }
        if (hasRightBtn()) {
            if (isRightImg()) {
                rightImg.setVisibility(View.VISIBLE);
                rightTxt.setVisibility(View.GONE);
            } else {
                rightImg.setVisibility(View.GONE);
                rightTxt.setVisibility(View.GONE);
            }
        } else {
            rightTxt.setVisibility(View.GONE);
            rightImg.setVisibility(View.GONE);
        }
        leftImg.setOnClickListener(this);
        rightImg.setOnClickListener(this);
        rightTxt.setOnClickListener(this);
        dialog = new LoadingDialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
    }

    public void hideActionBar(){
        layout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_actionbar_img_left:
                onLeftBtnClick(view);
                break;
            case R.id.my_actionbar_img_right:
            case R.id.my_actionbar_txt_right:
                onRightBtnClick(view);
                break;

        }
    }

    protected void requestPermission(int id, String permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }

        allowablePermissionRunnables.put(id, allowableRunnable);
        if (disallowableRunnable != null) {
            disallowablePermissionRunnables.put(id, disallowableRunnable);
        }

        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //check if have right
            if(getActivity()!=null&&!getActivity().isFinishing()) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), permission);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    //弹出对话框接收权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, id);
                    return;
                } else {
                    allowableRunnable.run();
                }
            }
        } else {
            allowableRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Runnable allowRun = allowablePermissionRunnables.get(requestCode);
            allowRun.run();
        } else {
            Runnable disallowRun = disallowablePermissionRunnables.get(requestCode);
            disallowRun.run();
        }

    }

    public void showDialog() {
        dialog.show();
    }

    public void hideDialog() {
        dialog.dismiss();
    }
    protected void onLeftBtnClick(View v) {
        getActivity().onBackPressed();
    }

    protected void onRightBtnClick(View v) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_base_bar;
    }

    protected abstract boolean hasBackBtn();

    protected abstract boolean hasRightBtn();

    protected abstract boolean isRightImg();    //true表示显示图片 隐藏文字

    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    public void setTitle(String str) {

        title.setText(str);
    }

    public void setRightImg(int resId) {
        if (hasRightBtn() && isRightImg()) {
            rightImg.setVisibility(View.VISIBLE);
            rightTxt.setVisibility(View.GONE);
            rightImg.setImageResource(resId);
        }
    }

    public void setRightImgColor(int color) {
        rightImg.setColorFilter(color);
    }

    public void setRightTxt(int resId) {
        setRightTxt(getString(resId));
    }

    public void setRightTxt(String str) {
        if (hasRightBtn() && !isRightImg()) {
            rightImg.setVisibility(View.GONE);
            rightTxt.setVisibility(View.VISIBLE);
            rightTxt.setText(str);
        }
    }

    protected void setFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().
                add(R.id.content_view, fragment).commit();
    }

    protected void setView(int layoutId) {
        setView(inflateView(layoutId));
    }

    protected void setView(View view) {
        contentView.addView(view, new FrameLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public ProgressDialog showWaitDialog(String message) {
        if (_isVisiable) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(getActivity(), message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                if (!_waitDialog.isShowing())
                    _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (_isVisiable && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
