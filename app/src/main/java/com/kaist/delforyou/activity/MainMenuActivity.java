package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2016-07-23.
 */
public class MainMenuActivity extends Activity {
    private ListView deliveryList;
    PHP_GetDeliveryRequest taskPHP;
    HashMap<String, HashMap<String, String>> deliveries = new HashMap<>();
    HashMap<String, ArrayList<Item>> deliveryItems = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenuactivity);

        taskPHP = new PHP_GetDeliveryRequest();
        taskPHP.execute("http://125.131.73.146/delivery_list.php");

        deliveryList = (ListView)findViewById(R.id.list_item);
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
        Log.i("HOHO", "Now, populate item list!");
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
                        Log.i("HOHO", "Not here2 ?");
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
                Log.i("HOHO", "results -> " + results.toString());
                JSONArray delivery = results.getJSONArray("delivery");
                Log.i("HOHO", "delivery item count) " + delivery.length());

                for (int index=0; index<delivery.length(); index++){
                    JSONObject jo = delivery.getJSONObject(index);
                    if (deliveries.containsKey(jo.getString("id"))) {
                        ArrayList<Item> items = new ArrayList<>();
                        Item it = new Item(jo.getString("item"), jo.getString("category"),
                                jo.getInt("count"), jo.getString("dimension"));
                        deliveryItems.get(jo.getString("id")).add(it);
                    } else {
                        HashMap<String, String> list = new HashMap<>();
                        list.put("name", jo.getString("name"));
                        list.put("shipping", jo.getString("shipping"));
                        deliveries.put(jo.getString("id"), list);

                        ArrayList<Item> items = new ArrayList<>();
                        Item it = new Item(jo.getString("item"), jo.getString("category"),
                                           jo.getInt("count"), jo.getString("dimension"));
                        items.add(it);
                        deliveryItems.put(jo.getString("id"), items);
                    }
                }
                fillItemList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Item {
        private String description;
        private String category;
        private int count;
        private String dimensions;

        public Item(String description, String category, int count, String dimensions) {
            this.description = description;
            this.category = category;
            this.count = count;
            this.dimensions = dimensions;
        }
    }
}
