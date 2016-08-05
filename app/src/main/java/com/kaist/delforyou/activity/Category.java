package com.kaist.delforyou.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.kaist.delforyou.app.AppConfig;

import java.util.HashMap;

/**
 * Created by luvsword on 2016-08-01.
 */
public class Category {

    public HashMap<String, String> groups;
    public HashMap<String, HashMap<String, String>> companies;
    public HashMap<String, HashMap<String, String>> divisions;
    public HashMap<String, HashMap<String, String>> buildings;

    public Category(){}

    public Boolean getJSONData(JSONObject info) {
        try {
            info.put("connectType", "getCatInfo");
            info.put("serverInfo", AppConfig.URL_GETCATINFO1);
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
