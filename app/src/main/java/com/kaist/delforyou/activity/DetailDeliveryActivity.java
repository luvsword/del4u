package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kaist.delforyou.R;
import com.kaist.delforyou.app.AppConfig;
import com.kaist.delforyou.helper.DatabaseAsynTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by user on 2016-07-23.
 */
public class DetailDeliveryActivity extends Activity {
    private static final String TAG = DetailDeliveryActivity.class.getSimpleName();
    private int deliveryId;
    private DetailDeliveryInfo DeliveryInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detaildelivery);

        String res = null;
        JSONObject info = new JSONObject();
        DeliveryInfo = new DetailDeliveryInfo();
        DeliveryInfo.itemList = new ArrayList<String>();
        DatabaseAsynTask dbcon = new DatabaseAsynTask();

        Intent intent = getIntent();
        if (intent != null) {
            deliveryId = intent.getIntExtra("deliveryid", 0);
            Log.d(TAG, "deliveryId = " + deliveryId);
        }

        DeliveryInfo.deliveryId = deliveryId;
        DeliveryInfo.getJSONData(info);

        try {
            res = dbcon.execute(info).get();
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        }

        try {
            JSONObject root = new JSONObject(res);
            JSONObject result = root.getJSONObject("results");
            DeliveryInfo.setJSONData(result);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        populateDetailDeliveryInfo();
        populateStatusLog();

    }

    void populateStatusLog(){
        String rest = null;
        JSONObject info = new JSONObject();

        TextView textStatuslog1 = (TextView) findViewById(R.id.statuslog1);
        TextView textStatuslog2 = (TextView) findViewById(R.id.statuslog2);
        TextView textStatuslog3 = (TextView) findViewById(R.id.statuslog3);
        try {
            info.put("connectType", "getstatusinfo");
            info.put("serverInfo", AppConfig.URL_GETSTATUSINFO);
            info.put("deliveryid", deliveryId);
        } catch (JSONException e){
            e.printStackTrace();
        }

        DatabaseAsynTask dbcon = new DatabaseAsynTask();
        try {
            rest = dbcon.execute(info).get();
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        }

        try {
            JSONObject root = new JSONObject(rest);
            JSONObject result = root.getJSONObject("results");
            JSONArray statusLog = result.getJSONArray("statusInfo");

            for(int index=0; index<statusLog.length(); index++) {
                String time;
                info = statusLog.getJSONObject(index);
                time = info.getString("time");
                Log.d(TAG, "info = " + info.getString("statusdesc") +info.getString("time") );
                if (index == 0) {
                    textStatuslog1.setText(time);
                } else if (index == 1) {
                    textStatuslog2.setText(time);
                } else if (index == 2) {
                    textStatuslog3.setText(time);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    void populateDetailDeliveryInfo() {
        StringBuilder itemText = new StringBuilder();
        TextView deliveryID = (TextView) findViewById(R.id.deliveryid);
        TextView itemInfo = (TextView) findViewById(R.id.itemDescription);
        TextView senderInfo = (TextView) findViewById(R.id.senderinfo);
        TextView receiverInfo = (TextView) findViewById(R.id.receiverinfo);
        TextView requestDate = (TextView) findViewById(R.id.DrequestDate);
        TextView expectedDate = (TextView) findViewById(R.id.DexpectedDate);
        TextView pickupLocation = (TextView) findViewById(R.id.pickupLocation);
        TextView shippingLocation = (TextView) findViewById(R.id.shippingLocation);

        Log.d(TAG, "itemlist size = " + DeliveryInfo.itemList.size());

        for(int i =0; i < DeliveryInfo.itemList.size(); i++) {
                itemText.append(DeliveryInfo.itemList.get(i).toString()).append("\n");
        }
        deliveryID.setText(String.valueOf(DeliveryInfo.deliveryId));
        itemInfo.setText(itemText);
        senderInfo.setText(DeliveryInfo.senderInfo);
        receiverInfo.setText(DeliveryInfo.receiverInfo);
        requestDate.setText(DeliveryInfo.requestDate);
        expectedDate.setText(DeliveryInfo.expectedDate);
        pickupLocation.setText(DeliveryInfo.pickupLocInfo);
        shippingLocation.setText(DeliveryInfo.shippingLocInfo);

    }
    //  배달원 정보 조회 버튼 눌렀을 시,
    public void deliveryMenView(View v) {
        Intent intent = new Intent(DetailDeliveryActivity.this, DeliveryMenViewActivity.class);
        startActivity(intent);
    }

}
