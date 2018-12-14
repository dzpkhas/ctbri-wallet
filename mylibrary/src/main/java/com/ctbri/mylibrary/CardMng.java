package com.ctbri.mylibrary;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CardMng {

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
