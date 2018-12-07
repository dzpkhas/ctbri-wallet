package cn.hyperchain.hitoken.ble;

import android.content.SharedPreferences;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.rthtech.ble.Controller;
import com.rthtech.ble.Data;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ble {

    //窗体显示相关变量
    private final static String TAG = "BLE";
    private final static String STR_LINE_SESSION = "===========================";
    private final static String STR_LINE_COMMAND = "--------------------";
    private String mDeviceAddress = null;
    private SimpleAdapter mAdapterAPI = null;
    private List<Map<String, String>> mListAPI = null;
    private ListView mListViewAPI = null;
    private ArrayAdapter<String> mAdapterLog = null;
    private ArrayList<String> mListLog = null;
    private ListView mListViewLog = null;
    private View mDialogView = null;
    private List<String> mListDeviceName = null;
    private List<String> mListDeviceAddress = null;
    public boolean mScanMode = false;
    private InputData mInputData = new InputData();
    boolean isRun = true;
    EditText edtsendms;
    Button btnsend;
    SharedPreferences sp;
    Button btnSetting;
    PrintWriter out;
    BufferedReader in;
    public boolean mTestCardId;
    public String[] cardinfolist;
    public String result;

    // 卡片操作相关变量
    public String CLA;
    public String INS;
    public String P1;
    public String P2;
    public String LC;
    public String DATE;
    public String LE;
    public String errmessage = "";
    public String masterkey = "";
    public String btcPublicKey = "";
    public String ethPublicKey = "";
    public String btcSignValue = "";
    public String ethSignValue = "";
    public String StrRetrun = "";
    public String StrApdu = "";

    // 卡片操作记时变量
    public long begintime;
    public long endtime;
    public long costTime;
    // 返回标识
    public int flag = -1;

    private class MyNumberKeyListener extends NumberKeyListener {
        public MyNumberKeyListener(boolean hex) {
            this.hex = hex;
        }

        private char[] hexChars = "0123456789abcdefABCDEF".toCharArray();
        private boolean hex = true;
        private char[] ascChars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-=_+`~,./<>?[]{}\\|\'\" "
                .toCharArray();

        @Override
        public int getInputType() {
            return android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;
        }

        @Override
        protected char[] getAcceptedChars() {
            if (hex) {
                return hexChars;
            }
            return ascChars;
        }
    }


    private static class InputData {
        public boolean boolValue;
        public byte byteValue1;
        public byte byteValue2;
        public byte[] dataValue1;
        public int dataLength1;
        public String title;
        public String labelBool;
        public String labelByte1;
        public String labelByte2;
        public String labelData1;
        public boolean asciiData1;

        public InputData() {
            reset();
        }

        public void reset() {
            title = null;
            labelBool = null;
            labelByte1 = null;
            labelByte2 = null;
            labelData1 = null;
            boolValue = false;
            byteValue1 = (byte) 0;
            byteValue2 = (byte) 0;
            dataValue1 = null;
            asciiData1 = false;
        }

        public void setDataLength(int length) {
            dataLength1 = length;
        }

        public void set(String title, String labelBool, boolean boolValue,
                        String labelByte1, byte byteValue1, String labelData1,
                        byte[] dataValue1) {
            reset();
            this.title = title;
            this.labelBool = labelBool;
            this.boolValue = boolValue;
            this.labelByte1 = labelByte1;
            this.byteValue1 = byteValue1;
            this.labelData1 = labelData1;
            this.dataValue1 = dataValue1;
        }

        public void set(String title, String labelByte1, byte byteValue1,
                        String labelData1, byte[] dataValue1) {
            reset();
            this.title = title;
            this.labelByte1 = labelByte1;
            this.byteValue1 = byteValue1;
            this.labelData1 = labelData1;
            this.dataValue1 = dataValue1;
        }

        public void set(String title, String labelBool, boolean boolValue) {
            reset();
            this.title = title;
            this.labelBool = labelBool;
            this.boolValue = boolValue;
        }

        public void set(String title, String labelByte1, byte byteValue1,
                        String labelByte2, byte byteValue2) {
            reset();
            this.title = title;
            this.labelByte1 = labelByte1;
            this.byteValue1 = byteValue1;
            this.labelByte2 = labelByte2;
            this.byteValue2 = byteValue2;
        }

        public void set(String title, String labelByte1, byte byteValue1) {
            reset();
            this.title = title;
            this.labelByte1 = labelByte1;
            this.byteValue1 = byteValue1;
        }

        public void set(String title, String labelData1, byte[] dataValue1,
                        boolean ascii) {
            reset();
            this.title = title;
            this.labelData1 = labelData1;
            this.dataValue1 = dataValue1;
            this.asciiData1 = ascii;
        }
    }

    private int mCommand;

    private Controller mController = null;

    enum APIId {
        AntennaControl, SelectCard, LoginSector, ReadDataBlock, WriteDataBlock, ReadValueBlock, InitializeValueBlock, WriteMasterKey, IncrementValue, DecrementValue, CopyValue, ReadDataPage, WriteDataPage, RequestAnswerToSelect, ExchangeTransparentData1, ExchangeTransparentData2, ExchangeTransparentData3, ExchangeTransparentData4, ExchangeTransparentData6, ExchangeTransparentData5, ManageLED, GetFirmwareVersion, ModifyDeviceName, BeepControl, ReadDeviceName, OriginalData, PairDevice
    }


    public Ble(Controller controller) {
        mController = controller;

    }

    public void conect(String address) {
        mController.connect(address);
    }

    /**
     * 生成Seed
     *
     * @param key
     *            用户输入的支付密码，长度任意
     * @return 以TLV格式将seed密文返回
     */
    public void creatSeed(String key) {
        flag = 1;
        try {
            CLA = "80";
            INS = "E0";
            P1 = "00";
            P2 = "00";
            LC = "12";
            DATE = Utils.getSha1(key).substring(0, 32)
                    + CRCUtil.getCrc16(Utils.getSha1(key).substring(0, 32));
            LE = "44";
            StrApdu = CLA + INS + P1 + P2 + LC + DATE + LE;
            log("send==>>" + Util.AddSpace(StrApdu));

            final byte[] Apdudata = Util.hexStringToByte(StrApdu.toUpperCase()
                    .replace(" ", ""));
            begintime = System.currentTimeMillis();
            mController.exchangeTransparentData(Apdudata, Apdudata.length);
        } catch (Exception e) {
            log(e.toString());
        }

    }

    /**
     * 获取公钥
     *
     * @param coinType
     *            `00`表示?特币；`3C`表示以太坊
     * @param accountID
     *            钱包ID 4字节
     * @return 以TLV格式返回公钥和索引
     */
    public void getPublicKey(String coinType, String accountID) {
        if(coinType.equals("00")) {
            flag = 2;
        } else {
            flag = 4;
        }


        CLA = "80";
        INS = "E2";
        P1 = "00";
        P2 = coinType; // `00`表示?比特币；`3C`表示以太坊
        LC = "04";
        DATE = accountID;
        LE = "44";
        StrApdu = CLA + INS + P1 + P2 + LC + DATE + LE;
        log("send==>>" + Util.AddSpace(StrApdu));
        final byte[] Apdudata = Util.hexStringToByte(StrApdu.toUpperCase()
                .replace(" ", ""));
        begintime = System.currentTimeMillis();
        mController.exchangeTransparentData(Apdudata, Apdudata.length);
    }

    /**
     * 签名
     *
     * @param coinType
     *            `00`表示?比特币；`3C`表示以太坊
     * @param accountID
     *            钱包ID
     * @param hashTransaction
     *            待签名的Hash值
     * @return 返回签名结果。返回结果是（r, s），采用SecP256k1标准参数，使用ECDSA做签名
     */
    public void getSignature(String coinType, String accountID,
                             String hashTransaction) {

        if(coinType.equals("00")) {
            flag = 3;
        } else {
            flag = 5;
        }


        CLA = "80";
        INS = "E4";
        P1 = "00";
        P2 = coinType; // `00`表示?比特币；`3C`表示以太坊
        LC = "44";
        DATE = accountID + hashTransaction;
        LE = "44";
        StrApdu = CLA + INS + P1 + P2 + LC + DATE + LE;
        log("send==>>" + Util.AddSpace(StrApdu));
        final byte[] Apdudata = Util.hexStringToByte(StrApdu.toUpperCase()
                .replace(" ", ""));
        begintime = System.currentTimeMillis();
        mController.exchangeTransparentData(Apdudata, Apdudata.length);
    }

    /**
     * seed恢复
     *
     * @param key
     *            用户支付密码
     * @param seedCiphertext
     *            seed密文
     * @return 以TLV格式返回恢复结果（成功0/失败1）
     */
    public void recoveryKey(String key, String seedCiphertext) {
        flag = 6;

        CLA = "80";
        INS = "E6";
        P1 = "00";
        P2 = "00";
        LC = "50";
        DATE = key + seedCiphertext;
        LE = "04";
        StrApdu = CLA + INS + P1 + P2 + LC + DATE + LE;
        log("send==>>" + Util.AddSpace(StrApdu));
        final byte[] Apdudata = Util.hexStringToByte(StrApdu.toUpperCase()
                .replace(" ", ""));
        begintime = System.currentTimeMillis();
        mController.exchangeTransparentData(Apdudata, Apdudata.length);
    }

    public void pair() {
        byte[] key = "123456".getBytes();
        mController.pairDevice(key);
    }

    public void getCardId() {
        int err;
        if (null == mController) {
            log("Service is not ready!");
            return;
        }
        if (!mController.isReady()) {
            return;
        }
        if (mController.isBusy()) {
            return;
        }
        log("checking Card Id...");
        mTestCardId = true;
        err = mController.antennaControl(true);
        if (Data.ERROR_OK != err) {
            mTestCardId = false;
            log("command failed! code=" + errDesc(err));
            log(STR_LINE_COMMAND);
        }
    }


    public void stateChange(int old_state, int new_state, int error) {
        if (new_state == Data.STATE_SCANNING) {
            log("scanning device...");
        } else if (new_state == Data.STATE_CONNECTING_DEVICE) {
            log("conn: " + mController.getDeviceName());
            log("addr: " + mController.getDeviceAddress());
        } else if (new_state == Data.STATE_CONNECTING_SERVICE) {
        } else if (new_state == Data.STATE_END) {
            if (mScanMode) {
                log("scanning stop!");
                mScanMode = false;
            } else {
                log("disconnected! code=" + errDesc(error));
            }
            log(STR_LINE_SESSION);
        } else if (new_state == Data.STATE_READY) {
            log("connect ok!");
        }
    }

    public void ReSend() {
        final byte[] Apdudata = Util.hexStringToByte(StrApdu.toUpperCase()
                .replace(" ", ""));
        mController.exchangeTransparentData(Apdudata, Apdudata.length);
    }

    public boolean checkSessionStatus() {
        if (null == mController) {
            log("Service is not ready!");
            return false;
        }
        if (!mController.isReady()) {
            log("No device connected!");
            return false;
        }
        return true;
    }

    public void log(String str) {
        Util.log(str);
        Log.d(TAG, str);
    }

    private void setDataLength(int length) {
        mInputData.setDataLength(length);
    }

    public String errDesc(int err) {
        String desc = "";
        if (err == Data.ERROR_OK) {
            desc = "ok";
        } else if (err == Data.ERROR_DEVICE_DISCONNECTED) {
            desc = "device disconnected";
        } else if (err == Data.ERROR_USER_CANCEL) {
            desc = "user cancel";
        } else if (err == Data.ERROR_BUSY) {
            desc = "ble is busy";
        } else if (err == Data.ERROR_NO_DEVICE) {
            desc = "no device";
        } else if (err == Data.ERROR_NO_SERVICE) {
            desc = "no service";
        } else if (err == Data.ERROR_FAILED_TO_SET_CHARACTERISTIC) {
            desc = "write characteristic error";
        } else if (err == Data.ERROR_FAILED_TO_SET_DESCRIPTOR) {
            desc = "write descriptor error";
        } else if (err == Data.ERROR_NOT_READY) {
            desc = "not ready";
        } else if (err == Data.ERROR_NOT_SUPPORTED) {
            desc = "not supported";
        } else {
            desc = "" + err;
        }
        return desc;
    }

    public String ErrMessage(String Sw) {
        errmessage = "";
        if (Sw.equals("6E 00") == true) {
            errmessage = "CLA不合法";
        }
        if (Sw.equals("6D 00") == true) {
            errmessage = "INS不合法";
        }
        if (Sw.equals("6A 86") == true) {
            errmessage = "p1，p2参数不合法";
        }
        if (Sw.equals("67 00") == true) {
            errmessage = "Lc长度不正确";
        }
        if (Sw.equals("69 85") == true) {
            errmessage = "卡片SEED已存在";
        }
        if (Sw.equals("69 88") == true) {
            errmessage = "私钥不存在";
        }
        return errmessage;
    }

}
