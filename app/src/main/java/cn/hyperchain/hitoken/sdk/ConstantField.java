package cn.hyperchain.hitoken.sdk;

public final class ConstantField {
    public static final String CARDSTATUS0 = "0";   //白卡：初始化-无运营商密码
    public static final String CARDSTATUS1 = "1";   //未售未激活卡：生成密码、无客户信息，没有seed
    public static final String CARDSTATUS2 = "2";   //新发已激活卡：已保存seed；或新补卡，已重置种子—正常状态
    public static final String CARDSTATUS3 = "3";   //卡挂失，未补卡
    public static final String CARDSTATUS4 = "4";   //卡注销，退业务或已完成挂失补卡
    public static final String CARDSTATUS5 = "5";   //新补卡，已重置密码，seed未写入sim卡
    public static final String CARDSTATUS6 = "6";   //新发未激活卡：新用户办理，有运营商密码，有客户信息，无seed

    public static final int RESULT_CREATSEED = 1;
    public static final int RESULT_PUBLICKEY_BTC = 2;
    public static final int RESULT_SIGNATURE_BTC = 3;
    public static final int RESULT_PUBLICKEY_ETH = 4;
    public static final int RESULT_SIGNATURE_ETH = 5;

    public static final String CARDSYS_OK = "1";
    public static final String CARDSYS_ERROR = "0";


}
