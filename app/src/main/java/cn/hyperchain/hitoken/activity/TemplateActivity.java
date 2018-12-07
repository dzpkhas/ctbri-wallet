package cn.hyperchain.hitoken.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.utils.LoadingDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by yanceywang on 3/27/16.
 */
public abstract class TemplateActivity extends BaseActivity {


    //具体页面容器
    FrameLayout contentView;

    //template 头
    @BindView(R.id.v_status_bar)
    View vStatusBar;


    //template 头
    @BindView(R.id.templet_header)
    RelativeLayout header;

    //template 中间文字
    @BindView(R.id.templet_header_title)
    TextView tvTitle;

    //整个左边框
    @BindView(R.id.templet_header_linear_left)
    LinearLayout linearViewLeft;

    //template 左边图标
    @BindView(R.id.templet_header_img_left)
    ImageView imgViewLeft;

    //template 左边文字
    @BindView(R.id.templet_header_text_left)
    TextView textViewLeft;

    //整个右边框
    @BindView(R.id.templet_header_linear_right)
    LinearLayout linearViewRight;

    //template 左边图标
    @BindView(R.id.templet_header_img_right)
    ImageView imgViewRight;

    //template 左边文字
    @BindView(R.id.templet_header_text_right)
    TextView textViewRight;

    @BindView(R.id.progressbar_holder)
    FrameLayout progressHolder;

    Fragment fragment;
    LoadingDialog dialog;

    public enum SHOW_TYPE {
        BLANK, SHOW_IMG, SHOW_TEXT, SHOW_ALL
    }

    protected Unbinder mBinder;

    @Override
    protected void init(Bundle savedInstanceState) {
        contentView = (FrameLayout) findViewById(R.id.templet_content_view);
        setView();
    }

    @Override
    protected void initView() {
        super.initView();
        if (fullScreen()) {
            header.setVisibility(View.GONE);
        }

        switch (showBackBtn()) {
            case BLANK:
                linearViewLeft.setVisibility(View.GONE);
                break;
            case SHOW_IMG:
                linearViewLeft.setVisibility(View.VISIBLE);
                imgViewLeft.setVisibility(View.VISIBLE);
                textViewLeft.setVisibility(View.GONE);
                break;
            case SHOW_TEXT:
                linearViewLeft.setVisibility(View.VISIBLE);
                imgViewLeft.setVisibility(View.GONE);
                textViewLeft.setVisibility(View.VISIBLE);
                break;
            case SHOW_ALL:
                linearViewLeft.setVisibility(View.VISIBLE);
                imgViewLeft.setVisibility(View.VISIBLE);
                textViewLeft.setVisibility(View.VISIBLE);
                break;
        }
        switch (showRightBtn()) {
            case BLANK:
                linearViewRight.setVisibility(View.GONE);
                break;
            case SHOW_IMG:
                linearViewRight.setVisibility(View.VISIBLE);
                imgViewRight.setVisibility(View.VISIBLE);
                textViewRight.setVisibility(View.GONE);
                break;
            case SHOW_TEXT:
                linearViewRight.setVisibility(View.VISIBLE);
                imgViewRight.setVisibility(View.GONE);
                textViewRight.setVisibility(View.VISIBLE);
                break;
            case SHOW_ALL:
                linearViewRight.setVisibility(View.VISIBLE);
                imgViewRight.setVisibility(View.VISIBLE);
                textViewRight.setVisibility(View.VISIBLE);
                break;
        }
        dialog = new LoadingDialog(this);
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_template;
    }

    @OnClick({
            R.id.templet_header_linear_left,R.id.templet_header_linear_right
    })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.templet_header_linear_left:
                onTitleLeftBtnClick(v);
                break;
            case R.id.templet_header_linear_right:
                onTitleRightBtnClick(v);
                break;
        }
    }
    protected void onTitleLeftBtnClick(View v) {
        onBackPressed();
    }

    protected void onTitleRightBtnClick(View v) {

    }

    public final void setLeftImg(int resId) {
        imgViewLeft.setVisibility(View.VISIBLE);
        imgViewLeft.setImageResource(resId);

    }

    public final void setLeftText(String text) {
        textViewLeft.setVisibility(View.VISIBLE);
        textViewLeft.setText(text);
    }

    public final void setLeftText(String text,int color) {
        textViewLeft.setVisibility(View.VISIBLE);
        textViewLeft.setText(text);
        textViewLeft.setTextColor(color);
    }

    public final void setRightImg(int resId) {
        imgViewRight.setVisibility(View.VISIBLE);
        imgViewRight.setImageResource(resId);

    }

    public final void setRightText(String text) {
        textViewRight.setVisibility(View.VISIBLE);
        textViewRight.setText(text);
    }

    public final void setRightText(String text,int color) {
        textViewRight.setVisibility(View.VISIBLE);
        textViewRight.setText(text);
        textViewRight.setTextColor(color);
    }

    public final void setTitle(int resId) {
        setTitle(getString(resId));
    }

    public final void setTitle(String title) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    public final void setTitle(String title,int colorRes) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
        tvTitle.setTextColor(getResources().getColor(colorRes));
    }

    public final void setTitleBackground(int color) {
        header.setBackgroundColor(color);
    }

    public final void showTitleBar() {
        header.setVisibility(View.VISIBLE);
    }

    public final void hideTitleBar() {
        vStatusBar.setVisibility(View.GONE);
        header.setVisibility(View.GONE);
    }

    public final void setTitleBarColor(int colorRes) {
        header.setBackgroundColor(getResources().getColor(colorRes));
    }

    protected abstract SHOW_TYPE showBackBtn();

    protected abstract SHOW_TYPE showRightBtn();

    protected abstract int getLayout();


    protected final void setView() {
        View view = inflateView(getLayout());
        contentView.setVisibility(View.VISIBLE);
        contentView.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mBinder = ButterKnife.bind(this);
    }

    protected void setFragment(@Nullable Fragment fragment) {
        this.fragment = fragment;
        getSupportFragmentManager().beginTransaction().add(R.id.templet_content_view, fragment).commitAllowingStateLoss();
    }

    public void hideProgress() {
        progressHolder.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

    //set View 之前
    public void showProgress() {
        if (progressHolder != null) {
            progressHolder.bringToFront();
            progressHolder.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
        }
    }

    public void showDialog() {
        dialog.show();
    }

    public void hideDialog() {
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        mBinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
