package cn.hyperchain.hitoken.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import com.rthtech.ble.Controller;
import com.rthtech.ble.Data;

import cn.hyperchain.hitoken.activity.GoldenTicketsActivity;
import cn.hyperchain.hitoken.activity.MainActivity;
import cn.hyperchain.hitoken.ble.Ble;
import cn.hyperchain.hitoken.ble.Util;
import cn.hyperchain.hitoken.utils.NumberUtil;
import cn.hyperchain.hitoken.utils.SPHelper;

/**
 * @author liunan
 *
 */
public class WebViewWithAndroid extends Object implements com.rthtech.ble.Callback{
    //native传递给会h5的token
    private String mToken;
    private int findCard = 0;
    //卡链接蓝牙次数
    private int cardTimes = 0;
    String accountId;//账户id
    byte[] hash;//交易摘要哈希值
    static final int SignaValue = 50;
    //蓝牙设备和卡
    Controller mController = null;
    Ble ble;
    Context context;
    WebView webView;
    Activity activity;
    public  static final int YES = 50;


public WebViewWithAndroid (Context mContext, WebView webView, Activity activity,Controller mController){
    this.context = mContext;
    this.webView = webView;
    this.activity = activity;
    this.mController = mController;
}


    //返回给h5token信息
    @JavascriptInterface
    public String getToken() {
        mToken = (String) SPHelper.get(context, "token", "");
        return mToken;

    }



    //获取签名
    @JavascriptInterface
    public void getSignature(String transaction) {

        //生成hash
        //rawTransaction = RawTransaction.createEtherTransaction(transaction);
//            byte[] encodedtransaction = TransactionEncoder.encode(rawTransaction);
//            Log.d("hash1",NumberUtil.bytesToHexString(encodedtransaction));


//            hash = Hash.sha3(encodedtransaction);
//            Log.d("hash2",NumberUtil.bytesToHexString(hash));


        mController.init(this);
        if (!mController.isBluetoothEnabled()) {
            mController.term();
//            showToast("蓝牙未打开，请打开蓝牙后再操作");
            Toast.makeText(activity,"蓝牙未打开，请打开蓝牙后再操作",Toast.LENGTH_SHORT).show();

        }
        ble = new Ble(mController);
        String bluetooth = (String) SPHelper.get(context, "bluetooth", "");
        if (bluetooth.isEmpty()) {
//            showToast("蓝牙连接地址为空，无法继续操作");
            Toast.makeText(activity,"蓝牙连接地址为空，无法继续操作",Toast.LENGTH_SHORT).show();
        }

        ble.conect(bluetooth);
//        showToast("设备连接中...");
        Toast.makeText(activity,"设备连接中...",Toast.LENGTH_SHORT).show();

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
//                showToast("设备断开连接 code=" + ble.errDesc(error));

//                Toast.makeText(activity,"设备断开连接 code=" + ble.errDesc(error),Toast.LENGTH_SHORT).show();
                Log.d("handler","状态改变");
//                hideDialog();
//                finish();
                Log.d("handler",ble.errDesc(error));
                if("device disconnected".equals(ble.errDesc(error))){
                    String bluetooth = (String) SPHelper.get(context, "bluetooth", "");
                    ble.conect(bluetooth);
                }
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
//                showToast("no data! reSending");

                Toast.makeText(activity,"no data! reSending",Toast.LENGTH_SHORT).show();
//                if (mController.isIdle())
//                ble.ReSend();
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
//                    showToast("配对失败");
                    Toast.makeText(activity,"配对失败",Toast.LENGTH_SHORT).show();
                }
            }

            //卡id
            if(result.getCommand() == Command.AntennaControl) {
                if(result.getStatus() == Status.OperationSuccess) {
                    ble.log("卡ID 成功");
                    mController.selectCard();
                } else {

//                    showToast("卡ID 失败");
                    Toast.makeText(activity,"卡ID 失败",Toast.LENGTH_SHORT).show();
                }
            }


            //选择卡
            if (result.getCommand() == Command.SelectCard) {
                if(result.getStatus() == Status.OperationSuccess) {
                    ble.log("      Id: " + Util.hexstr(result.getCardID(), false)
                            + ", Type: 0x"
                            + Util.hexstr(result.getCardType().getValue()));
//                    showToast("      Id: " + Util.hexstr(result.getCardID(), false)
//                            + ", Type: 0x"
//                            + Util.hexstr(result.getCardType().getValue()));
                    findCard = 1;

                    Toast.makeText(activity,"设备连接成功",Toast.LENGTH_SHORT).show();
                    accountId = (String)SPHelper.get(activity,"ethAccountId","");
                    ble.getSignature("3C",accountId,NumberUtil.bytesToHexString(hash));


                } else {

                    if(cardTimes >= MainActivity.FIND_CARD_TIMES) {

//                      showToast("SelectCard, NoTag,发起" + MainActivity.FIND_CARD_TIMES + "重连后失败");
                        findCard = 0;
                    } else {
                        cardTimes++;
                        ble.log("SelectCard, NoTag,重新第" + cardTimes + "次发起寻卡指令");
                        mController.selectCard();
                    }
                }

            }
            if (result.getStatus() == Status.AuthenticationRequired) {
                ble.log("Please pair the device firstly");
//                showToast("Please pair the device firstly");
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
                                ble.getPublicKey("00",(String) SPHelper.get(context,"registerAccountId",""));

                                break;
                            case 2:
                                ble.btcPublicKey = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("公钥数据-比特币：  " + Util.AddSpace(ble.btcPublicKey));

                                webView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String str=Util.AddSpace(ble.btcPublicKey);
                                        webView.loadUrl("javascript:returnResult('"
                                                + str+ "')");
                                    }
                                });
//                                  ble.getPublicKey("3C",(String) SPHelper.get(getApplicationContext(), "registerAccountId",""));
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

                                break;
                            case 5:
                                ble.ethSignValue = Util.hexstr(result.getData(), false)
                                        .substring(4, 132);
                                ble.log("签名结果-以太坊：  " + Util.AddSpace(ble.ethSignValue));
//                                showToast(ble.ethSignValue);
//                                Toast.makeText(activity,"ble.ethSignValue",Toast.LENGTH_SHORT).show();
                                Message message = new Message();
                                message.obj = ble.ethSignValue ;
                                message.what = YES;
                                GoldenTicketsActivity.handler.sendMessage(message);
                                Log.d("handler","得到消息");

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

