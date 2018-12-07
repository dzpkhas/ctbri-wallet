package cn.hyperchain.hitoken.activity;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.ble.Ble;
import cn.hyperchain.hitoken.ble.Util;
import cn.hyperchain.hitoken.ble.crypto.Hash;
import cn.hyperchain.hitoken.ble.key.ECDSASignature;
import cn.hyperchain.hitoken.ble.key.RawTransaction;
import cn.hyperchain.hitoken.ble.key.Sign;
import cn.hyperchain.hitoken.ble.key.TransactionEncoder;
import cn.hyperchain.hitoken.entity.Gas;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.Wallet;
import cn.hyperchain.hitoken.entity.post.Transaction;
import cn.hyperchain.hitoken.entity.post.TxBody;
import cn.hyperchain.hitoken.utils.DataUtils;
import cn.hyperchain.hitoken.utils.NumberUtil;
import cn.hyperchain.hitoken.utils.SPHelper;
import cn.hyperchain.hitoken.view.CommonPopupWindow;
import cn.hyperchain.hitoken.view.PayPsdInputView;
import com.rthtech.ble.Controller;
import com.rthtech.ble.Data;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.hyperchain.hitoken.google.zxing.activity.CaptureActivity;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SendActivity extends TemplateActivity implements com.rthtech.ble.Callback {

    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.ll_back)
    LinearLayout llBack;

    @BindView(R.id.iv_scan)
    LinearLayout ivScan;

    @BindView(R.id.ll_address_book)
    LinearLayout llAddressBook;


    @BindView(R.id.iv_address_book)
    ImageView ivAddressBook;

    @BindView(R.id.tv_money_unit)
    TextView tvMoneyUnit;

    @BindView(R.id.tv_service_unit)
    TextView tvServiceUnit;

    @BindView(R.id.et_account)
    EditText etAccount;

    @BindView(R.id.et_money)
    EditText etMoney;

    @BindView(R.id.tv_service_charge)
    TextView tvServiceCharge;

    @BindView(R.id.tv_send)
    TextView tvSend;

    @BindView(R.id.sb_service_charge)
    SeekBar sbServiceCharge;



    String type;

    private final static int MUL = 1000000;

    private Gas gas;

    private long gasl;

    private Controller mController = null;
    private Ble ble;

    public static final byte CHINAID = 0x03;

    RawTransaction rawTransaction;

    byte[] hash;

    CommonPopupWindow window;

    String accountId;
    TxBody txBody = new TxBody();
    Transaction transaction = new Transaction();

    private int cardTimes = 0;

    // 0 未寻卡 1 已寻卡
    private int findCard = 0;

    public final static int TRAN_TIMES = 10;
    private int tranTimes = 0;
    @Override
    public void initView() {
        super.initView();
        hideTitleBar();
        Intent intent =  getIntent();
        type = intent.getStringExtra("type");
        tvMoneyUnit.setText(type);
        tvServiceUnit.setText(type);

        etAccount.setCursorVisible(false);
        etAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAccount.setCursorVisible(true);
            }
        });
        accountId = (String)SPHelper.get(SendActivity.this,"ethAccountId","");
        mController = com.rthtech.ble.Factory.getController(SendActivity.this);
        mController.init(this);
        if (!mController.isBluetoothEnabled()) {
            mController.term();
            showToast("蓝牙未打开，请打开蓝牙后再操作");
            finish();
        } else {
            ble = new Ble(mController);
            String bluetooth = (String)SPHelper.get(SendActivity.this,"bluetooth","");
            if(bluetooth.isEmpty()) {
                showToast("蓝牙地址未找到");
                finish();
            }
            ble.conect(bluetooth);
            showToast("设备连接中...");
            showDialog();
            RetrofitUtil.getService().getGas().enqueue(new Callback<MyResult<Gas>>() {
                @Override
                public void onResponse(Response<MyResult<Gas>> response, Retrofit retrofit) {
                    MyResult myResult = response.body();
                    if(myResult.getStatusCode() == 200) {
                        gas = (Gas) myResult.getData();
                        final int max = (int)(gas.getGas_max() / MUL);
                        final int min = (int)(gas.getGas_min() / MUL);
                        final long gasLimit = gas.getGas_limit();
                        final int def = (int)(gas.getGas() / MUL);
                        sbServiceCharge.setMax(max);
                        sbServiceCharge.setProgress(def);
                        double defGas = 1.0 * gas.getGas() * gas.getGas_limit() / 1e18;
                        DecimalFormat df = new DecimalFormat("#0.############");
                        tvServiceCharge.setText(df.format(defGas));
                        gasl = gas.getGas() * gas.getGas_limit();

                        sbServiceCharge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                double gas = 1.0 * (long)(i + min) * MUL * gasLimit / 1e18;
                                Log.d("process i", "" + i);
                                DecimalFormat df = new DecimalFormat("#0.############");
                                tvServiceCharge.setText(df.format(gas));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                double gas = 1.0 * (long)(seekBar.getProgress() + min) * MUL * gasLimit / 1e18;
                                Log.d("process", "" + seekBar.getProgress());
                                DecimalFormat df = new DecimalFormat("#0.############");
                                tvServiceCharge.setText(df.format(gas));
                                gasl = (long)(seekBar.getProgress() + min) * MUL * gasLimit;
                            }
                        });

                    } else {
                        showToast((String) myResult.getData());
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    @Override
    public void initData() {
        super.initData();
    }

    private CommonPopupWindow initTranPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight=metrics.heightPixels;
        int screenWidth=metrics.widthPixels;
        // create popup window
        window = new CommonPopupWindow(this, R.layout.popup_tran_password, (int) (screenWidth * 0.9), DataUtils.dip2px(SendActivity.this,154)) {
            @Override
            protected void initView() {
                View view = getContentView();
                final PayPsdInputView  passwordInputView = (PayPsdInputView) view.findViewById(R.id.password);
                ImageView ivCancel = view.findViewById(R.id.iv_cancel);
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        window.getPopupWindow().dismiss();
                    }
                });
                passwordInputView.setComparePassword( new PayPsdInputView.onPasswordListener() {

                    @Override
                    public void onDifference(String oldPsd, String newPsd) {
                    }

                    @Override
                    public void onEqual(String psd) {
                    }

                    @Override
                    public void inputFinished(String inputPsd) {
                        window.getPopupWindow().dismiss();
                        final String password = inputPsd;
                        RetrofitUtil.getService().getWallet().enqueue(new Callback<MyResult<List<Wallet>>>() {
                            @Override
                            public void onResponse(Response<MyResult<List<Wallet>>> response, Retrofit retrofit) {
                                hideDialog();
                                MyResult result = response.body();

                                if(result.getStatusCode() == 200) {
                                    ArrayList<Wallet> wallets = (ArrayList<Wallet>)result.getData();
                                    for(int i = 0; i < wallets.size();i++) {
                                        Wallet wallet = wallets.get(i);
                                        if(wallet.getType().toUpperCase().equals("ETH")) {
                                            String from = wallet.getAddress();
                                            long nonce = wallet.getNonce();
                                            String to = etAccount.getText().toString();
                                            String moneyStr = etMoney.getText().toString();
//                                            to = "0x33a6464e3750d90c76c8a40e2d3c4746250af159";

//                                                long mValue = (long)(Double.valueOf(money) * 1e18);

                                            BigInteger big = new BigInteger("1000000000000000000");
                                            double money = Double.valueOf(moneyStr);
                                            if(money > wallet.getBalance()) {
                                                showToast("转账金额不能大于钱包余额");
                                                window.getPopupWindow().dismiss();
                                                return;
                                            }

                                            BigInteger bValue = BigInteger.valueOf((long)(money * 1e18));
                                            Log.d("bValue",bValue.toString());
                                            if(type.equals("ETH")) {
                                                rawTransaction = RawTransaction.createEtherTransaction(BigInteger.valueOf(nonce),BigInteger.valueOf(2000000000),
                                                        BigInteger.valueOf(gas.getGas_limit()),to,bValue);

                                                Log.d("raw to",rawTransaction.getTo());
                                                Log.d("raw gas limit","" + rawTransaction.getGasLimit());
                                                Log.d("raw gas price","" + rawTransaction.getGasPrice());
                                                Log.d("raw nonce","" + rawTransaction.getNonce());
                                                Log.d("raw value","" + rawTransaction.getValue());
                                                Log.d("raw data","" + rawTransaction.getData());

                                                byte[] encodedtransaction = TransactionEncoder.encode(rawTransaction);
                                                Log.d("hash1",NumberUtil.bytesToHexString(encodedtransaction));


                                                hash = Hash.sha3(encodedtransaction);
                                                Log.d("hash2",NumberUtil.bytesToHexString(hash));

                                                showDialog();
                                                tranTimes = 0;
                                                ble.getPublicKey("3C",accountId);

                                                txBody.setData("0x");
                                                txBody.setFees("");
                                                txBody.setFrom(from);
                                                txBody.setGas_limit(String.valueOf(gas.getGas_limit()));
                                                txBody.setGas_price(String.valueOf(gasl));
                                                txBody.setNonce(String.valueOf(nonce));
                                                txBody.setTo(to);
                                                txBody.setValue(bValue.toString());

                                                transaction.setTx_body(txBody);
                                                transaction.setType(type);
                                                transaction.setPay_word(password);
                                                Log.d("hash2","-------------------");
                                            }

                                        }
                                    }
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
        return window;
    }
    @OnClick({
            R.id.iv_scan,R.id.ll_back,R.id.tv_send,R.id.ll_address_book
    })

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent;

        switch (v.getId()) {

            case R.id.iv_scan:
                requestPermission(1, Manifest.permission.CAMERA, new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SendActivity.this, CaptureActivity.class);
                        intent.putExtra("from",1);
                        startActivityForResult(intent,10);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        showToast("没有权限");
                    }
                });
                break;
            case R.id.ll_address_book:
                intent = new Intent(SendActivity.this,AddressBookActivity.class);
                if(type.equals("ETH")) {
                    intent.putExtra("from",1);
                } else {
                    intent.putExtra("from",2);
                }

                startActivityForResult(intent,100);
                break;

            case R.id.ll_back:
                finish();
                break;


            case R.id.tv_send:
                if(findCard == 1) {
                    String to = etAccount.getText().toString();
                    String money = etMoney.getText().toString();
                    if(to.isEmpty()) {
                        showToast("收款地址不能为空");
                        return;
                    }
                    if(money.isEmpty()) {
                        showToast("转账金额不能为空");
                        return;
                    }
                    initTranPopupWindow();
                    WindowManager.LayoutParams lp2=getWindow().getAttributes();
                    lp2.alpha=0.3f;
                    DisplayMetrics metrics=new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    int screenHeight=metrics.heightPixels;
                    window.showAtLocation(root, Gravity.TOP, 0, (int)(screenHeight*0.2));
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    getWindow().setAttributes(lp2);
                    window.getPopupWindow().setFocusable(true);
                } else if (findCard == 0) {
                    showToast("发起重新寻卡...");
                    showDialog();
                    cardTimes = 0;
                    mController.selectCard();
                }

                break;


        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            if(scanResult.contains(",")) {
                String[] temps = scanResult.split(",");
                etAccount.setText(temps[0]);
                etMoney.setText(temps[1]);
            } else {
                etAccount.setText(scanResult);
            }

        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mController != null) {
            mController.term();
        }

    }

    @Override
    protected SHOW_TYPE showBackBtn() {
        return SHOW_TYPE.SHOW_ALL;
    }

    @Override
    protected SHOW_TYPE showRightBtn() {
        return SHOW_TYPE.BLANK;

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_send;
    }

    @Override
    protected boolean isFillStatusBar() {
        return true;
    }

    @Override
    public void onFoundDevice(String s, String s1) {

    }

    @Override
    public void onStateChange(int old_state, int new_state, int error) {
        if (new_state == Data.STATE_SCANNING) {
            ble.log("scanning device...");
        } else if (new_state == Data.STATE_CONNECTING_DEVICE) {
            ble.log("conn: " + mController.getDeviceName());
            ble.log("addr: " + mController.getDeviceAddress());
        } else if (new_state == Data.STATE_CONNECTING_SERVICE) {
        } else if (new_state == Data.STATE_END) {
            if (ble.mScanMode) {
                ble.log("scanning stop!");
                ble.mScanMode = false;
            } else {
                ble.log("disconnected! code=" + ble.errDesc(error));
                showToast("设备断开连接 code=" + ble.errDesc(error));
                hideDialog();
//                finish();
            }
        } else if (new_state == Data.STATE_READY) {
            ble.log("connect ok!");
            //连接成功后做配对
            ble.pair();

            cardTimes = 0;
        }
    }

    @Override
    public void onWrite(byte[] bytes, int i) {

    }

    @Override
    public void onResult(byte[] data, int dataLength) {
        byte[] tmp = new byte[dataLength];
        System.arraycopy(data, 0, tmp, 0, dataLength);

        if (null != data) {
            ble.log("recv: " + Util.hexstr(tmp, true));
        } else {
            ble.log("info: no data!");
        }
    }

    @Override
    public void onResult(Result result) {
        try {
            if (null == result) {
                ble.log("info: no data!");
                showToast("no data! reSending");
                ble.ReSend();
                return;
            } else {
                if (result.getCommand() != Command.ExchangeTransparentData) {
                    ble.log("info: " + result.getCommand() + ", "
                            + result.getStatus());

                }
            }

            //配对
            if(result.getCommand() == Command.PairDevice) {
                if(result.getStatus() == Status.OperationSuccess) {
                    ble.log("配对成功");
                    ble.getCardId();
                } else {
                    hideDialog();
                    showToast("配对失败");
                    finish();
                }
            }

            //卡id
            if(result.getCommand() == Command.AntennaControl) {
                if(result.getStatus() == Status.OperationSuccess) {
                    ble.log("卡ID 成功");
                    mController.selectCard();
                } else {
                    hideDialog();
                    showToast("卡ID 失败");
                    finish();
                }
            }


            //选择卡
            if (result.getCommand() == Command.SelectCard) {
                if(result.getStatus() == Status.OperationSuccess) {
                    ble.log("      Id: " + Util.hexstr(result.getCardID(), false)
                            + ", Type: 0x"
                            + Util.hexstr(result.getCardType().getValue()));
                    findCard = 1;
                    tvSend.setText("确认转账");
                    hideDialog();
                    showToast("设备连接成功");
                } else {


                    if(cardTimes >= MainActivity.FIND_CARD_TIMES) {
                        hideDialog();
                        showToast("SelectCard, NoTag,发起" + MainActivity.FIND_CARD_TIMES + "重连后失败");
                        findCard = 0;
                        tvSend.setText("重新寻卡");
                    } else {
                        cardTimes++;
                        ble.log("SelectCard, NoTag,重新第" + cardTimes + "次发起寻卡指令");
                        mController.selectCard();

                    }
                }

            }
            if (result.getStatus() == Status.AuthenticationRequired) {
                ble.log("Please pair the device firstly");
                showToast("Please pair the device firstly");
                ble.mTestCardId = false;
            }

            if (null != result.getData()) {
                try {
                    if (ble.ErrMessage(Util.hexstr(result.getData(), false)) != ""
                            | ble.ErrMessage(Util.hexstr(result.getData(), false))
                            .equals("") == true) {
                        ble.log("返回<<== " + Util.hexstr(result.getData(), true));
                        switch (ble.flag) {
                            case 1:
                                ble.masterkey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("双重加密后的SEED：  " + Util.AddSpace(ble.masterkey));
//                                ble.getPublicKey("00",accountId);
                                break;
                            case 2:
                                ble.btcPublicKey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("公钥数据-比特币：  " + Util.AddSpace(ble.btcPublicKey));
//                                ble.getPublicKey("3C",accountId);
                                break;
                            case 3:
                                ble.btcSignValue = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("签名结果-比特币：  " + Util.AddSpace(ble.btcSignValue));
                                break;
                            case 4:
                                ble.ethPublicKey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("公钥数据-以太坊：  " + Util.AddSpace(ble.ethPublicKey));
                                ble.getSignature("3C",accountId,NumberUtil.bytesToHexString(hash));
                                break;
                            case 5:
                                ble.ethSignValue = Util.hexstr(result.getData(), false);

                                ble.log("签名结果-以太坊：  " + ble.ethSignValue);
                                String rStr = ble.ethSignValue.substring(12,76);
                                ble.log("r：  " + rStr);
                                String sStr = ble.ethSignValue.substring(80,144);
                                ble.log("s：  " + sStr);
                                byte[] r = NumberUtil.hexStringToBytes(rStr);
                                byte[] s = NumberUtil.hexStringToBytes(sStr);
                                //以太坊获取签名后
                                int recId = -1;
                                ECDSASignature sig = new ECDSASignature(new BigInteger(1,r),new BigInteger(1,s));
                                byte[] publicKeyBytes = NumberUtil.hexStringToBytes(ble.ethPublicKey);
                                for (int i = 0; i < 4; i++) {
                                    BigInteger k = Sign.recoverFromSignature(i, sig, hash);
                                    if (k != null && k.equals(new BigInteger(1,publicKeyBytes))) {
                                        recId = i;
                                        break;
                                    }
                                }
                                if (recId == -1) {
                                    throw new RuntimeException(
                                            "Could not construct a recoverable key. This should never happen.");
                                }
                                int headerByte = recId + 27;
                                byte v = (byte) headerByte;


//                                Sign.SignatureData signatureData = new Sign.SignatureData(v,r,s);
                                Sign.SignatureData signatureDataV = new Sign.SignatureData(v, r, s);
                                Log.d("r",NumberUtil.bytesToHexString(signatureDataV.getR()));
                                Log.d("s",NumberUtil.bytesToHexString(signatureDataV.getS()));
                                Log.d("v",NumberUtil.bytesToHexString(new byte[]{signatureDataV.getV()}));
                                byte[] res = TransactionEncoder.encode(rawTransaction,signatureDataV);
                                Log.d("ble",NumberUtil.bytesToHexString(res));

                                //发起请求
                                transaction.setHex(NumberUtil.bytesToHexString(res));
                                Gson gson = new Gson();
                                String json = gson.toJson(transaction);

                                Log.d("json", json);

                                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
                                RetrofitUtil.getService().transaction(body).enqueue(new Callback<MyResult>() {
                                    @Override
                                    public void onResponse(Response<MyResult> response, Retrofit retrofit) {

                                        MyResult result = response.body();
                                        if(result.getStatusCode() == 200) {
                                            hideDialog();
                                            showToast("交易成功");
                                            finish();
                                        } else {

                                            tranTimes++;
                                            if(tranTimes >= TRAN_TIMES ) {
                                                hideDialog();
                                                showToast((String)result.getData());
                                            } else {
                                                //重新发起交易
//                                                showToast("重新发起第" + tranTimes + "次交易");
                                                ble.getPublicKey("3C",accountId);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        hideDialog();
                                        Toast.makeText(getBaseContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case 6:
                                ble.log("成功标识：  "
                                        + Util.hexstr(result.getData(), false)
                                        .substring(4, 6)
                                        + "     [00成功/01失败]");
                                break;
                        }
                    } else {
                        ble.log("返回<<== " + ble.errmessage);
                    }

                } catch (Exception e) {
                    ble.log(e.toString());
                    ble.ReSend();
                }
            }
        } catch (Exception e) {
            ble.log(e.toString());
            ble.ReSend();
        }

    }
}
