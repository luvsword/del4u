package com.kaist.delforyou.activity;

/**
 * Created by luvsword on 2016-07-31.
 */

import com.kaist.delforyou.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import android.util.Log;

public class RequestDelivery  {
    private static final String TAG = ReservationActivity.class.getSimpleName();
    public String senderEmail;
    public String recipientEmail;
    public String pickupBuildingId;
    public String pickupLocation;
    public String shippingBuildingId;
    public String shippingLocation;
    public String requestMessage;

    public int requestYear, requestMonth, requestDay;
    public int expectedYear, expectedMonth, expectedDay;
    public int requestHour, requestMinutes;
    public int expectedHour, expectedMinutes;

    public ArrayList<DeliveryItem> itemList;

    public RequestDelivery() {}

    public class DeliveryItem {
        public String categoryId;
        public String description;
        public int count;
        public String dimension;
    }

    public String getDateTimeformat(int year, int month, int day, int hour, int minutes) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Calendar c = Calendar.getInstance();
        c.set(year, month-1, day, hour, minutes);
        Log.d(TAG, "datetime = " + year +"," + month +"," + day  +"," +hour +  "," + minutes );
        // TODO Auto-generated method stub
        Log.d(TAG, "format date = " + f.format(c.getTimeInMillis()));
        return f.format(c.getTimeInMillis());
    }

    public Boolean getJSONData(JSONObject info) {
        try {
            info.put("connectType", "setreservation");
            info.put("serverInfo", AppConfig.URL_SETREVINFO);
            info.put("senderEmail", senderEmail);
            info.put("recipientEmail", recipientEmail);
            info.put("pickupBuildingId", pickupBuildingId);
            info.put("pickupLocation", pickupLocation);
            info.put("shippingBuildingId", shippingBuildingId);
            info.put("shippingLocation", shippingLocation);
            info.put("requestMessage", requestMessage);
            info.put("requestDate", getDateTimeformat(requestYear, requestMonth, requestDay, requestHour, requestMinutes));
            info.put("expectedDate", getDateTimeformat(expectedYear, expectedMonth, expectedDay, expectedHour, expectedMinutes));

            JSONArray jSonItemList = new JSONArray();
            Iterator<DeliveryItem> iterItemList = itemList.iterator();
            while(iterItemList.hasNext()){
                JSONObject iTemJSONData = new JSONObject();
                DeliveryItem item = iterItemList.next();
                if(item.description.isEmpty()){
                    iterItemList.remove();
                }
                else
                {
                    iTemJSONData.put("categoryId", item.categoryId);
                    iTemJSONData.put("description", item.description);
                    iTemJSONData.put("count", item.count);
                    iTemJSONData.put("dimension", item.dimension);
                    jSonItemList.put(iTemJSONData);
                }
            }
            info.put("itemList", jSonItemList);
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
