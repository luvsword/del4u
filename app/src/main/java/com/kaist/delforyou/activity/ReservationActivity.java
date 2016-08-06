package com.kaist.delforyou.activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.app.Dialog;
import android.view.Menu;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kaist.delforyou.R;
import com.kaist.delforyou.app.AppConfig;
import com.kaist.delforyou.app.AppController;
import com.kaist.delforyou.helper.DatabaseAsynTask;
import com.kaist.delforyou.helper.SQLiteHandler;
import com.kaist.delforyou.helper.SessionManager;
import com.kaist.delforyou.activity.Category;
import com.kaist.delforyou.activity.RequestDelivery;
import org.w3c.dom.Text;

import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import android.widget.Spinner;
import android.widget.ArrayAdapter;

/**
 * Created by user on 2016-07-23.
 */
public class ReservationActivity extends Activity {
    private static final String TAG = ReservationActivity.class.getSimpleName();
    private Calendar calendar;
    private TextView requestDateView, requestTimeView;
    private TextView expectedDateView, expectedTimeView;

    private int datePickerId = 999;
    private int timePickerId = 998;
    private int dateExpectedPickerId = 997;
    private int timeExpectedPickerId = 996;
    private SQLiteHandler db;
    private Spinner senderSpinnerCompany;
    private Spinner senderSpinnerDivision;
    private Spinner senderSpinnerBuilding;

    private String userGroupId;
    private String userCompanyId;
    private String userDivisionId;
    private String userBuildingId;
    private String email;
    private String name;
    private String phone;

    private TextView userName;
    private TextView userPhone;
    private Spinner receiverSpinnerCompany;
    private Spinner receiverSpinnerDivision;
    private Spinner receiverSpinnerBuilding;

    private String receiverCompanyId;
    private String receiverDivisionId;
    private String receiverBuildingId;
    private RequestDelivery RDObject;

    private Category comInfo;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private JSONObject info;
    //    Button reservationCancelButton = (Button) findViewById(R.id.reservationCancelButton);
    //  Button reservationConfirmButton = (Button) findViewById(R.id.reservationConfirmButton);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation);

        info = new JSONObject();

        db = new SQLiteHandler(getApplicationContext());

        RDObject = new RequestDelivery();
        RDObject.itemList = new ArrayList<RequestDelivery.DeliveryItem>();
        comInfo = new Category();

        comInfo.groups = new HashMap<>();
        comInfo.companies = new HashMap<>();
        comInfo.divisions = new HashMap<>();
        comInfo.buildings = new HashMap<>();

        if (getComInfoList()) {
            Log.d(TAG, "GET Com Info List");
        } else {
            Log.e(TAG, "Failed to get com info list");
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        email = user.get("email");
        name = user.get("lname") + user.get("fname");
        phone = user.get("phone");

        if (getGroupId()) {
            Log.d(TAG, "GET groupid");
        } else {
            Log.e(TAG, "Failed to get groupid");
        }
        RDObject.senderEmail = email;

        Log.d(TAG, "name = " + name + " email = " + email);

        /* 보내는 사람 정보 출력 */
        userName = (TextView) findViewById(R.id.user_name_text);
        userName.setText(name);

        userPhone = (TextView) findViewById(R.id.user_phone_text);
        userPhone.setText(phone);

        requestDateView = (TextView) findViewById(R.id.visitDate);
        requestTimeView = (TextView) findViewById(R.id.visitTime);

        expectedDateView = (TextView) findViewById(R.id.expectedDate);
        expectedTimeView = (TextView) findViewById(R.id.expectedTime);

        /* pickup date 를 위한 DatePicker, TimePicker 등록 */
        calendar = Calendar.getInstance();

        showDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        showTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        showExpectedDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        showExpectedTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

        /* pickup & shipping location 선택을 위한 spinner 등록 */
        senderSpinnerCompany = (Spinner) findViewById(R.id.user_spinner_company);
        senderSpinnerDivision = (Spinner) findViewById(R.id.user_spinner_division);
        senderSpinnerBuilding = (Spinner) findViewById(R.id.user_spinner_building);

        receiverSpinnerCompany = (Spinner) findViewById(R.id.receiver_spinner_company);
        receiverSpinnerDivision = (Spinner) findViewById(R.id.receiver_spinner_division);
        receiverSpinnerBuilding = (Spinner) findViewById(R.id.receiver_spinner_building);

        populateComSpinner(userGroupId);
        populateRComSpinner(userGroupId);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public boolean getGroupId() {
        String res = null;
        JSONObject g = new JSONObject();
        DatabaseAsynTask dbcon = new DatabaseAsynTask();

        try {
            g.put("email", email);
            g.put("type", "group");
            g.put("connectType", "getgroupid");
            g.put("serverInfo", AppConfig.URL_GETCATINFO);
        } catch (JSONException e){
            e.printStackTrace();
            return false;
        }

        try {
            res = dbcon.execute(g).get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        }

        Log.d(TAG, "result = " + res.toString());
        try {
            JSONObject root = new JSONObject(res);
            boolean error = root.getBoolean("error");

            if (!error) {
                JSONObject user = root.getJSONObject("user");
                userGroupId = user.getString("groupid");
                Log.d(TAG, "User's Group ID = " + userGroupId);
            } else {
                // Error occurred in registration. Get the error
                // message
                String errorMsg = root.getString("error_msg");
                Toast.makeText(getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public boolean getComInfoList() {
        String result = null;
        DatabaseAsynTask dbControl = new DatabaseAsynTask();
        comInfo.getJSONData(info);

        try {
            result = dbControl.execute(info).get();
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        }

        try {
            JSONObject root = new JSONObject(result);
            JSONObject results = root.getJSONObject("results");

            JSONArray group = results.getJSONArray("group");
            Log.i(TAG, "groups.. " + Integer.toString(group.length()));
            for(int index=0; index<group.length(); index++){
                JSONObject jo = group.getJSONObject(index);
                comInfo.groups.put(jo.getString("groupid"), jo.getString("description"));
            }

            JSONArray company = results.getJSONArray("company");
            Log.i(TAG, "companies.. " + Integer.toString(company.length()));
            for (int index=0; index<company.length(); index++){
                JSONObject jo = company.getJSONObject(index);
                if (comInfo.companies.containsKey(jo.getString("groupid"))) {
                    comInfo.companies.get(jo.getString("groupid")).put(jo.getString("companyid"), jo.getString("description"));
                } else {
                    HashMap<String, String> list = new HashMap<>();
                    list.put(jo.getString("companyid"),jo.getString("description"));
                    comInfo.companies.put(jo.getString("groupid"), list);
                }
            }

            JSONArray division = results.getJSONArray("division");
            Log.i(TAG, "divisions.. " + Integer.toString(division.length()));
            for (int index=0; index<division.length(); index++){
                JSONObject jo = division.getJSONObject(index);
                if (comInfo.divisions.containsKey(jo.getString("companyid"))) {
                    comInfo.divisions.get(jo.getString("companyid")).put(jo.getString("divisionid"),jo.getString("description"));
                } else {
                    HashMap<String, String> list = new HashMap<>();
                    list.put(jo.getString("divisionid"), jo.getString("description"));
                    comInfo.divisions.put(jo.getString("companyid"), list);
                }
            }

            JSONArray building = results.getJSONArray("building");
            Log.i(TAG, "buildings.. " + Integer.toString(building.length()));
            for (int index=0; index<building.length(); index++){
                JSONObject jo = building.getJSONObject(index);
                if (comInfo.buildings.containsKey(jo.getString("divisionid"))) {
                    comInfo.buildings.get(jo.getString("divisionid")).put(jo.getString("buildingid"),jo.getString("buildingname"));
                } else {
                    HashMap<String, String> list = new HashMap<>();
                    list.put(jo.getString("buildingid"), jo.getString("buildingname"));
                    comInfo.buildings.put(jo.getString("divisionid"), list);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(result.equals("true"))
            return true;
        else
            return false;
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(datePickerId);
    }

    @SuppressWarnings("deprecation")
    public void setExpectedDate(View view) {
        showDialog(dateExpectedPickerId);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == datePickerId) {
            return new DatePickerDialog(this, myDateListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        } else if (id == timePickerId) {
            return new TimePickerDialog(this, myTimeListerner,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        } else if (id == dateExpectedPickerId) {
            return new DatePickerDialog(this, myExpectedDateListener,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        } else if (id == timeExpectedPickerId) {
            return new TimePickerDialog(this, myExpectedTimeListerner,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            RDObject.requestYear = year;
            RDObject.requestMonth = month + 1;
            RDObject.requestDay = day;
            showDate(year, month + 1, day);
        }
    };

    private DatePickerDialog.OnDateSetListener myExpectedDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            RDObject.expectedYear = year;
            RDObject.expectedMonth = month + 1;
            RDObject.expectedDay = day;
            // TODO Auto-generated method stub
            showExpectedDate(year, month + 1, day);
        }
    };

    private void showDate(int year, int month, int day) {
        requestDateView.setText(new StringBuilder().append(year).append("/").append(month).append("/")
                .append(day));
    }

    private void showExpectedDate(int year, int month, int day) {
        expectedDateView.setText(new StringBuilder().append(year).append("/").append(month).append("/")
                .append(day));
    }

    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(timePickerId);
    }

    @SuppressWarnings("deprecation")
    public void setExpectedTime(View view) {
        showDialog(timeExpectedPickerId);
    }

    private TimePickerDialog.OnTimeSetListener myTimeListerner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            RDObject.requestHour = hourOfDay;
            RDObject.requestMinutes = minute;
            showTime(hourOfDay, minute);
        }
    };

    private TimePickerDialog.OnTimeSetListener myExpectedTimeListerner = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            RDObject.expectedHour = hourOfDay;
            RDObject.expectedMinutes = minute;
            showExpectedTime(hourOfDay, minute);
        }
    };

    private void showTime(int hourOfDay, int minute) {
        requestTimeView.setText(new StringBuilder().append(hourOfDay).append("::").append(minute));
    }

    private void showExpectedTime(int hourOfDay, int minute) {
        expectedTimeView.setText(new StringBuilder().append(hourOfDay).append("::").append(minute));
    }
    protected void populateComSpinner(String groupID) {
        Log.i(TAG, "groupID.. " + groupID);
        final HashMap<String, String> companyHashMap = comInfo.companies.get(groupID);
        final ArrayList<String> companyList = new ArrayList<>(companyHashMap.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, companyList);

        senderSpinnerCompany.setAdapter(adapter);
        senderSpinnerCompany.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position= senderSpinnerCompany.getSelectedItemPosition();
                        Toast.makeText(getApplicationContext(), "You select " + companyList.toArray()[+position],Toast.LENGTH_SHORT).show();
                        populateDivSpinner(companyHashMap.keySet().toArray()[+position].toString());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
        );
    }

    protected void populateDivSpinner(String companyID) {
        Log.i(TAG, "companyID) " + companyID);
        final HashMap<String, String> divisionHashMap = comInfo.divisions.get(companyID);
        final ArrayList<String> divisionList = new ArrayList<>(divisionHashMap.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, divisionList);

        senderSpinnerDivision.setAdapter(adapter);
        senderSpinnerDivision.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position= senderSpinnerDivision.getSelectedItemPosition();
                        Toast.makeText(getApplicationContext(), "You select " + divisionList.toArray()[+position],Toast.LENGTH_SHORT).show();
                        populateBuildingSpinner(divisionHashMap.keySet().toArray()[+position].toString());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
        );
    }

    protected void populateBuildingSpinner(String divisionID) {
        Log.i(TAG, "divisionID) " + divisionID);
        Log.i(TAG, "buildingHashMap) " + comInfo.buildings.toString());
        final HashMap<String, String> buildingHashMap = comInfo.buildings.get(divisionID);

        if (buildingHashMap != null) {
            final ArrayList<String> buildingList = new ArrayList<>(buildingHashMap.values());
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buildingList);

            senderSpinnerBuilding.setAdapter(adapter);
            senderSpinnerBuilding.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            int position= senderSpinnerBuilding.getSelectedItemPosition();
                            Toast.makeText(getApplicationContext(), "You select " + buildingList.toArray()[+position],Toast.LENGTH_SHORT).show();
                            RDObject.pickupBuildingId = buildingHashMap.keySet().toArray()[+position].toString();
                            Log.d(TAG, "Building ID = " + RDObject.pickupBuildingId);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    }
            );
        } else {
            senderSpinnerBuilding.setAdapter(null);
        }

    }

    protected void populateRComSpinner(String groupID) {
        Log.i(TAG, "groupID.. " + groupID);
        final HashMap<String, String> companyHashMap = comInfo.companies.get(groupID);
        final ArrayList<String> companyList = new ArrayList<>(companyHashMap.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, companyList);

        receiverSpinnerCompany.setAdapter(adapter);
        receiverSpinnerCompany.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position= receiverSpinnerCompany.getSelectedItemPosition();
                        Toast.makeText(getApplicationContext(), "You select " + companyList.toArray()[+position],Toast.LENGTH_SHORT).show();
                        populateRDivSpinner(companyHashMap.keySet().toArray()[+position].toString());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
        );
    }

    protected void populateRDivSpinner(String companyID) {
        Log.i(TAG, "companyID) " + companyID);
        final HashMap<String, String> divisionHashMap = comInfo.divisions.get(companyID);
        final ArrayList<String> divisionList = new ArrayList<>(divisionHashMap.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, divisionList);

        receiverSpinnerDivision.setAdapter(adapter);
        receiverSpinnerDivision.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position= receiverSpinnerDivision.getSelectedItemPosition();
                        Toast.makeText(getApplicationContext(), "You select " + divisionList.toArray()[+position],Toast.LENGTH_SHORT).show();
                        populateRBuildingSpinner(divisionHashMap.keySet().toArray()[+position].toString());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
        );
    }


    protected void populateRBuildingSpinner(String divisionID) {
        Log.i(TAG, "divisionID) " + divisionID);
        Log.i(TAG, "buildingHashMap) " + comInfo.buildings.toString());
        final HashMap<String, String> buildingHashMap = comInfo.buildings.get(divisionID);

        if (buildingHashMap != null) {
            final ArrayList<String> buildingList = new ArrayList<>(buildingHashMap.values());
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buildingList);

            receiverSpinnerBuilding.setAdapter(adapter);
            receiverSpinnerBuilding.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            int position= receiverSpinnerBuilding.getSelectedItemPosition();
                            Toast.makeText(getApplicationContext(), "You select " + buildingList.toArray()[+position],Toast.LENGTH_SHORT).show();
                            RDObject.shippingBuildingId = buildingHashMap.keySet().toArray()[+position].toString();
                            Log.d(TAG, "Building ID = " + RDObject.shippingBuildingId);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    }
            );
        } else {
            receiverSpinnerBuilding.setAdapter(null);
        }
    }

    // 요청사항 버튼 눌렀을 시,
    public void requirement(View v) {
        Intent intent = new Intent(ReservationActivity.this, RequirementActivity.class);
        startActivity(intent);
    }

    // 예약확인 버튼 눌렀을 시,
    public void reservationConfirm(View v) {

        String res = null;
        JSONObject tjson = new JSONObject();
        DatabaseAsynTask dbCon = new DatabaseAsynTask();

        RDObject.itemList.clear();

        TextView textInfo = (TextView) findViewById(R.id.receiver_email);
        RDObject.recipientEmail = textInfo.getText().toString();

        RDObject.senderEmail = email;

        textInfo = (TextView) findViewById(R.id.user_location);
        RDObject.pickupLocation = textInfo.getText().toString();

        textInfo = (TextView) findViewById(R.id.receiver_location);
        RDObject.shippingLocation = textInfo.getText().toString();

        RequestDelivery.DeliveryItem test1 = RDObject.new DeliveryItem();
        test1.categoryId = "000";
        test1.count = 3;
        test1.description = "LG G5";
        test1.dimension = "000";

        RequestDelivery.DeliveryItem test2 = RDObject.new DeliveryItem();
        test2.categoryId = "005";
        test2.count = 1;
        test2.description = "LG TV";
        test2.dimension = "004";

        RDObject.itemList.add(test1);
        RDObject.itemList.add(test2);

        RDObject.getJSONData(tjson);
        Log.d(TAG, "Reservation = " + tjson.toString());

        try {
            res = dbCon.execute(tjson).get();
        } catch (InterruptedException e) {
            return;
        } catch (ExecutionException e) {
            return;
        }

        try {
            JSONObject root = new JSONObject(res);
            JSONObject results = root.getJSONObject("result");

            String deliveryid = results.getString("deliveryid");

            Log.d(TAG, "deliveryID = " + deliveryid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // 예약취소 버튼 눌렀을 시,
    public void reservationCancel(View v) {
        Intent intent = new Intent(ReservationActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }
}

