package cn.hyperchain.hitoken.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Administrator on 2016/7/19.
 */
public class DataUtils {
    /** 获取屏幕的宽度 */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }


    /**
     * dip转为 px
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px 转为 dip
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dip转化像素
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getSystem().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);

    }

    /**
     * RoomDevice中fCode从10进制转换到16进制
     *
     * @param ten
     * @return
     */
    public static String ten2hex_fCode(int ten) {
        String result = Integer.toHexString(ten);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8 - result.length(); i++) {
            sb.append(0);
        }
        sb.append(result);
        return sb.toString().toUpperCase();
    }

    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    // 过滤特殊字符
    public static String StringFilterAll(String str)
            throws PatternSyntaxException {
        // 只允许字母和数字
        // String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 判断字符串是否为手机号码</br> 只能判断是否为大陆的手机号码
     *
     * @param str
     * @return
     */
    public static boolean checkMobile(String str) {
        Pattern p = Pattern.compile("1[34578][0-9]{9}");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isCellPhoneNumber(String str) {
        return checkMobile(str);
    }

    /**
     * 验证email的合法性
     *
     * @param emailStr
     * @return
     */
    public static boolean checkEmail(String emailStr) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(emailStr.trim());
        boolean isMatched = matcher.matches();
        if (isMatched) {
            return true;
        } else {
            return false;
        }
    }
}
