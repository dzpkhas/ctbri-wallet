package cn.hyperchain.hitoken.activity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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

import com.bumptech.glide.Glide;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.Center;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.utils.RQcode;
import cn.hyperchain.hitoken.utils.SPHelper;
import cn.hyperchain.hitoken.view.CommonPopupWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.imagecrop.Constants;
import cn.hyperchain.hitoken.imagecrop.FileUtis;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class GatherActivity extends TemplateActivity {

    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;

    @BindView(R.id.iv_copy)
    ImageView ivCopy;

    @BindView(R.id.iv_headimg)
    ImageView ivHeadimg;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_money)
    TextView tvMoney;

    @BindView(R.id.tv_money_unit)
    TextView tvMoneyUnit;

    @BindView(R.id.tv_account)
    TextView tvAccount;


    String type;
    String accountStr;
    private CommonPopupWindow window;

    Bitmap bitmap;

    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
        Intent intent =  getIntent();
        type = intent.getStringExtra("type");
        tvMoneyUnit.setText(type);

        if(type.equals("ETH")) {
            tvAccount.setText((String) SPHelper.get(GatherActivity.this,"ethAddress",""));
        } else if(type.equals("BTC")) {
//            tvAccount.setText((String)SPHelper.get(GatherActivity.this,"btcAddress",""));
            tvAccount.setText("0x");
        }

        String tmpStr = tvAccount.getText().toString();

        if(tmpStr == null || tmpStr.equals("")) {
            accountStr = "0";
        } else {
            accountStr = tmpStr;
        }
        initPopupWindow();
        bitmap = RQcode.getRQcode(accountStr);
        ivQrcode.setImageBitmap(bitmap);

        ivQrcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                long timestamp = System.currentTimeMillis();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String time = formatter.format(new Date());

                String fileName ="/qrcode" + time + timestamp + ".jpg";
                File file = FileUtis.saveBitmap2File(bitmap, Constants.EXTERNAL_STORAGE_DIRECTORY,fileName);
                if (file != null ) {
                    //由文件得到uri
                    Uri imageUri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        imageUri = FileProvider.getUriForFile(getApplicationContext(), "cn.hyperchain.hitoken.fileprovider", file);
                    } else {
                        imageUri = Uri.fromFile(file);
                    }
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "分享地址二维码"));
                }

                return false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        RetrofitUtil.getService().getCenter().enqueue(new Callback<MyResult<Center>>() {
            @Override
            public void onResponse(Response<MyResult<Center>> response, Retrofit retrofit) {

                MyResult result = response.body();
                if(result.getStatusCode() == 200) {
                    Center center = (Center)result.getData();
//                    center.setUser_name("")  ;
                    if((center.getUser_name()).equals("")) {
                        //未创建钱包
                        hideDialog();
                    } else {
                        //已经创建钱包
                        //个人中心赋值
                        if(center.getPortrait()!=null && !center.getPortrait().isEmpty()) {
                            String url = Constants.NETPICTAILAPPHEAD + center.getPortrait();
                            Glide.with(GatherActivity.this).load(url).into(ivHeadimg);
                        }
                        tvName.setText(center.getUser_name());

                    }
                } else {
                    showToast((String)result.getData());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick({
            R.id.ll_back,R.id.iv_copy,R.id.tv_money
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.iv_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", accountStr);
                cm.setPrimaryClip(mClipData);
                showToast("已经复制到剪贴板");
                break;

            case R.id.tv_money:

                WindowManager.LayoutParams lp2=getWindow().getAttributes();
                lp2.alpha=0.3f;
                window.showAtLocation(root, Gravity.CENTER, 0, 0);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getWindow().setAttributes(lp2);
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
        window = new CommonPopupWindow(this, R.layout.popup_transfer_amount, (int) (screenWidth*0.82), (int) (screenHeight*0.42)) {
            @Override
            protected void initView() {
                View view = getContentView();
                final EditText etMoney = view.findViewById(R.id.et_money);
                TextView tvSubmit = view.findViewById(R.id.tv_submit);
                TextView tvCancel = view.findViewById(R.id.tv_cancel);



                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String money = etMoney.getText().toString();
                        if(money.isEmpty()) {
                            showToast("金额不能为空");
                            return;
                        }
                        tvMoney.setText(money);
                        String codeStr = accountStr + "," + money;
                        bitmap = RQcode.getRQcode(codeStr);
                        ivQrcode.setImageBitmap(bitmap);
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

    public File bitMap2File(Bitmap bitmap) {


        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory() + File.separator;//保存到sd根目录下
        }


        //        File f = new File(path, System.currentTimeMillis() + ".jpg");
        File f = new File(path, "share" + ".jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return f;
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
        return R.layout.activity_gather;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
