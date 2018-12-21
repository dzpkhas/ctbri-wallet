package cn.hyperchain.hitoken.fragment.maintab.wallet;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import cn.com.fmsh.bluetooth.sdk.BLEServiceOperate;
import cn.com.fmsh.bluetooth.sdk.ServiceStatusCallback;
import cn.hyperchain.hitoken.sdk.CardMngIfs;
import cn.hyperchain.hitoken.sdk.CardSysResult;
import cn.hyperchain.hitoken.sdk.ConstantField;
import com.google.gson.Gson;
import com.rthtech.ble.Controller;
import com.rthtech.ble.Data;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.hyperchain.hitoken.R;
import cn.hyperchain.hitoken.activity.AddWalletActivity;
import cn.hyperchain.hitoken.activity.AddressBookActivity;
import cn.hyperchain.hitoken.activity.MainActivity;
import cn.hyperchain.hitoken.ble.Ble;
import cn.hyperchain.hitoken.ble.Util;
import cn.hyperchain.hitoken.ble.key.Keys;
import cn.hyperchain.hitoken.entity.Center;
import cn.hyperchain.hitoken.entity.MyResult;
import cn.hyperchain.hitoken.entity.post.WalletBody;
import cn.hyperchain.hitoken.entity.post.WalletItem;
import cn.hyperchain.hitoken.fragment.BaseBarFragment;
import cn.hyperchain.hitoken.imagecrop.Constants;
import cn.hyperchain.hitoken.retrofit.RetrofitUtil;
import cn.hyperchain.hitoken.thread.WalletRefreshThread;
import cn.hyperchain.hitoken.utils.SPHelper;
import cn.hyperchain.hitoken.view.CommonPopupWindow;
import cn.hyperchain.hitoken.view.SlidingMenu;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

import static cn.hyperchain.hitoken.ble.Util.log;
import static cn.hyperchain.hitoken.sdk.ConstantField.*;


public class MainFragment extends BaseBarFragment implements com.rthtech.ble.Callback, ServiceStatusCallback {


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

    @BindView(R.id.fl_content)
    FrameLayout flContent;

    @BindView(R.id.tv_property)
    TextView tvProperty;


    private CommonPopupWindow window;
    private CommonPopupWindow cardReissueWindow;

    String accountStr;

    //钱包是否创建 0 表示未创建 1 表示已创建
    int walletCreated = 0;

    //是否跳转至个人中心 0否 1是
    int personalCenter = 0;

    WalletRefreshThread thread;

    private Controller mController = null;
    private Ble ble;
    private CardSysResult cardSysResult = new CardSysResult();

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
    TextView tvReissueCard;

    WalletListFragment walletListFragment;
    CardMngIfs<String> cardMngIfs = new CardMngIfs<>();
    String phoneNum = "13012345678";
    String iccid = "QQ11WW22EE33RR44TT";
    String idNum = "120104201811160113";

    String carrierKey;       //运营商密码
    String encyptedSeed;    //加密后种子

    Boolean getEncryptedSeed_done = true;
    Boolean getCarrierKey_done = true;

    //dzp SDK
    private BluetoothAdapter adapter;
    protected BLEServiceOperate mBLEServiceOperate;
    public List<BluetoothDevice> mBlueList = new ArrayList<>();
    private String  mStrProcessKey = "48ad10e64db3b448ebbd66fe546889bc";

    private BroadcastReceiver mDiscoveryResult = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            final BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!mBlueList.contains(bluetoothDevice)){
                        mBlueList.add(bluetoothDevice);
                        Log.d("dzp", "mBlueList " + mBlueList.toString());
                    }
                }
            });
        }
    };

    @Override
    public void initViews(View rootView) {
        super.initViews(rootView);
        setView(R.layout.fragment_main);

        ButterKnife.bind(this, rootView);
        hideActionBar();
        initPopupWindow();
        initCardReissueWindow();
        walletListFragment = new WalletListFragment();
    }

    private void  loadFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, walletListFragment);
        transaction.commit();
    }

    @Override
    public void initData() {
        super.initData();
        showDialog();
        RetrofitUtil.getService().getCenter().enqueue(new Callback<MyResult<Center>>() {
            @Override
            public void onResponse(Response<MyResult<Center>> response, Retrofit retrofit) {

                hideDialog();
                MyResult result = response.body();
                if(result.getStatusCode() == 200) {
                    Center center = (Center)result.getData();
//                    center.setUser_name("") ;
                    if((center.getUser_name()).equals("")) {
                        //未创建钱包
                        hideDialog();
                        ivAddWallet.setVisibility(View.GONE);
                        flContent.setVisibility(View.GONE);
                        llCreateWallet.setVisibility(View.VISIBLE);
                        walletCreated = 0;
                        SPHelper.put(getContext(),"walletCreated","no");
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

                        flContent.setVisibility(View.VISIBLE);
                        ivAddWallet.setVisibility(View.VISIBLE);

                        llCreateWallet.setVisibility(View.GONE);
                        walletCreated = 1;
                        SPHelper.put(getContext(),"walletCreated","yes");
                        loadFragment();
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


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
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
                //演示
//                final EditText etPhoneNum = view.findViewById(R.id.tv_phone);
//                final EditText etIccid = view.findViewById(R.id.tv_iccid);

                //演示

                tvPopupSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("dzp PopupWindow 确定按钮", phoneNum.toString() + " " +  iccid);
//                        phoneNum = etPhoneNum.getText().toString();
//                        iccid = etIccid.getText().toString();
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
                            Log.d("dzp checkSessionStatus", "123");
//                            if(ble.checkSessionStatus()) {
                              if(true) {
                                showDialog();
                                //卡SDK
//                                cardSysResult.getCareerKey();
                                //演示
                                //ble.creatSeed(password);
                                //得到加密seed
                                  Log.d("dzp getEncryptedSeed", cardSysResult.getEncryptedSeed());
                                cardSysResult.
                                        setEncryptedSeed("37F1699B9E5941C66D4AA137DD09161D8E72585F0B16B3A182A555FBC712A6CEBFD557F9415D0B3D895EEF544045FB2748F1A40BF3CB6400FCAA26FA6F463FCF");  //临时
                                phoneNum = "13012345109";
                                iccid = "898603181112712X109";
                                //演示
                                Log.d("dzp getEncryptedSeed", cardSysResult.getEncryptedSeed());
                                cardMngIfs.submitSeed(phoneNum, iccid, cardSysResult.getEncryptedSeed(), new CardMngIfs.AnsyCallback<String>(){
                                    @Override
                                    public void AnsyLoader(String loader, String Url) {
                                        JSONObject jsonObject;
                                        try{
                                            jsonObject = new JSONObject(loader);
                                            if(jsonObject.getString("resCode") == ConstantField.CARDSYS_OK){
                                                Log.d("dzp submitSeed", jsonObject.getString("resCode"));
                                                //演示
//                                                ble.log("双重加密后的SEED：  " + Util.AddSpace(ble.masterkey));
//                                                生成以太坊公私钥
//                                                ble.getPublicKey("3C",registerAccountId);
                                                //演示

//                                                cardSysResult.setEncryptedSeed(ble.masterkey);
                                                //向平台上传种子


                                            }else{
                                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                                        .setTitle("开卡失败")
                                                        .setMessage("种子上传失败，请检查网络或联系客服")
                                                        .setIcon(R.mipmap.ic_launcher)
                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.cancel();
                                                            }
                                                        }).create();
                                                alertDialog.show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                                //卡SDK
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

    //卡SDk
    private void initCardReissueWindow() {
        // get the height of screen
        DisplayMetrics metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight=metrics.heightPixels;
        int screenWidth=metrics.widthPixels;

        // create popup window
        cardReissueWindow = new CommonPopupWindow(getActivity(), R.layout.popup_card_reissue, (int) (screenWidth*0.73), (int) (screenHeight*0.6)) {//设置弹窗的ui和高度宽度
            @Override
            protected void initView() {
                View view = getContentView();
                final EditText etWalletName = view.findViewById(R.id.reissue_card_password);//补卡密码
                tvReissueCard = view.findViewById(R.id.reissue_submit);//提交按钮
                TextView tvCancel = view.findViewById(R.id.reissue_cancel);//取消按钮

                tvReissueCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        walletName = etWalletName.getText().toString();
                        if(findCard == 1) {
                            if(walletName.isEmpty()) {
                                showToast("密码不能为空");
                                return;
                            }
                            if(ble.checkSessionStatus()) {
                                showDialog();
                                //得到运营商密码
                                phoneNum = "13012345110";
                                iccid = "898603181112712X110";
                                cardMngIfs.submitReplaceInfoToSmart(phoneNum, iccid, new CardMngIfs.AnsyCallback<String>() {
                                    @Override
                                    public void AnsyLoader(String loder, String Url4) {
                                        JSONObject jsonObject;
                                        try{
                                            jsonObject = new JSONObject(loder);
                                            if(jsonObject.getString("resCode") == ConstantField.CARDSYS_OK){
                                                carrierKey = jsonObject.getString("resmsg");
                                                getCarrierKey_done = true;

                                                //恢复卡
                                                if (getCarrierKey_done && getEncryptedSeed_done){
                                                    recoverCard(encyptedSeed, registerAccountId, etWalletName.getText().toString(), carrierKey);
                                                }


                                            }else{
                                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                                        .setTitle("卡恢复失败")
                                                        .setMessage("卡恢复失败，请检查网络或联系客服")
                                                        .setIcon(R.mipmap.ic_launcher)
                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.cancel();
                                                            }
                                                        }).create();
                                                alertDialog.show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                //得到加密种子
                                cardMngIfs.requestSeedToSmart(phoneNum, iccid, new CardMngIfs.AnsyCallback<String>() {
                                    @Override
                                    public void AnsyLoader(String loder, String Url4) {
                                        JSONObject jsonObject;
                                        try{
                                            jsonObject = new JSONObject(loder);
                                            if(jsonObject.getString("resCode") == ConstantField.CARDSYS_OK){
                                                    encyptedSeed = jsonObject.getString("resmsg");
                                                    getCarrierKey_done = true;

                                                //恢复卡
                                                if (getCarrierKey_done && getEncryptedSeed_done){
                                                    recoverCard(encyptedSeed, registerAccountId, etWalletName.getText().toString(), carrierKey);
                                                }


                                            }else{
                                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                                        .setTitle("卡恢复失败")
                                                        .setMessage("卡恢复失败，请检查网络或联系客服")
                                                        .setIcon(R.mipmap.ic_launcher)
                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.cancel();
                                                            }
                                                        }).create();
                                                alertDialog.show();
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
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

            public void recoverCard(String encyptedSeed, String registerAccountId, String userKey, String carrierKey){
                //恢复种子
                ble.recoveryKey(userKey, encyptedSeed);
                //恢复公私钥
                ble.getPublicKey("3C", registerAccountId);      //以太坊
                ble.getPublicKey("00", registerAccountId);      //比特币
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
    //卡SDK
    @OnClick({
            R.id.tv_create_wallet,R.id.ll_personal_center,R.id.ll_address_book,
            R.id.iv_add_wallet,R.id.root,R.id.fl_content, R.id.tv_scan_bluetooth,
            R.id.tv_pair_bluetooth, R.id.tv_select

    })


    @Override
    public void onClick(View v) {
        String resCode = null;



        Intent intent;
        super.onClick(v);
        switch (v.getId()) {

            case R.id.root:
                break;
            case R.id.fl_content:
                break;

                //dzp sdk
            case R.id.tv_scan_bluetooth:
                //得到手机蓝牙服务
                final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                if(bluetoothManager != null){
                    adapter = bluetoothManager.getAdapter();
                    if (adapter == null || !adapter.isEnabled()) {
                        Toast.makeText(getActivity(), "请打开蓝牙连接", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //初始化蓝牙
                mBLEServiceOperate = BLEServiceOperate.getInstance(getActivity().getApplicationContext());
                if (!this.mBLEServiceOperate.isSupportBLE()) {
                    Toast.makeText(getActivity(), "无法连接蓝牙", Toast.LENGTH_LONG).show();
                }
                mBLEServiceOperate.setServiceStatusCallback(this);
                mBLEServiceOperate.SetBroadcastReceiverCallBack( mDiscoveryResult );
                //扫描
                mBLEServiceOperate.startLeScan();

                break;
            case R.id.tv_pair_bluetooth:
                String address = "00:F2:1D:4C:A6:00";
                mBLEServiceOperate.setMAC(address);
                int connect = mBLEServiceOperate.connect(address, mStrProcessKey, 0,"1");
                Log.d("dzp", "connect " + connect);
                break;
            case R.id.tv_select:
                try {
                    String SelectApdu="00A404000e636f696e57616c6c657441707001";
                    log("dzp 发送指令: "+SelectApdu);
                    String command = mBLEServiceOperate.sendAPDUCommand(SelectApdu, 0);
                    Log.e("dzp command:", "command:" + command);
                    log("dzp 返回命令：" + command);

                }catch (Exception e){
                    e.printStackTrace();
                    log("发生错误："+e.getMessage());
                }
                break;

            case R.id.tv_create_wallet:
                //卡SDK
                Log.d("dzp 点击免费创建钱包", "123");
                View view = this.getLayoutInflater().inflate(R.layout.fragment_main, (ViewGroup) null);
                final EditText etPhoneNum = view.findViewById(R.id.tv_phone);
                final EditText etIccid = view.findViewById(R.id.tv_iccid);

                phoneNum = etPhoneNum.getText().toString();
                iccid = etIccid.getText().toString();
                Log.d("dzp phoneNum", phoneNum + " " + iccid);
                //得到carrier key
                cardMngIfs.submitInfo(phoneNum,iccid, idNum, new CardMngIfs.AnsyCallback<String>() {
                    @Override
                    public void AnsyLoader(String loder, String Url) {
                        JSONObject jsonObject;
//                        CardMngIfs<String> cardMngIfs = new CardMngIfs<>();
                        String resCode = null;
                        //此处UI线程
//                        Log.d("MainActivity",loder+"ffff");
//                        jsonObject = cardMngIfs.parseJSONWithJSONObject(loder);
//                        Log.d("MainFragment", resCode);

                        try{
                            jsonObject = new JSONObject(loder);
                            Log.d("dzp resCode", jsonObject.getString("resCode"));
                            if(jsonObject.getString("resCode") == CARDSTATUS0){
                                //得到carrier key
                                cardSysResult.setCareerKey(jsonObject.getString("result"));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //打开创建钱包 连接蓝牙设备
                        mController = com.rthtech.ble.Factory.getController(getActivity());
                        mController.init(MainFragment.this);
                        if (!mController.isBluetoothEnabled()) {
                            mController.term();
                            showToast("蓝牙未打开，请打开蓝牙后再操作");
                            return;
                        }
                        ble = new Ble(mController);
//                        String bluetooth = (String)SPHelper.get(getActivity(),"bluetooth","");
                        String bluetooth = "7C:01:0A:FC:7A:E7";
                        if(bluetooth.isEmpty()) {
                            showToast("蓝牙连接地址为空，无法继续操作");
                            return;
                        }
                        String currentTime = String.valueOf(System.currentTimeMillis());

                        //卡平台
                        //生成种子时用作随机数
//                        registerAccountId = currentTime.substring(currentTime.length()-8);
                        //卡平台

                        ble.conect(bluetooth);
                        showToast("设备连接中...");
                        showDialog();

                        //获得窗口属性
                        WindowManager.LayoutParams lp2=getActivity().getWindow().getAttributes();
                        //设置透明度
                        lp2.alpha=0.3f;
                        Log.d("dzp 弹出window", window.toString());
                        //弹窗位置
                        window.showAtLocation(root, Gravity.CENTER, 0, 0);
                        //背后窗口参数
                        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        getActivity().getWindow().setAttributes(lp2);
                    }
                });
                //卡SDK


//                //打开创建钱包 连接蓝牙设备
//                mController = com.rthtech.ble.Factory.getController(getActivity());
//                mController.init(this);
//                if (!mController.isBluetoothEnabled()) {
//                    mController.term();
//                    showToast("蓝牙未打开，请打开蓝牙后再操作");
//                    break;
//                }
//                ble = new Ble(mController);
//                String bluetooth = (String)SPHelper.get(getActivity(),"bluetooth","");
////                String bluetooth = "D0:B5:C2:B8:95:C2";
//                if(bluetooth.isEmpty()) {
//                    showToast("蓝牙连接地址为空，无法继续操作");
//                    break;
//                }

//                String currentTime = String.valueOf(System.currentTimeMillis());
//                registerAccountId = currentTime.substring(currentTime.length()-8);
//
//                ble.conect(bluetooth);



//                showToast("设备连接中...");
//                showDialog();

//                WindowManager.LayoutParams lp2=getActivity().getWindow().getAttributes();
//                lp2.alpha=0.3f;
//                window.showAtLocation(root, Gravity.CENTER, 0, 0);
//                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                getActivity().getWindow().setAttributes(lp2);
                break;

            case R.id.ll_personal_center:
                SlidingMenu mMenu = getActivity().findViewById(R.id.id_menu);
                mMenu.toggle();

                break;

            case R.id.ll_address_book:
                intent = new Intent(getActivity(), AddressBookActivity.class);
                intent.putExtra("from",0);
                startActivity(intent);
                break;


            case R.id.iv_add_wallet:
                intent = new Intent(getActivity(),AddWalletActivity.class);
                startActivityForResult(intent,100);
                break;


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        walletListFragment.beginRefreshing();

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
                    ivAddWallet.setVisibility(View.VISIBLE);
                    flContent.setVisibility(View.VISIBLE);
                    llCreateWallet.setVisibility(View.GONE);
                    window.getPopupWindow().dismiss();
                    walletCreated = 1;
                    SPHelper.put(getContext(),"walletCreated","yes");
                    loadFragment();

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
                            //卡SDK
                            case ConstantField.RESULT_CREATSEED:
                                //加密后的种子
                                ble.masterkey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                //移动到window的确认按钮下了

                                break;
                            case ConstantField.RESULT_PUBLICKEY_BTC:
                                ble.btcPublicKey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("公钥数据-比特币：  " + Util.AddSpace(ble.btcPublicKey));
                                ble.getPublicKey("3C",registerAccountId);
                                break;
                            case ConstantField.RESULT_SIGNATURE_BTC:
                                ble.btcSignValue = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("签名结果-比特币：  " + Util.AddSpace(ble.btcSignValue));
                                break;
                            case ConstantField.RESULT_PUBLICKEY_ETH:
                                ble.ethPublicKey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("dzp" + result.toString());
                                ble.log("公钥数据-以太坊：  " + ble.ethPublicKey);
                                //获取以太坊签名后 返回数据接口
                                registerWallet();
                                break;
                            case ConstantField.RESULT_SIGNATURE_ETH:
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
                            //卡SDK
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

    @Override
    public void OnServiceStatus(int i) {

    }
}
