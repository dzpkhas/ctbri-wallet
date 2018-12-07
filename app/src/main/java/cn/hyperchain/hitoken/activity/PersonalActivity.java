package cn.hyperchain.hitoken.activity;


import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.post.Nickname;
import cn.hyperchain.hitoken.utils.DataUtils;
import cn.hyperchain.hitoken.utils.SPHelper;
import cn.hyperchain.hitoken.view.CommonPopupWindow;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.imagecrop.Constants;
import cn.hyperchain.hitoken.imagecrop.CropBorderOption;
import cn.hyperchain.hitoken.imagecrop.CropBorderView;
import cn.hyperchain.hitoken.imagecrop.ImageCropFragment;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PersonalActivity extends TemplateActivity {

    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.ll_nickname)
    LinearLayout llNickname;

    @BindView(R.id.ll_headimg)
    LinearLayout llHeadimg;

    @BindView(R.id.iv_headimg)
    ImageView ivHeadImg;

    private CommonPopupWindow window;

    enum CROPTYPE {
        HEAD_IMG   //头像
    }

    private int currentType;
    CROPTYPE currentCropType;//当前裁剪的对象
    String urlCard1 = "";


    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
        initPopupWindow();
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if(url != null && !url.isEmpty()) {
            Glide.with(PersonalActivity.this).load(url).into(ivHeadImg);
        }
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @OnClick({
            R.id.ll_back,R.id.ll_nickname,R.id.ll_headimg
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_nickname:
                WindowManager.LayoutParams lp2=getWindow().getAttributes();
                lp2.alpha=0.3f;
                window.showAtLocation(root, Gravity.CENTER, 0, 0);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp2);
                break;
            case R.id.ll_headimg:
                showActionSheetSelectUploadIdentityCard(CROPTYPE.HEAD_IMG);
                break;
        }
    }


    private void initPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight=metrics.heightPixels;
        int screenWidth=metrics.widthPixels;
        // create popup window
        window = new CommonPopupWindow(this, R.layout.popup_update_nickname, DataUtils.dip2px(PersonalActivity.this,280),
                DataUtils.dip2px(PersonalActivity.this,170)) {
            @Override
            protected void initView() {
                View view = getContentView();
                final EditText etNickname = view.findViewById(R.id.et_nickname);
                TextView tvSubmit = view.findViewById(R.id.tv_submit);
                TextView tvCancel = view.findViewById(R.id.tv_cancel);



                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nickname = etNickname.getText().toString();
                        if(nickname.isEmpty()) {
                            showToast("新昵称不能为空");
                            return;
                        }

                        Nickname nicknameBody = new Nickname();
                        nicknameBody.setNew_name(nickname);

                        Gson gson = new Gson();
                        String json = gson.toJson(nicknameBody);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

                        RetrofitUtil.getService().updateNickname(body).enqueue(new Callback<MyResult>() {
                            @Override
                            public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                                hideDialog();
                                MyResult result = response.body();
                                if(result.getStatusCode() == 200) {
                                    showToast("修改昵称成功");
                                } else {
                                    showToast((String)result.getData());
                                }

                            }

                            @Override
                            public void onFailure(Throwable t) {
                                hideDialog();
                                Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                        getPopupWindow().dismiss();

                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPopupWindow().dismiss();
                    }
                });

            }

            @Override
            protected void initEvent() {

            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance=getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        WindowManager.LayoutParams lp=getWindow().getAttributes();
                        lp.alpha=1.0f;
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getWindow().setAttributes(lp);

                    }
                });
            }
        };
    }

    private void showActionSheetSelectUploadIdentityCard(final CROPTYPE cropType) {
        setTheme(R.style.ActionSheetStyleiOS7);
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消")
                .setOtherButtonTitles("拍照", "从相册选择")
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        currentCropType = cropType;
                        SPHelper.put(PersonalActivity.this, "currentCropType2", cropType.ordinal());
                        CropBorderView.borderOption = CropBorderOption.ONE2ONE;
                        if (index == 0) {
                            requestPermission(1, Manifest.permission.CAMERA, new Runnable() {
                                @Override
                                public void run() {
                                    ImageCropFragment.startTakePhoto(PersonalActivity.this);
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        } else if (index == 1) {
                            requestPermission(1, Manifest.permission.READ_EXTERNAL_STORAGE, new Runnable() {
                                @Override
                                public void run() {
                                    ImageCropFragment.startPickPhoto(PersonalActivity.this);
                                }
                            }, new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    }
                }).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.TAKE_PHOTO:
                    //相机拍摄照片
                    ImageCropFragment.startCropAct(PersonalActivity.this, Constants.PHOTONAME);
                    break;
                case Constants.PICK_PHOTO:
                    //从相册选择
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        ImageCropFragment.startCropAct(PersonalActivity.this, picturePath);
                    }
                    break;
                case Constants.CROP_IMG:
                    urlCard1 = data.getStringExtra("imageUrl");
                    Glide.with(this).load(urlCard1).into(ivHeadImg);
                    break;

            }
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
        return R.layout.activity_personal;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
