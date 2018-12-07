package cn.hyperchain.hitoken.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017-05-08.
 */

public class SignGenerate {

    private final static String key = "bf0286fed096b9ed3ad8e7aad04c4c02";

    public static String generate(HashMap<String, String> body) {
        ArrayList<String> valueList = new ArrayList<String>();
        for (Map.Entry<String, String> entry : body.entrySet()) {
            if (entry.getValue() != null)
                valueList.add(entry.getValue());
        }
        Collections.sort(valueList);
        String rs = "";
        for (String str : valueList) {
            rs += str;
        }
        rs += key;
        return DigestUtils.md5Hex(rs);
    }
}
