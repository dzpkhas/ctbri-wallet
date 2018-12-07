package cn.hyperchain.hitoken.activity;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.Wallet;
import cn.hyperchain.hitoken.thread.WalletRefreshThread;
import cn.hyperchain.hitoken.utils.RQcode;
import cn.hyperchain.hitoken.utils.SPHelper;
import cn.hyperchain.hitoken.view.CommonPopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.fragment.HistoryFragment;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailActivity extends TemplateActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.ll_gathering)
    LinearLayout llGathering;

    @BindView(R.id.ll_payment)
    LinearLayout llPayment;

    @BindView(R.id.tv_number)
    TextView tvNumber;

    @BindView(R.id.tv_rmb)
    TextView tvRMB;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.root)
    LinearLayout root;

    String type;

    String accountStr;

    private CommonPopupWindow window;

    WalletRefreshThread thread;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case WalletRefreshThread.SUCCESSFORINDUCTIONCOUNT:
                    ArrayList<Wallet> wallets = (ArrayList<Wallet>) msg.obj;
                    for(int i = 0; i < wallets.size();i++) {
                        Wallet wallet = wallets.get(i);
                        if(wallet.getType().toUpperCase().equals(type)) {
                            tvNumber.setText("" + wallet.getBalance());
                            tvRMB.setText("" + wallet.getCny());
                            break;
                        }
                    }
//                    showToast("detail定时器启动");
                    break;

            }

        }

    };

    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
    }

    private void initThreads() {
        thread = new WalletRefreshThread(handler);
        thread.run();

    }
    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        tvTitle.setText(type);

        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        HistoryFragment historyFragment = new HistoryFragment();
        historyFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content,historyFragment).commit();

        initPopupWindow();
        showDialog();
        RetrofitUtil.getService().getWallet().enqueue(new Callback<MyResult<List<Wallet>>>() {
            @Override
            public void onResponse(Response<MyResult<List<Wallet>>> response, Retrofit retrofit) {
                hideDialog();
                MyResult myResult = response.body();
                if(myResult.getStatusCode() == 200) {
                    ArrayList<Wallet> wallets = (ArrayList<Wallet>) myResult.getData();
                    for(int i = 0; i < wallets.size();i++) {
                        Wallet wallet = wallets.get(i);
                        if(wallet.getType().toUpperCase().equals(type)) {
                            tvNumber.setText("" + wallet.getBalance());
                            tvRMB.setText("" + wallet.getCny());
                            break;
                        }
                    }
                } else {
                    showToast((String) myResult.getData());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                hideDialog();
                Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @OnClick({
            R.id.ll_gathering,R.id.ll_payment,R.id.ll_back
    })
    @Override
    public void onClick(View v) {
        Intent intent;
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_gathering:
//                window.showAtLocation(root, Gravity.CENTER, 0, 0);
//                WindowManager.LayoutParams lp=getWindow().getAttributes();
//                lp.alpha=0.3f;
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                getWindow().setAttributes(lp);
                intent = new Intent(DetailActivity.this,GatherActivity.class);
                intent.putExtra("type",type);
                startActivity(intent);
                break;
            case R.id.ll_payment:
                if(type.equals("ETH")) {
                    intent = new Intent(DetailActivity.this,SendActivity.class);
                    intent.putExtra("type",type);
                    startActivity(intent);
                } else {
                    showToast("btc 暂不支持");
                }

                break;

            case R.id.ll_back:
                finish();
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
        window = new CommonPopupWindow(this, R.layout.popup_qrcode, (int) (screenWidth*0.73), (int) (screenHeight*0.7)) {
            @Override
            protected void initView() {
                View view = getContentView();
                ImageView ivCopy = view.findViewById(R.id.iv_copy);
                ImageView ivQRcode = view.findViewById(R.id.iv_qrcode);
                CircleImageView ivHeadimg = view.findViewById(R.id.iv_headimg);
                TextView tvName = view.findViewById(R.id.tv_name);
                TextView tvAccount = view.findViewById(R.id.tv_account);
                if(type.equals("ETH")) {
                    tvAccount.setText((String)SPHelper.get(DetailActivity.this,"ethAddress",""));
                } else if(type.equals("BTC")) {
                    tvAccount.setText((String)SPHelper.get(DetailActivity.this,"btcAddress",""));
                }

                String tmpStr = tvAccount.getText().toString();

                if(tmpStr == null || tmpStr.equals("")) {
                    accountStr = "0";
                } else {
                    accountStr = tmpStr;
                }
                ivQRcode.setImageBitmap(RQcode.getRQcode(accountStr));

                ivCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", accountStr);
                        cm.setPrimaryClip(mClipData);
                        showToast("已经复制到剪贴板");

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

    @Override
    protected void onResume() {
        super.onResume();
        initThreads();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(thread);
//        showToast("detail thead 销毁");
    }
    @Override
    protected SHOW_TYPE showBackBtn() {
        return SHOW_TYPE.BLANK;
    }

    @Override
    protected SHOW_TYPE showRightBtn() {
        return SHOW_TYPE.BLANK;

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
