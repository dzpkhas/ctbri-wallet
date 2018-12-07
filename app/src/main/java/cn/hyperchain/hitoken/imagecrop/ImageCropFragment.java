package cn.hyperchain.hitoken.imagecrop;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;
import cn.hyperchain.hitoken.utils.ImageUtils;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static android.app.Activity.RESULT_OK;

/**
 * Created by admin on 2017/11/15.
 */

public class ImageCropFragment extends BaseBarFragment {

    CropImageView cropimageview;

    private Bundle b;

    private Bitmap mBitmap;
    private Bitmap mScaleBitmap;
    private Bitmap rotabm;
    private String mPhotoPath;
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
    private String imgPath;

    LinearLayout llBack;

    TextView tvTailor;



    //上传图片
    int taskSize = 0;
    int finishedSize = 0;
    double per;
    String imgUrl="";
    File file;
    byte[] data;

    Handler myHandler = new Handler();

    //上传图片显示进度
    private ProgressDialog mProgressDialog;
    Runnable percentRun = new Runnable() {
        @Override
        public void run() {
            changeProgress((int) (((per) / taskSize)*100));
            myHandler.postDelayed(this, 100);
        }
    };

    private void changeProgress(int percent) {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setProgress(percent);
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("正在上传图片...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
        mProgressDialog.show();
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);

        setView(R.layout.fragment_image_crop);
        hideActionBar();
        cropimageview = rootView.findViewById(R.id.cropimageview);

        ((ImageCropActivity)getActivity()).requestPermission(1, Manifest.permission.READ_EXTERNAL_STORAGE, new Runnable() {
            @Override
            public void run() {
                b = getActivity().getIntent().getExtras();
                if (b != null) {
                    mPhotoPath = b.getString("PHOTO_PATH");
                }
                if (!TextUtils.isEmpty(mPhotoPath)) {
                    try {
                        mBitmap = ImageUtils.getScaleBitmap(getActivity(),mPhotoPath);
                        mScaleBitmap = FileUtis.scaleBitmap(mBitmap, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
                        mBitmap.recycle();
                        cropimageview.setImageBitmap(mScaleBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                //showToast("没有权限");
                getActivity().finish();
            }
        });

        llBack = (LinearLayout)rootView.findViewById(R.id.ll_back);
        tvTailor = (TextView)rootView.findViewById(R.id.tv_tailor);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        tvTailor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWaitDialog("正在裁剪并上传...").show();
                saveFileToLocal();
            }
        });


    }

    @Override
    protected boolean hasBackBtn() {
        return true;
    }

    @Override
    protected boolean hasRightBtn() {
        return true;
    }

    @Override
    protected boolean isRightImg() {
        return false;
    }



    @Override
    protected void onRightBtnClick(View v) {
        super.onRightBtnClick(v);

    }

    private String fileName;

    private void saveFileToLocal() {

        long timestamp = System.currentTimeMillis();
        String time = formatter.format(new Date());

        fileName ="/cropped" + time + timestamp + ".jpg";

        Bitmap bm = cropimageview.getCropImage();

        data = ImageUtils.convertBitMapToByteArray(bm);

        //保存图片到本地
        file =  FileUtis.saveBitmap2File(bm, Constants.DIRPATH,fileName);


        if (data != null&&data.length!=0) {
            fileName ="cropped" + time + timestamp + ".jpg";
            RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM).
                    addFormDataPart("portrait", fileName, RequestBody.create(MediaType.parse("image/png"),
                            file)).build();

//
//            RequestBody requestBody = new MultipartBuilder().
//                    addFormDataPart(Headers.of("Content-Disposition", "form-data; name=\"portrait\";filename=\"file.jpg\""), RequestBody.create(MediaType.parse("image/png"), new File(Constants.DIRPATH+fileName))).
//                    build();

            RetrofitUtil.getService().postImage(requestBody).enqueue(new Callback<MyResult>() {
                @Override
                public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                    hideWaitDialog();
                    MyResult result = response.body();
                    if (result.getStatusCode() == 200) {

                        String url = (String)result.getData();
                        showToast("头像上传成功");
                        Intent intent = getActivity().getIntent();
                        intent.putExtra("imageUrl",Constants.NETPICTAILAPPHEAD + url);
                        getActivity().setResult(RESULT_OK, intent);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(),"上传失败", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    hideWaitDialog();
                }
            });

        } else {
            Toast.makeText(getActivity(),"文件不存在", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        /*if(taskSize!=0){
            showProgressDialog();
            myHandler.post(percentRun);
        }else{
            Intent intent = getIntent();
            intent.putExtra("path", imgUrl);
            if(file!=null) {
                intent.putExtra("localpath", file.getAbsolutePath());
            }
            setResult(RESULT_OK, intent);*/
            //hideWaitDialog();


       // }

    }


    public static void startCropAct(Activity activity, String picPath) {
        Intent intent = new Intent(activity, ImageCropActivity.class);
        intent.putExtra("PHOTO_PATH", picPath);
        activity.startActivityForResult(intent,Constants.CROP_IMG);
    }


    public static void startTakePhoto(Activity act) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //下面这句指定调用相机拍照后的照片存储的路径
        File file= new File(Constants.PHOTONAME);
        Uri imgUri;
        if (Build.VERSION.SDK_INT >= 24) {
            imgUri = FileProvider.getUriForFile(act.getApplicationContext(), "cn.hyperchain.hitoken.fileprovider", file);
        } else {
            imgUri = Uri.fromFile(file);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        act.startActivityForResult(intent, Constants.TAKE_PHOTO);

    }

    public static void startPickPhoto(Activity act){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        act.startActivityForResult(intent, Constants.PICK_PHOTO);
    }
}
