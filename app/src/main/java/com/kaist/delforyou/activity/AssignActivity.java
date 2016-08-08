package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.kaist.delforyou.R;
import com.kaist.delforyou.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by user on 2016-07-30.
 */
public class AssignActivity extends Activity {
    private PHP_GetUnassignedItem taskPHP;
    private LinkedHashMap<String, HashMap<String, String>> unassignedItems = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignactivity);

        taskPHP = new PHP_GetUnassignedItem();
        taskPHP.execute(AppConfig.URL_GETUNASSIGNED);
    }

    //업데이트 버튼 눌렀을 시,
    public void updateDelivery(View v) {
        // TODO:
    }

    //배송조회 버튼 눌렀을 시,
    public void deliveryView(View v) {
        Intent intent = new Intent(AssignActivity.this, MainMenuActivityforDeliveryMen.class);
        startActivity(intent);
    }

    public void assignJobToMe(View v) {
        // TODO
    }

    //예약함 버튼 눌렀을 시,
    public void reservationHistory(View v) {
        Intent intent = new Intent(AssignActivity.this, ReservationHistoryActivity.class);
        startActivity(intent);
    }

    //설정 버튼 눌렀을 시,
    public void setting(View v) {
        Intent intent = new Intent(AssignActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    protected void fillItemListView() {
        ListView listView = (ListView)findViewById(R.id.unassignedItemList);
        UnassignedListViewAdapter adapter = new UnassignedListViewAdapter();
        listView.setAdapter(adapter);

        for (String key : unassignedItems.keySet()) {
            HashMap<String, String> deliveryInfo = unassignedItems.get(key);
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            int month = 0;
            int day = 0;
            String dayOfWeek = "";
            try {
                date = transFormat.parse(deliveryInfo.get("time"));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, getResources().getConfiguration().locale);
            } catch (ParseException e) {
                // TODO::
            }

            String itemDescription = unassignedItems.get(key).get("item");
            adapter.addItems(Integer.toString(month+1) + "." + Integer.toString(day), dayOfWeek,
                    itemDescription, deliveryInfo.get("pickup"), deliveryInfo.get("shipping"));
        }
    }

    private class PHP_GetUnassignedItem extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");

                if (conn!=null){
                    conn.setConnectTimeout(10000);
                    Log.i("HOHO", "Response Code from AssignActivity) "+ conn.getResponseCode());
                    //conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;) {
                            String line = br.readLine();
                            if (line == null) break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str){
            Log.i("HOHO", "Unassigned)" + str);
            try {
                JSONObject root = new JSONObject(str);
                JSONObject results = root.getJSONObject("results");
Log.i("HOHO", "Unassigned)" + results.toString());
                JSONArray unassigned = results.getJSONArray("unassigned");
Log.i("HOHO", "Unassigned)" + unassigned.toString());

                for (int index = 0; index < unassigned.length(); index++) {
                    JSONObject jo = unassigned.getJSONObject(index);
                    HashMap<String, String> list = new HashMap<>();
                    list.put("name", jo.getString("name"));
                    list.put("pickup", jo.getString("pickup"));
                    list.put("shipping", jo.getString("shipping"));
                    list.put("time", jo.getString("time"));
                    list.put("item", jo.getString("item"));
                    unassignedItems.put(jo.getString("id"), list);
                }

                fillItemListView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
