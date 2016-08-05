package com.kaist.delforyou.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kaist.delforyou.R;
import com.kaist.delforyou.app.AppConfig;
import com.kaist.delforyou.app.AppController;
import com.kaist.delforyou.helper.SQLiteHandler;
import com.kaist.delforyou.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModefySignInfoActivity extends Activity {
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
    private EditText inputCompany;
    private EditText inputDivision;
    private EditText inputJobPos;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    Spinner spinner;

    String[] companies = {
            "LG Electronics",
            "Samsung Electronics",
            "LG Chem Ltd.",
            "GE Healthcare Korea",
            "만도"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modifysigninfoactivity);

        spinner = (Spinner)findViewById(R.id.company);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, companies);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        int position= spinner.getSelectedItemPosition();
                        Toast.makeText(getApplicationContext(), "You select " + companies[+position],Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                }
        );

        inputFname = (EditText) findViewById(R.id.fname);
        inputLname = (EditText) findViewById(R.id.lname);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputCPassword = (EditText) findViewById(R.id.cpassword);
        inputPhoneNo = (EditText) findViewById(R.id.phoneno);
        inputEID = (EditText) findViewById(R.id.employeeid);
        btnRegister = (Button) findViewById(R.id.btnSignupReg);
        btnRegCancel = (Button) findViewById(R.id.btnSignupCancel);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

//        if (session.isLoggedIn()) {
//            // User is already logged in. Take him to main activity
//            Intent intent = new Intent(ModefySignInfoActivity.this,
//                    MainMenuActivityforDeliveryMen.class);
//            startActivity(intent);
//            finish();
//        }
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
                String divisionid = "1";
                String jobposition = "1";

                if (!password.equals(cpassword)) {
                    Toast.makeText(getApplicationContext(),
                            "Please check password!", Toast.LENGTH_LONG)
                            .show();
                }

                if (!fname.isEmpty() && !lname.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(fname, lname, email, password, phoneno, eid, divisionid, jobposition);
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
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
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
                              final String divisionid, final String jobposition) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        //String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String fname = user.getString("firstname");
                        String lname = user.getString("lastname");
                        String email = user.getString("email");
                        String phone = user.getString("phoneno");
                        // Inserting row in users table
                        db.addUser(fname, lname, email, eid, phone);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                ModefySignInfoActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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
}
