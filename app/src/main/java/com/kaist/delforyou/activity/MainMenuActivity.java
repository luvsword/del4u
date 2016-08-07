package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kaist.delforyou.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.kaist.delforyou.app.AppConfig;

/**
 * Created by user on 2016-07-23.
 */
public class MainMenuActivity extends Activity {
    private ListView deliveryList;
    PHP_GetDeliveryRequest taskPHP;
    HashMap<String, HashMap<String, String>> deliveries = new HashMap<>();
    HashMap<String, ArrayList<ListItem>> deliveryItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenuactivity);

        taskPHP = new PHP_GetDeliveryRequest();
        taskPHP.execute(AppConfig.URL_GETDELIVERY);
    }

    //보낸택배 버튼 눌렀을 시,
    public void sendDlivery(View v) {
    // TODO: 보낸 택배 목록만 보여줘야함
    }

    //받는택배 버튼 눌렀을 시,
    public void receiveDlivery(View v) {
    // TODO: 받는 택배 목록만 보여줘야함
    }

    //정렬 버튼 눌렀을 시,
    public void sort(View v) {
    // TODO:사람이름 or 날짜 순으로 정렬해야함
    }

    // TODO: EditTextView에 DB에서 각각의 배송내역들 읽어와서 내용보여줘야함

    // 'TextView1 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView1(View v) {
        Intent intent = new Intent(MainMenuActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // 'TextView2 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView2(View v) {
        Intent intent = new Intent(MainMenuActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // 'TextView3 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView3(View v) {
        Intent intent = new Intent(MainMenuActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // '배송내역 더보기' 버튼 눌렀을 시,
    public void moreDeliveryVeiw(View v) {
    // TODO: 배송 내역 더 보여줘야함
    }

    //배송조회 버튼 눌렀을 시,
    public void dliveryView(View v) {

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

    protected void fillItemList() {
        ListView listView;
        ListViewAdapter adapter = new ListViewAdapter();

        listView = (ListView)findViewById(R.id.deliveryRequestList);
        listView.setAdapter(adapter);

        Log.i("HOHO", "After setAdapter..");
        for (String key : deliveries.keySet()) {
            HashMap<String, String> deliveryInfo = deliveries.get(key);
            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date;
            int month = 0;
            int day = 0;
            int dayOfWeek = 0;
            String[] namesOfDays = DateFormatSymbols.getInstance().getShortWeekdays();
            Log.i("HOHO", "here ?");
            try {
                date = transFormat.parse(deliveryInfo.get("time"));
                Log.i("HOHO", "Or, here ?");
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                Log.i("HOHO", "XXXXXXXXXXX?");
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            } catch (ParseException e) {
                // TODO::
            }

            String itemDescription = deliveryItems.get(key).get(0).getItemDescription();
            adapter.addItems(Integer.toString(month+1) + "." + Integer.toString(day),
                             namesOfDays[dayOfWeek], itemDescription,
                             deliveryInfo.get("name"), deliveryInfo.get("shipping"));
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

                String postParameters = "email=delforyou@kaist.ac.kr";
                //Send request
                DataOutputStream wr = new DataOutputStream (conn.getOutputStream ());
                wr.writeBytes (postParameters);
                wr.flush ();
                wr.close ();

                //conn.setFixedLengthStreamingMode(postParameters.getBytes().length);
                //PrintWriter out = new PrintWriter(conn.getOutputStream());
                //out.print(postParameters);
                //out.close();

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
            return
                    jsonHtml.toString();
        }
        protected void onPostExecute(String str){
            try {
                JSONObject root = new JSONObject(str);
                JSONObject results = root.getJSONObject("results");
                JSONArray delivery = results.getJSONArray("delivery");
                Log.i("HOHO", "delivery item count) " + delivery.length());

                for (int index=0; index<delivery.length(); index++){
                    JSONObject jo = delivery.getJSONObject(index);
                    Log.i("HOHO", "Inside loop) " + jo.toString());
                    if (deliveries.containsKey(jo.getString("id"))) {
                        ListItem it = new ListItem(jo.getString("time"), "Thuesday", jo.getString("item"),
                                jo.getString("description"), jo.getString("category"));
                        deliveryItems.get(jo.getString("id")).add(it);
                    } else {
                        HashMap<String, String> list = new HashMap<>();
                        list.put("name", jo.getString("name"));
                        list.put("shipping", jo.getString("shipping"));
                        list.put("time", jo.getString("time"));
                        deliveries.put(jo.getString("id"), list);

                        ArrayList<ListItem> items = new ArrayList<>();
                        //ListItem it = new ListItem("haha", "hoho", "abc", "XYZ", "KK");
                        ListItem it = new ListItem(jo.getString("time"), "Thuesday", jo.getString("item"),
                                           jo.getString("description"), jo.getString("category"));
                        items.add(it);
                        deliveryItems.put(jo.getString("id"), items);
                    }
                }
                Log.i("HOHO", "You see here?");
                fillItemList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
