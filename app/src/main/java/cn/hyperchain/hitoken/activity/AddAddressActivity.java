package cn.hyperchain.hitoken.activity;


import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.post.AddressBody;
import cn.hyperchain.hitoken.entity.post.EditAddressBody;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class AddAddressActivity extends TemplateActivity {

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.ll_finish)
    LinearLayout llFinish;

    @BindView(R.id.et_nickname)
    EditText etNickname;

    @BindView(R.id.et_eth_address)
    EditText etEthAddress;

    @BindView(R.id.et_btc_address)
    EditText etBtcAddress;

    //from = 0 表示 新增， from = 1 表示编辑
    int from = 0;
    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
        Intent intent = getIntent();
        from = intent.getIntExtra("from",0);
        if(from == 1) {
            etNickname.setText(intent.getStringExtra("nickname"));
            etEthAddress.setText(intent.getStringExtra("ethAddress"));
            etBtcAddress.setText(intent.getStringExtra("btcAddress"));
        }
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @OnClick({
            R.id.ll_back,R.id.ll_finish
    })
    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;

            case R.id.ll_finish:
                String nickname = etNickname.getText().toString();
                String ethAddress = etEthAddress.getText().toString();
                String btcAddress = etBtcAddress.getText().toString();

                if(nickname.isEmpty()) {
                    showToast("昵称不能为空");
                    return;
                }

                if(ethAddress.isEmpty() && btcAddress.isEmpty()) {
                    showToast("eth 和 btc 至少需要一个地址");
                    return;
                }

                if(from == 0) {
                    AddressBody addressBody = new AddressBody();
                    addressBody.setBtc_address(btcAddress);
                    addressBody.setEth_address(ethAddress);
                    addressBody.setUser_name(nickname);

                    Gson gson = new Gson();
                    String json = gson.toJson(addressBody);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

                    RetrofitUtil.getService().addAddress(body).enqueue(new Callback<MyResult>() {
                        @Override
                        public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                            hideDialog();
                            MyResult result = response.body();
                            if(result.getStatusCode() == 200) {
                                showToast("添加联系人成功");
                                finish();
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
                } else if(from == 1) {
                    EditAddressBody addressBody = new EditAddressBody();
                    intent = getIntent();
                    addressBody.setId(intent.getIntExtra("id",0));
                    addressBody.setBtc_address(btcAddress);
                    addressBody.setEth_address(ethAddress);
                    addressBody.setUser_name(nickname);

                    Gson gson = new Gson();
                    String json = gson.toJson(addressBody);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

                    RetrofitUtil.getService().editAddress(body).enqueue(new Callback<MyResult>() {
                        @Override
                        public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                            hideDialog();
                            MyResult result = response.body();
                            if(result.getStatusCode() == 200) {
                                showToast("修改联系人成功");
                                finish();
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
                }

                break;
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
        return R.layout.activity_add_address;
    }


    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

}
