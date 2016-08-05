package com.kaist.delforyou.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kaist.delforyou.R;
import com.kaist.delforyou.app.AppConfig;
import com.kaist.delforyou.app.AppController;
import com.kaist.delforyou.helper.SQLiteHandler;
import com.kaist.delforyou.helper.SessionManager;

/**
 * Created by user on 2016-07-23.
 */
public class SignupActivity extends Activity {
    private static final String TAG = SignupActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnRegCancel;
    private EditText inputFname;
    private EditText inputLname;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputCPassword;
    private EditText inputPhoneNo;
    private EditText inputEID;
    private CheckBox checkboxDeliveryJob;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    PHP_GetCompnayInfo taskPHP;
    Spinner spinner_businessgroup;
    Spinner spinner_company;
    Spinner spinner_division;
    Spinner spinner_building;
    HashMap<String, String> groups = new HashMap<>();
    HashMap<String, HashMap<String, String>> companies = new HashMap<>();
    HashMap<String, HashMap<String, String>> divisions = new HashMap<>();
    HashMap<String, HashMap<String, String>> buildings = new HashMap<>();

    String divisionID;
    String buildingID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        taskPHP = new PHP_GetCompnayInfo();
        taskPHP.execute("http://125.131.73.146/company_info.php");

        inputFname = (EditText) findViewById(R.id.fname);
        inputLname = (EditText) findViewById(R.id.lname);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputCPassword = (EditText) findViewById(R.id.cpassword);
        inputPhoneNo = (EditText) findViewById(R.id.phoneno);
        inputEID = (EditText) findViewById(R.id.employeeid);
        btnRegister = (Button) findViewById(R.id.btnSignupReg);
        btnRegCancel = (Button) findViewById(R.id.btnSignupCancel);
        checkboxDeliveryJob = (CheckBox) findViewById(R.id.deliveryjob);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(SignupActivity.this,
                    MainMenuActivityforDeliveryMen.class);
            startActivity(intent);
            finish();
        }
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String fname = inputFname.getText().toString().trim();
                String lname = inputLname.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String cpassword = inputCPassword.getText().toString().trim();
                String phoneno = inputPhoneNo.getText().toString().trim();
                String eid = inputEID.getText().toString().trim();
                String divisionid = divisionID;
                String buildingid = buildingID;
                String jobposition = checkboxDeliveryJob.isChecked() ? "000" : "111";

                if (!password.equals(cpassword)) {
                    Toast.makeText(getApplicationContext(),
                            "Please check password!", Toast.LENGTH_LONG)
                            .show();
                }

                if (!fname.isEmpty() && !lname.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(fname, lname, email, password, phoneno, eid, divisionid, buildingid, jobposition);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnRegCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String fname, final String lname, final String email,
                              final String password, final String phoneno, final String eid,
                              final String divisionid, final String buildingid, final String jobposition) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Register Response: " + response.toString());
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");
                        if (!error) {
                            // User successfully stored in MySQL. Now store the user in sqlite
                            // String uid = jObj.getString("uid");

                            JSONObject user = jObj.getJSONObject("user");
                            String fname = user.getString("firstname");
                            String lname = user.getString("lastname");
                            String email = user.getString("email");
                            String phone = user.getString("phoneno");
                            String jobposition = user.getString("jobposition");
                            db.addUser(fname, lname, email, eid, phone, jobposition);

                            Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                            // Launch login activity
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
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
                    Log.e(TAG, "Registration Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("employeeid", eid);
                    params.put("firstname", fname);
                    params.put("lastname", lname);
                    params.put("phoneno", phoneno);
                    params.put("divisionid", divisionid);
                    params.put("buildingid", buildingid);
                    params.put("jobposition", jobposition);
                    params.put("email", email);
                    params.put("password", password);

                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    protected void fillGroupList() {
        final ArrayList<String> groupList = new ArrayList<>(groups.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupList);
        spinner_businessgroup = (Spinner)findViewById(R.id.businessgroup);
        spinner_businessgroup.setAdapter(adapter);
        spinner_businessgroup.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    int position= spinner_businessgroup.getSelectedItemPosition();
                    Toast.makeText(getApplicationContext(), "You select " + groupList.toArray()[+position],Toast.LENGTH_SHORT).show();
                    fillCompanySpinner(groups.keySet().toArray()[+position].toString());
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            }
        );
    }

    protected void fillCompanySpinner(String groupID) {
        final HashMap<String, String> companyHashMap = companies.get(groupID);
        final ArrayList<String> companyList = new ArrayList<>(companyHashMap.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, companyList);
        spinner_company = (Spinner)findViewById(R.id.company);
        spinner_company.setAdapter(adapter);
        spinner_company.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    int position= spinner_company.getSelectedItemPosition();
                    Toast.makeText(getApplicationContext(), "You select " + companyList.toArray()[+position],Toast.LENGTH_SHORT).show();
                    fillDivisionSpinner(companyHashMap.keySet().toArray()[+position].toString());
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            }
        );
    }

    protected void fillDivisionSpinner(String companyID) {
        final HashMap<String, String> divisionHashMap = divisions.get(companyID);
        final ArrayList<String> divisionList = new ArrayList<>(divisionHashMap.values());
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, divisionList);
        spinner_division = (Spinner)findViewById(R.id.division);
        spinner_division.setAdapter(adapter);
        spinner_division.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position= spinner_division.getSelectedItemPosition();
                        Toast.makeText(getApplicationContext(), "You select " + divisionList.toArray()[+position],Toast.LENGTH_SHORT).show();
                        divisionID = divisionHashMap.keySet().toArray()[+position].toString();
                        fillBuildingSpinner(divisionID);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
        );
    }

    protected void fillBuildingSpinner(String divisionID) {
        final HashMap<String, String> buildingHashMap = buildings.get(divisionID);
        if (buildingHashMap != null) {
            final ArrayList<String> buildingList = new ArrayList<>(buildingHashMap.values());
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buildingList);
            spinner_building = (Spinner) findViewById(R.id.building);
            spinner_building.setAdapter(adapter);
            spinner_building.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position = spinner_building.getSelectedItemPosition();
                        buildingID = buildingHashMap.keySet().toArray()[+position].toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                }
            );
        } else {
            spinner_building.setAdapter(null);
        }
    }

    private class PHP_GetCompnayInfo extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn!=null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
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

                JSONArray group = results.getJSONArray("group");
                Log.i("HOHO", "groups.. " + Integer.toString(group.length()));
                for(int index=0; index<group.length(); index++){
                    JSONObject jo = group.getJSONObject(index);
                    groups.put(jo.getString("groupid"), jo.getString("description"));
                }

                JSONArray company = results.getJSONArray("company");
                Log.i("HOHO", "companies.. " + Integer.toString(company.length()));
                for (int index=0; index<company.length(); index++){
                    JSONObject jo = company.getJSONObject(index);
                    if (companies.containsKey(jo.getString("groupid"))) {
                        companies.get(jo.getString("groupid")).put(jo.getString("companyid"), jo.getString("description"));
                    } else {
                        HashMap<String, String> list = new HashMap<>();
                        list.put(jo.getString("companyid"),jo.getString("description"));
                        companies.put(jo.getString("groupid"), list);
                    }
                }

                JSONArray division = results.getJSONArray("division");
                Log.i("HOHO", "divisions.. " + Integer.toString(division.length()));
                for (int index=0; index<division.length(); index++){
                    JSONObject jo = division.getJSONObject(index);
                    if (divisions.containsKey(jo.getString("companyid"))) {
                        divisions.get(jo.getString("companyid")).put(jo.getString("divisionid"),jo.getString("description"));
                    } else {
                        HashMap<String, String> list = new HashMap<>();
                        list.put(jo.getString("divisionid"), jo.getString("description"));
                        divisions.put(jo.getString("companyid"), list);
                    }
                }

                JSONArray building = results.getJSONArray("building");
                Log.i("HOHO", "buildings.. " + Integer.toString(building.length()));
                for (int index=0; index<building.length(); index++){
                    JSONObject jo = building.getJSONObject(index);
                    if (buildings.containsKey(jo.getString("divisionid"))) {
                        buildings.get(jo.getString("divisionid")).put(jo.getString("buildingid"),jo.getString("buildingname"));
                    } else {
                        HashMap<String, String> list = new HashMap<>();
                        list.put(jo.getString("buildingid"), jo.getString("buildingname"));
                        buildings.put(jo.getString("divisionid"), list);
                    }
                }

                fillGroupList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
