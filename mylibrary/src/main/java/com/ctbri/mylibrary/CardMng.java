package com.ctbri.mylibrary;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CardMng {
    public void checkStatus(String resCode){
        CardMngIfs<String> cardMngIfs = new CardMngIfs<>();

        cardMngIfs.submitInfo(new CardMngIfs.AnsyCallback<String>() {
            @Override
            public void AnsyLoader(String loder, String Url) {
//                String resCode = null;
//                //此处UI线程
//                Log.d("MainActivity",loder+"ffff");
//                parseJSONWithJSONObject(loder, resCode);
//                Log.d("MainFragment", resCode);

            }
        });
    }

    private JSONObject parseJSONWithJSONObject(String jsonData, String resCode){
        JSONObject jsonObject = null;
        try{
            jsonObject = new JSONObject(jsonData);
            return jsonObject;


//            JSONArray jsonArray = new JSONArray(jsonData);
//            for(int i = 0; i < jsonArray.length(); i++){
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                 resCode = jsonObject.getString("resCode");
////                String resMsg = jsonObject.getString("resMsg");
////                String result = jsonObject.getString("result");
//            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
}
