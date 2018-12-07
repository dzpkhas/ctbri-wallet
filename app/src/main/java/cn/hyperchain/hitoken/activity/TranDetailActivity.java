package cn.hyperchain.hitoken.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.TranDetail;
import cn.hyperchain.hitoken.utils.DateUtils;
import cn.hyperchain.hitoken.utils.RQcode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.imagecrop.Constants;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class TranDetailActivity extends TemplateActivity {


    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.tv_tran_no)
    TextView tvTranNo;

    @BindView(R.id.tv_tran_time)
    TextView tvTranTime;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.iv_headimg)
    ImageView ivHeadImg;

    @BindView(R.id.tv_status)
    TextView tvStatus;

    @BindView(R.id.tv_tran_money)
    TextView tvTranMoney;

    @BindView(R.id.tv_gas_price)
    TextView tvGasPrice;

    @BindView(R.id.iv_tran_type)
    ImageView ivTranType;

    @BindView(R.id.tv_desp)
    TextView tvDesp;

    @BindView(R.id.tv_to_address)
    TextView tvToAddress;

    @BindView(R.id.tv_from_address)
    TextView tvFromAddress;

    @BindView(R.id.tv_block_height)
    TextView tvBlockHeight;

    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;

    @BindView(R.id.ll_copy)
    LinearLayout llCopy;

    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
    }

    @Override
    public void initData() {
        super.initData();
        Intent intent = getIntent();
        long id = intent.getLongExtra("id",0);
        final int tranType = intent.getIntExtra("tranType",0);
        showDialog();
        RetrofitUtil.getService().tranDetail(id).enqueue(new Callback<MyResult<TranDetail>>() {
            @Override
            public void onResponse(Response<MyResult<TranDetail>> response, Retrofit retrofit) {

                hideDialog();
                MyResult result = response.body();

                if(result.getStatusCode() == 200) {
                    TranDetail tranDetail = (TranDetail)result.getData();
                    tvTranNo.setText(tranDetail.getTx_hash());
                    tvTranTime.setText(DateUtils.date1(Long.valueOf(tranDetail.getTime_stamp())));
                    tvName.setText(tranDetail.getUser_name());
                    if(!TextUtils.isEmpty(tranDetail.getPortrait())) {
                        String url = Constants.NETPICTAILAPPHEAD + tranDetail.getPortrait();
                        Glide.with(TranDetailActivity.this).load(url).into(ivHeadImg);
                    }
                    tvStatus.setText(tranDetail.getStatus());
                    tvTranMoney.setText(tranDetail.getValue());
                    if(tranType == 1) {
                        ivTranType.setImageResource(R.mipmap.tran_income);
                    } else {
                        ivTranType.setImageResource(R.mipmap.tran_send);
                    }
                    tvGasPrice.setText(tranDetail.getGas_price());
                    tvDesp.setText(tranDetail.getRemark());
                    tvToAddress.setText(tranDetail.getTo());
                    tvFromAddress.setText(tranDetail.getFrom());
                    tvBlockHeight.setText(tranDetail.getBlock_height());
                    Bitmap bitmap = RQcode.getRQcode(tranDetail.getTx_hash());
                    ivQrcode.setImageBitmap(bitmap);
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
            R.id.ll_back,R.id.ll_copy
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.ll_copy:
                String tranNo = tvTranNo.getText().toString();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", tranNo);
                cm.setPrimaryClip(mClipData);
                showToast("已经复制到剪贴板");
                break;
        }
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
        return R.layout.activity_tran_detail;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}