package cn.hyperchain.hitoken.fragment.maintab.wallet;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.activity.AddWalletActivity;
import cn.hyperchain.hitoken.activity.AddressBookActivity;
import cn.hyperchain.hitoken.activity.DetailActivity;
import cn.hyperchain.hitoken.activity.GatherActivity;
import cn.hyperchain.hitoken.activity.MainActivity;
import cn.hyperchain.hitoken.activity.SendActivity;
import cn.hyperchain.hitoken.ble.Ble;
import cn.hyperchain.hitoken.ble.Util;
import cn.hyperchain.hitoken.ble.key.Keys;
import cn.hyperchain.hitoken.entity.Center;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.Wallet;
import cn.hyperchain.hitoken.entity.post.WalletBody;
import cn.hyperchain.hitoken.entity.post.WalletItem;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;
import cn.hyperchain.hitoken.thread.WalletRefreshThread;
import cn.hyperchain.hitoken.utils.DoubleUtil;
import cn.hyperchain.hitoken.utils.SPHelper;
import cn.hyperchain.hitoken.view.CommonPopupWindow;
import cn.hyperchain.hitoken.view.SlidingMenu;
import com.rthtech.ble.Controller;
import com.rthtech.ble.Data;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hyperchain.hitoken.imagecrop.Constants;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class WalletFragment extends BaseBarFragment implements com.rthtech.ble.Callback {
    @BindView(R.id.rl_btc)
    RelativeLayout rlBtc;

    @BindView(R.id.ll_btc_gathering)
    LinearLayout llBtcGathering;

    @BindView(R.id.ll_btc_payment)
    LinearLayout llBtcPayment;

    @BindView(R.id.tv_btc_number)
    TextView tvBtcNumber;

    @BindView(R.id.rl_eth)
    RelativeLayout rlBth;

    @BindView(R.id.ll_eth_gathering)
    LinearLayout llEthGathering;

    @BindView(R.id.ll_eth_payment)
    LinearLayout llEthPayment;

    @BindView(R.id.tv_eth_number)
    TextView tvEthNumber;


    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.ll_create_wallet)
    LinearLayout llCreateWallet;

    @BindView(R.id.tv_create_wallet)
    TextView tvCreateWallet;

    @BindView(R.id.iv_personal_center)
    ImageView ivPersonalCenter;

    @BindView(R.id.ll_personal_center)
    LinearLayout llPersonalCenter;

    @BindView(R.id.ll_address_book)
    LinearLayout llAddressBook;

    @BindView(R.id.iv_add_wallet)
    ImageView ivAddWallet;


    SlidingMenu mMenu;

    private TextView tabMain;

    private CommonPopupWindow window;

    private CommonPopupWindow QRcodeBTCWindow;
    private CommonPopupWindow QRcodeETHwindow;


    String accountStr;

    //钱包是否创建 0 表示未创建 1 表示已创建
    int walletCreated = 0;

    //是否跳转至个人中心 0否 1是
    int personalCenter = 0;

    WalletRefreshThread thread;

    private Controller mController = null;
    private Ble ble;

//    public static String accountId ="10000004";

    String password;
    String walletName;
    public String url;

    String registerAccountId;

    public final static int FIND_CARD_TIMES = 5;

    private int cardTimes = 0;

    // 0 未寻卡 1 已寻卡
    private int findCard = 0;

    TextView tvPopupSubmit;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case WalletRefreshThread.SUCCESSFORINDUCTIONCOUNT:
                    if(walletCreated == 1) {
                        ArrayList<Wallet> wallets = (ArrayList<Wallet>) msg.obj;
                        for(int i = 0; i < wallets.size();i++) {
                            Wallet wallet = wallets.get(i);
                            if(wallet.getType().toUpperCase().equals("BTC")) {
                                tvBtcNumber.setText(DoubleUtil.doubleToString(wallet.getBalance()));
                                SPHelper.put(getActivity(),"btcAddress",wallet.getAddress());
                                SPHelper.put(getActivity(),"btcNonce",wallet.getNonce());
                                SPHelper.put(getActivity(),"btcAccountId",wallet.getAccount_id());
                            } else if(wallet.getType().toUpperCase().equals("ETH")) {
                                tvEthNumber.setText(DoubleUtil.doubleToString(wallet.getBalance()));
                                SPHelper.put(getActivity(),"ethAddress",wallet.getAddress());
                                SPHelper.put(getActivity(),"ethNonce",wallet.getNonce());
                                SPHelper.put(getActivity(),"ethAccountId",wallet.getAccount_id());
                            }
                        }
//                        showToast("main定时器启动");
                    }
                    break;

            }

        }

    };

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_wallet);
        ButterKnife.bind(this, rootView);
        mMenu = getActivity().findViewById(R.id.id_menu);
        tabMain = getActivity().findViewById(R.id.tab_wallet);
        hideActionBar();
        initPopupWindow();

    }



    @Override
    public void initData() {
        super.initData();
        showDialog();
        RetrofitUtil.getService().getCenter().enqueue(new Callback<MyResult<Center>>() {
            @Override
            public void onResponse(Response<MyResult<Center>> response, Retrofit retrofit) {

                MyResult result = response.body();
                if(result.getStatusCode() == 200) {
                    Center center = (Center)result.getData();
//                    center.setUser_name("") ;
                    if((center.getUser_name()).equals("")) {
                        //未创建钱包
                        hideDialog();
                        rlBtc.setVisibility(View.GONE);
                        rlBth.setVisibility(View.GONE);
                        llCreateWallet.setVisibility(View.VISIBLE);
                        walletCreated = 0;
                    } else {
                        //已经创建钱包

                        //个人中心赋值
                        ImageView ivHeadImg = getActivity().findViewById(R.id.iv_headImg);
                        TextView tvNickname = getActivity().findViewById(R.id.tv_nickname);
                        TextView tvPhone = getActivity().findViewById(R.id.tv_phone);
                        if(center.getPortrait() != null && !center.getPortrait().isEmpty()) {
                            url = Constants.NETPICTAILAPPHEAD + center.getPortrait();
                            Glide.with(getActivity()).load(url).into(ivHeadImg);
                        }
                        tvNickname.setText(center.getUser_name());
                        tvPhone.setText(center.getPhone_number());

                        rlBtc.setVisibility(View.VISIBLE);
                        rlBth.setVisibility(View.VISIBLE);
                        llCreateWallet.setVisibility(View.GONE);
                        walletCreated = 1;
                        RetrofitUtil.getService().getWallet().enqueue(new Callback<MyResult<List<Wallet>>>() {
                            @Override
                            public void onResponse(Response<MyResult<List<Wallet>>> response, Retrofit retrofit) {
                                hideDialog();
                                MyResult result = response.body();

                                if(result.getStatusCode() == 200) {
                                    ArrayList<Wallet> wallets = (ArrayList<Wallet>)result.getData();
                                    for(int i = 0; i < wallets.size();i++) {
                                        Wallet wallet = wallets.get(i);
                                        if(wallet.getType().toUpperCase().equals("BTC")) {
                                            tvBtcNumber.setText(DoubleUtil.doubleToString(wallet.getBalance()));
                                            SPHelper.put(getActivity(),"btcAddress",wallet.getAddress());
                                            SPHelper.put(getActivity(),"btcNonce",wallet.getNonce());
                                            SPHelper.put(getActivity(),"btcAccountId",wallet.getAccount_id());
                                        } else if(wallet.getType().toUpperCase().equals("ETH")) {
                                            tvEthNumber.setText(DoubleUtil.doubleToString(wallet.getBalance()));
                                            SPHelper.put(getActivity(),"ethAddress",wallet.getAddress());
                                            SPHelper.put(getActivity(),"ethNonce",wallet.getNonce());
                                            SPHelper.put(getActivity(),"ethAccountId",wallet.getAccount_id());
                                        }
                                    }
                                } else {
                                    showToast((String)result.getData());
                                }

                            }

                            @Override
                            public void onFailure(Throwable t) {
                                hideDialog();
                                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    showToast((String)result.getData());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initThreads() {
        thread = new WalletRefreshThread(handler);
        thread.run();

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        initThreads();

    }

    private void initPopupWindow() {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight=metrics.heightPixels;
        int screenWidth=metrics.widthPixels;
        // create popup window
        window = new CommonPopupWindow(getActivity(), R.layout.popup_create_wallet, (int) (screenWidth*0.73), (int) (screenHeight*0.6)) {
            @Override
            protected void initView() {
                View view = getContentView();
                final EditText etWalletName = view.findViewById(R.id.et_wallet_name);
                final EditText etPassword = view.findViewById(R.id.et_password);
                final EditText edPasswordVerify = view.findViewById(R.id.et_password_verify);
                tvPopupSubmit = view.findViewById(R.id.tv_submit);
                TextView tvCancel = view.findViewById(R.id.tv_cancel);

                tvPopupSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(findCard == 1) {
                            walletName = etWalletName.getText().toString();
                            password = etPassword.getText().toString();
                            final String passwordVerify = edPasswordVerify.getText().toString();
                            if(walletName.isEmpty()) {
                                showToast("钱包名称不能为空");
                                return;
                            }
                            if(password.isEmpty()) {
                                showToast("请输入密码");
                                return;
                            }
                            if(passwordVerify.isEmpty()) {
                                showToast("请输入确认密码");
                                return;
                            }

                            if(!passwordVerify.equals(password)) {
                                showToast("两次密码输入不一致");
                                return;
                            }

                            if(ble.checkSessionStatus()) {
                                showDialog();
                                ble.creatSeed(password);
                            } else {
                                showToast("No device connected!");
                            }
                        } else {
                            showToast("发起重新寻卡...");
                            showDialog();
                            cardTimes = 0;
                            mController.selectCard();
                        }


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

                        WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
                        lp.alpha=1.0f;
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getActivity().getWindow().setAttributes(lp);

                        //创建钱包结束 关闭蓝牙连接
                        if(mController != null) {
                            mController.term();
                        }


                    }
                });
            }
        };
    }


    @OnClick({
            R.id.rl_btc,R.id.rl_eth,R.id.tv_create_wallet,R.id.ll_personal_center,R.id.ll_address_book,
            R.id.ll_btc_gathering,R.id.ll_btc_payment,
            R.id.ll_eth_gathering,R.id.ll_eth_payment,R.id.iv_add_wallet,R.id.root

    })
    @Override
    public void onClick(View v) {
        Intent intent;
        super.onClick(v);
        switch (v.getId()) {

            case R.id.root:
                break;
            case R.id.rl_btc:
                intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("type","BTC");
                startActivity(intent);
                break;
            case R.id.rl_eth:
                intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra("type","ETH");
                startActivity(intent);
                break;
            case R.id.tv_create_wallet:

                //打开创建钱包 连接蓝牙设备
                mController = com.rthtech.ble.Factory.getController(getActivity());
                mController.init(this);
                if (!mController.isBluetoothEnabled()) {
                    mController.term();
                    showToast("蓝牙未打开，请打开蓝牙后再操作");
                    break;
                }
                ble = new Ble(mController);
                String bluetooth = (String)SPHelper.get(getActivity(),"bluetooth","");
//                String bluetooth = "D0:B5:C2:B8:95:C2";
                if(bluetooth.isEmpty()) {
                    showToast("蓝牙连接地址为空，无法继续操作");
                    break;
                }

                String currentTime = String.valueOf(System.currentTimeMillis());
                registerAccountId = currentTime.substring(currentTime.length()-8);

                ble.conect(bluetooth);



                showToast("设备连接中...");
                showDialog();

                WindowManager.LayoutParams lp2=getActivity().getWindow().getAttributes();
                lp2.alpha=0.3f;
                window.showAtLocation(root, Gravity.CENTER, 0, 0);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                getActivity().getWindow().setAttributes(lp2);
                break;

            case R.id.ll_personal_center:
                mMenu.toggle();
                break;

            case R.id.ll_address_book:
                intent = new Intent(getActivity(), AddressBookActivity.class);
                intent.putExtra("from",0);
                startActivity(intent);
                break;

            case R.id.ll_btc_gathering:
                intent = new Intent(getActivity(),GatherActivity.class);
                intent.putExtra("type","BTC");
                startActivity(intent);
                break;
            case R.id.ll_btc_payment:
//                intent = new Intent(getActivity(),SendActivity.class);
//                intent.putExtra("type","BTC");
//                startActivity(intent);
                showToast("btc 暂不支持");
                break;

            case R.id.ll_eth_gathering:
                intent = new Intent(getActivity(),GatherActivity.class);
                intent.putExtra("type","ETH");
                startActivity(intent);
                break;
            case R.id.ll_eth_payment:
                intent = new Intent(getActivity(),SendActivity.class);
                intent.putExtra("type","ETH");
                startActivity(intent);
                break;

            case R.id.iv_add_wallet:
                intent = new Intent(getActivity(),AddWalletActivity.class);
                startActivity(intent);
                break;


        }
    }

    private void registerWallet() {
        WalletBody walletBody = new WalletBody();
        walletBody.setWallet_name(walletName);
        walletBody.setPay_password(password);
        walletBody.setSed(ble.masterkey);


        List<WalletItem> walletItems = new ArrayList<>();
        WalletItem walletItem1 = new WalletItem();
        walletItem1.setType("BTC");
        walletItem1.setAccount_id(registerAccountId);
        String btcAddress = "0x" + Keys.getAddress(ble.btcPublicKey);
        walletItem1.setAddress(btcAddress);

        WalletItem walletItem2 = new WalletItem();
        walletItem2.setAccount_id(registerAccountId);
        walletItem2.setType("ETH");
        String ethAddress = "0x" + Keys.getAddress(ble.ethPublicKey);
        walletItem2.setAddress(ethAddress);


        walletItems.add(walletItem1);
        walletItems.add(walletItem2);

        walletBody.setWallets(walletItems);

        Gson gson = new Gson();
        String json = gson.toJson(walletBody);
        Log.e("requestBody",json);

        SPHelper.put(getActivity(),"btcAddress",btcAddress);
        SPHelper.put(getActivity(),"ethAddress",ethAddress);



        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        RetrofitUtil.getService().registerWallet(body).enqueue(new Callback<MyResult>() {
            @Override
            public void onResponse(Response<MyResult> response, Retrofit retrofit) {
                hideDialog();
                MyResult result = response.body();
                if(result.getStatusCode() == 200) {
                    showToast("注册钱包成功");
                    //创建钱包成功
                    rlBtc.setVisibility(View.VISIBLE);
                    rlBth.setVisibility(View.VISIBLE);
                    llCreateWallet.setVisibility(View.GONE);
                    window.getPopupWindow().dismiss();
                    walletCreated = 1;

                    SPHelper.put(getActivity(),"ethAccountId",registerAccountId);
                    SPHelper.put(getActivity(),"btcAccountId",registerAccountId);

                } else {
                    showToast((String)result.getData());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                hideDialog();
                Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(thread);
//        showToast("main thead 销毁");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mController != null) {
            mController.term();
        }

    }

    @Override
    protected boolean hasBackBtn() {
        return false;
    }

    @Override
    protected boolean hasRightBtn() {
        return false;
    }

    @Override
    protected boolean isRightImg() {
        return false;
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
                    if(window.getPopupWindow().isShowing()) {
                        window.getPopupWindow().dismiss();
                    }
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
                    if(window.getPopupWindow().isShowing()) {
                        window.getPopupWindow().dismiss();
                    }
                }
            }


            //选择卡
            if (result.getCommand() == Command.SelectCard) {
                if(result.getStatus() == Status.OperationSuccess) {
                    ble.log("      Id: " + Util.hexstr(result.getCardID(), false)
                            + ", Type: 0x"
                            + Util.hexstr(result.getCardType().getValue()));
                    findCard = 1;
                    tvPopupSubmit.setText("确认");
                    hideDialog();
                    showToast("设备连接成功");
                } else {


                    if(cardTimes >= MainActivity.FIND_CARD_TIMES) {
                        hideDialog();
                        showToast("SelectCard, NoTag,发起" + MainActivity.FIND_CARD_TIMES + "重连后失败");
                        findCard = 0;
                        tvPopupSubmit.setText("重新寻卡");
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
                                ble.getPublicKey("00",registerAccountId);
                                break;
                            case 2:
                                ble.btcPublicKey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("公钥数据-比特币：  " + Util.AddSpace(ble.btcPublicKey));
                                ble.getPublicKey("3C",registerAccountId);
                                break;
                            case 3:
                                ble.btcSignValue = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("签名结果-比特币：  " + Util.AddSpace(ble.btcSignValue));
                                break;
                            case 4:
                                ble.ethPublicKey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("公钥数据-以太坊：  " + ble.ethPublicKey);
                                //获取以太坊签名后 返回数据接口
                                registerWallet();
                                break;
                            case 5:
                                ble.ethSignValue = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("签名结果-以太坊：  " + Util.AddSpace(ble.ethSignValue));
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
