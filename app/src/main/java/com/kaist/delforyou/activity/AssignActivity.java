package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kaist.delforyou.R;
import com.kaist.delforyou.app.AppConfig;
import com.kaist.delforyou.app.AppController;
import com.kaist.delforyou.helper.DatabaseAsynTask;
import com.kaist.delforyou.helper.SQLiteHandler;

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
import java.util.Map;

/**
 * Created by user on 2016-07-30.
 */
public class AssignActivity extends Activity {
    private static final String TAG = AssignActivity.class.getSimpleName();
    private PHP_GetUnassignedItem taskPHP;
    private LinkedHashMap<String, HashMap<String, String>> unassignedItems = new LinkedHashMap<>();
    private ListView unassignedItemListView;
    private String email;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assignactivity);

        db = new SQLiteHandler(getApplicationContext());
        email = db.getUserDetails().get("email");

        unassignedItemListView = (ListView)findViewById(R.id.unassignedItemList);
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

    private void insertStatusLog(final String deliveryid, final String status, final String time, final String owner) {
        // Tag used to cancel the request
        String tag_string_req = "req_insertStatusLog";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_INSERTSTATUSLOG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Insert Status Log Response: " + response.toString());
                        //hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
Log.i("HOHO", "Success ? status log insert ?");
                                Toast.makeText(getApplicationContext(), "Delivery assigned successfully!", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                // Error occurred in registration. Get the error message
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Insert Status Log Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();

                params.put("deliveryid", deliveryid);
                params.put("status", status);
                params.put("time", time);
                params.put("owner", owner);

                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void assignJobToMe(View v) {
        ArrayList<String> checkedDeliveryIDs = new ArrayList<>();
        String[] keys = unassignedItems.keySet().toArray(new String[0]);
        int index = 0;
        for (index = 0; index < unassignedItemListView.getCount(); index++) {
            CheckBox cb = (CheckBox) unassignedItemListView.getChildAt(index).findViewById(R.id.assignCheckBox);
            if (cb.isChecked()) {
                checkedDeliveryIDs.add(keys[index]);
            }
        }
        Log.i("HOHO", "IDs count) " + checkedDeliveryIDs.size());

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        for (index = 0; index < checkedDeliveryIDs.size(); index++) {
            Date now = new Date();
            String strDate = sdfDate.format(now);
            insertStatusLog(checkedDeliveryIDs.get(index), "002", strDate, email) ;
        }
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
            try {
                JSONObject root = new JSONObject(str);
                JSONObject results = root.getJSONObject("results");
                JSONArray unassigned = results.getJSONArray("unassigned");

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
