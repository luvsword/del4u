package com.kaist.delforyou.activity;

/**
 * Created by luvsword on 2016-08-06.
 */

import android.util.Log;

import com.kaist.delforyou.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class DetailDeliveryInfo {
    private static final String TAG = DetailDeliveryInfo.class.getSimpleName();
    public String senderEmail;
    public String receiverEmail;
    public String senderInfo; //(name + phone)
    public String receiverInfo; //(name + phone)
    public int deliveryId;

    public String pickupLocInfo; //building + location
    public String shippingLocInfo; //building + location

    public ArrayList<String> itemList;

    public String requestDate, expectedDate;

    public Boolean setJSONData(JSONObject result) {
        try {
            Log.d(TAG, "TEST");

            JSONArray deliveryInfos = result.getJSONArray("deliveryinfo");
            itemList.clear();
            for(int index=0; index<deliveryInfos.length(); index++) {
                JSONObject info = deliveryInfos.getJSONObject(index);
                deliveryId = info.getInt("deliveryid");
                senderInfo = info.getString("senderInfo"); //(name + phone)
                receiverInfo = info.getString("recipientInfo"); //(name + phone)
                pickupLocInfo = info.getString("pickupInfo"); //building + location
                shippingLocInfo = info.getString("shippingInfo"); //building + location
                requestDate = info.getString("requestdate");
                expectedDate = info.getString("expecteddeliverydate");
                senderEmail = info.getString("sender");
                receiverEmail = info.getString("recipient");
                itemList.add(info.getString("itemInfo"));
                Log.d(TAG, senderInfo + "/" + receiverInfo + "/" + pickupLocInfo + "/" + shippingLocInfo + "/" + requestDate + "/" + expectedDate + "/" + itemList.get(index));
            }
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public Boolean getJSONData(JSONObject info) {
        try {
            info.put("connectType", "getdeliveryinfo");
            info.put("serverInfo", AppConfig.URL_GETDELIVERYINFO);
            info.put("deliveryid", deliveryId);
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
