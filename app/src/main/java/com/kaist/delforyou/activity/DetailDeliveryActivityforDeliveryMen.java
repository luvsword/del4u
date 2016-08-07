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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by luvsword on 2016-08-06.
 */
public class DetailDeliveryActivityforDeliveryMen extends Activity {
    private static final String TAG = DetailDeliveryActivityforDeliveryMen.class.getSimpleName();
    private int deliveryId;
    private DetailDeliveryInfo DeliveryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detaildeliveryfordeliverymen);

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

        TextView textStatuslog1 = (TextView) findViewById(R.id.Dstatuslog1);
        TextView textStatuslog2 = (TextView) findViewById(R.id.Dstatuslog2);
        TextView textStatuslog3 = (TextView) findViewById(R.id.Dstatuslog3);
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
        TextView deliveryID = (TextView) findViewById(R.id.Ddeliveryid);
        TextView itemInfo = (TextView) findViewById(R.id.DitemDescription);
        TextView senderInfo = (TextView) findViewById(R.id.Dsenderinfo);
        TextView receiverInfo = (TextView) findViewById(R.id.Dreceiverinfo);
        TextView requestDate = (TextView) findViewById(R.id.DDrequestDate);
        TextView expectedDate = (TextView) findViewById(R.id.DDexpectedDate);
        TextView pickupLocation = (TextView) findViewById(R.id.DpickupLocation);
        TextView shippingLocation = (TextView) findViewById(R.id.DshippingLocation);

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
    //  사용자 정보 조회 버튼 눌렀을 시,
    public void displaySenderInfo(View v) {
        Intent intent = new Intent(DetailDeliveryActivityforDeliveryMen.this, DeliveryMenViewActivity.class);
        intent.putExtra("email", DeliveryInfo.senderEmail);
        startActivity(intent);
    }

    public void displayReceiverInfo(View v) {
        Intent intent = new Intent(DetailDeliveryActivityforDeliveryMen.this, DeliveryMenViewActivity.class);
        intent.putExtra("email", DeliveryInfo.receiverEmail);
        startActivity(intent);
    }

}