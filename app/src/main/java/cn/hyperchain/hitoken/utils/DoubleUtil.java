package cn.hyperchain.hitoken.utils;

import java.text.DecimalFormat;

public class DoubleUtil {

    public static String doubleToString(double num){
        //使用0.00不足位补0，#.##仅保留有效位
        return new DecimalFormat("0.0000").format(num);
    }
}
