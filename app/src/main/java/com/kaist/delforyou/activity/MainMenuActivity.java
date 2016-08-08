package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.kaist.delforyou.R;

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

import com.kaist.delforyou.app.AppConfig;
import com.kaist.delforyou.helper.SQLiteHandler;

/**
 * Created by user on 2016-07-23.
 */
public class MainMenuActivity extends Activity {
    private String email;
    private SQLiteHandler db;
    private PHP_GetDeliveryRequest taskPHP;
    private LinkedHashMap<String, HashMap<String, String>> deliveryFrom = new LinkedHashMap<>();
    private LinkedHashMap<String, HashMap<String, String>> deliveryTo = new LinkedHashMap<>();
    private HashMap<String, ArrayList<ListItem>> deliveryFromItems = new HashMap<>();
    private HashMap<String, ArrayList<ListItem>> deliveryToItems = new HashMap<>();
    private RadioButton fromDeliveryButton;
    private RadioButton toDeliveryButton;
    private ListView deliveryListView;
    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String[] deliveryIDs = deliveryFrom.keySet().toArray(new String[0]);
            Intent intent = new Intent(MainMenuActivity.this, DetailDeliveryActivity.class);
            intent.putExtra("deliveryid", Integer.parseInt(deliveryIDs[position]));
            startActivity(intent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenuactivity);

        fromDeliveryButton = (RadioButton)findViewById(R.id.radio0);
        toDeliveryButton = (RadioButton)findViewById(R.id.radio1);
        deliveryListView = (ListView)findViewById(R.id.deliveryRequestList);
        deliveryListView.setOnItemClickListener(listener);

        db = new SQLiteHandler(getApplicationContext());
        email = db.getUserDetails().get("email");

        taskPHP = new PHP_GetDeliveryRequest();
        taskPHP.execute(AppConfig.URL_GETDELIVERY);
    }

    public void sendDelivery(View v) {
        fillItemListView(deliveryFrom, deliveryFromItems);
        fromDeliveryButton.toggle();
    }

    //받는택배 버튼 눌렀을 시,
    public void receiveDelivery(View v) {
        fillItemListView(deliveryTo, deliveryToItems);
        toDeliveryButton.toggle();
    }

    public void refresh(View v) {
    // TODO:
    }

    //배송조회 버튼 눌렀을 시,
    public void deliveryView(View v) {

    }

    //예약접수 버튼 눌렀을 시,
    public void reservation(View v) {
        Intent intent = new Intent(MainMenuActivity.this, ReservationActivity.class);
        startActivity(intent);
    }
    //예약함 버튼 눌렀을 시,
    public void reservationHistory(View v) {
        Intent intent = new Intent(MainMenuActivity.this, ReservationHistoryActivity.class);
        startActivity(intent);
    }
    //설정 버튼 눌렀을 시,
    public void setting(View v) {
        Intent intent = new Intent(MainMenuActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    protected void fillItemListView(HashMap<String, HashMap<String, String>> deliveries,
                                    HashMap<String, ArrayList<ListItem>> deliveryItems) {
        ListView listView = (ListView)findViewById(R.id.deliveryRequestList);
        ListViewAdapter adapter = new ListViewAdapter();
        listView.setAdapter(adapter);

        for (String key : deliveries.keySet()) {
            HashMap<String, String> deliveryInfo = deliveries.get(key);
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

            String itemDescription = deliveryItems.get(key).get(0).getItemDescription();
            adapter.addItems(Integer.toString(month+1) + "." + Integer.toString(day), dayOfWeek,
                             itemDescription, deliveryInfo.get("name"), deliveryInfo.get("shipping"),
                             deliveryInfo.get("status"));
        }
    }

    private class PHP_GetDeliveryRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");

                String postParameters = "email="+email;
                //Send request
                DataOutputStream wr = new DataOutputStream (conn.getOutputStream ());
                wr.writeBytes (postParameters);
                wr.flush ();
                wr.close ();

                if (conn!=null){
                    conn.setConnectTimeout(10000);
                    Log.i("HOHO", "Response Code) "+ conn.getResponseCode());
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

        private void fillItemArray(JSONArray delivery, HashMap<String, HashMap<String, String>> deliveries,
                                   HashMap<String, ArrayList<ListItem>> deliveryItems) {
            try {
                JSONObject jo0 = delivery.getJSONObject(0);
                Log.i("HOHO", "Before loop) " + jo0.toString());

                for (int index = 0; index < delivery.length(); index++) {
                    JSONObject jo = delivery.getJSONObject(index);
                    if (deliveries.containsKey(jo.getString("id"))) {
                        ListItem it = new ListItem(jo.getString("time"), "Thuesday", jo.getString("item"),
                                jo.getString("status"), jo.getString("category"), "");
                        deliveryItems.get(jo.getString("id")).add(it);
                    } else {
                        HashMap<String, String> list = new HashMap<>();
                        list.put("name", jo.getString("name"));
                        list.put("shipping", jo.getString("shipping"));
                        list.put("time", jo.getString("time"));
                        list.put("status", jo.getString("status"));
                        deliveries.put(jo.getString("id"), list);

                        ArrayList<ListItem> items = new ArrayList<>();
                        ListItem it = new ListItem(jo.getString("time"), "Thuesday", jo.getString("item"),
                                jo.getString("status"), jo.getString("category"), "");
                        items.add(it);
                        deliveryItems.put(jo.getString("id"), items);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(String str){
            try {
                JSONObject root = new JSONObject(str);
                JSONObject results = root.getJSONObject("results");
                JSONArray from = results.getJSONArray("from");
                JSONArray to = results.getJSONArray("to");

                fillItemArray(from, deliveryFrom, deliveryFromItems);
                fillItemArray(to, deliveryTo, deliveryToItems);

                fillItemListView(deliveryFrom, deliveryFromItems);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
