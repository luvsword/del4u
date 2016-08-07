package com.kaist.delforyou.activity;

/**
 * Created by luvsword on 2016-07-27.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;




import com.kaist.delforyou.R;
import com.kaist.delforyou.helper.SQLiteHandler;
import com.kaist.delforyou.helper.SessionManager;


import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.io.ByteArrayInputStream;




/**
 * Created by user on 2016-07-23.
 */
public class DeliveryMenViewActivity extends Activity {

    String email_id = "delforyou@kaist.ac.kr";
    Context context;

    String routine;
    String view_info;

    String firstname;
    String lastname;
    String phoneno;
    String divisionid;
    String workplace;
    String employeeid;

    String buildingname;
    String description;
    TextView textView;


    public void loadEmployee() {

        ComWithServer cws = new ComWithServer(context); // ?쒕쾭? ?듭떊?섎뒗 Class
//        String sql = "select" + getinfoDB + "from employee where email =  " + '"' + email_id + '"' + ";" ;
        String sql = "select workplace, lastname, firstname, divisionid, phoneno, employeeid from employee where email = " + '"' + email_id + '"' + ";";



        String employee = cws.communicationTask(sql, "delivermen");
        // json 媛?parsing
        try {
            JSONArray jsonArray = new JSONArray(employee);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                firstname = jsonObject.getString("firstname");
                lastname = jsonObject.getString("lastname");
                phoneno = jsonObject.getString("phoneno");
                divisionid = jsonObject.getString("divisionid");
                workplace = jsonObject.getString("workplace");
                employeeid = jsonObject.getString("employeeid");
                routine = "passed";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sql_b = "select buildingname from building where buildingid = " + '"' + workplace + '"' + ";";
        //String sql_b = "select buildingname from building;";// where buildingid = " + '"' + workplace + '"' + ";";

        String building = cws.communicationTask(sql_b, "delivermen");
        // json 媛?parsing
        try {
            JSONArray jsonArray = new JSONArray(building);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                buildingname = jsonObject.getString("buildingname");
                routine = "passed";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String sql_d = "select description from division where divisionid = " + '"' + divisionid + '"' + ";";

        String division = cws.communicationTask(sql_d, "delivermen");
        // json 媛?parsing
        try {
            JSONArray jsonArray = new JSONArray(division);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                description = jsonObject.getString("description");
                routine = "passed";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverymenview);
        context = getApplicationContext();

        loadEmployee();
        view_info = "\n" + "\n" + "\n" + description + "\n" +  "\n" + buildingname + "\n" + "\n" + lastname + firstname + "\n" +"\n" + phoneno   ;
        textView = (TextView) findViewById(R.id.mn_email);
        textView.setText(view_info);
        textView.setTextSize(15);


    }


    public void connectMessage(View v) {
        String errorMsg = "To Be Continued";
        Toast.makeText(getApplicationContext(),
                errorMsg, Toast.LENGTH_LONG).show();

    }

    public void connectCall(View v) {
        String errorMsg = "To Be Continued";
        Toast.makeText(getApplicationContext(),
                errorMsg, Toast.LENGTH_LONG).show();
    }


//////////////////////////////////////////////////////////////////////////////////////////////////
//   ?쒕쾭?먯꽌 DB data 媛?몄삤?????
//////////////////////////////////////////////////////////////////////////////////////////////////

    public class ComWithServer {
        public static final int CONNECTION_TIMEOUT = 15 * 1000;
        private Context context;

        public ComWithServer(Context context) {
            this.context = context;
        }

        public String communicationTask(String sql, String mode) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("sql");
            arrayList.add(mode);
            arrayList.add(sql);
            CommunicationThread thread = new CommunicationThread(arrayList);
            thread.start();

            try {
                thread.join(); // thread媛 醫낅즺?좊븣 源뚯? 湲곕떎由곕떎.
            } catch (Exception e) {
                return "Communication Error";
            }

            return thread.getResult();
        }


        private class CommunicationThread extends Thread {
            private String sql;
            private String mode;
            private String serverUrl;
            private StringBuilder output; // thread?먯꽌 ?섏삩 output

            public CommunicationThread(ArrayList<String> params) {
                this.mode = params.get(1);
                this.sql = params.get(2);

                if (mode.equals("delivermen"))
                    serverUrl = "http://125.131.73.79:4002/delforyou/deliver.php";
                else if (mode.equals("buildingname"))
                    serverUrl = "http://125.131.73.79:4002/delforyou/deliver.php";
                output = new StringBuilder();
            }

            public String getResult() {
                return output.toString();
            }

            @Override
            public void run() {

                try {
                    // ?곌껐 url ?ㅼ젙
                    URL url = new URL(serverUrl);
                    // 而ㅻ꽖??媛앹껜 ?앹꽦
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // ?곌껐?섏뿀?ㅻ㈃,
                    if (conn != null) {
                        conn.setReadTimeout(CONNECTION_TIMEOUT);
                        conn.setConnectTimeout(CONNECTION_TIMEOUT);
                        conn.setUseCaches(false);
                        conn.setDoInput(true);
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                        conn.setDefaultUseCaches(false);

                        // Append parameters to URL
                        Uri.Builder builder = new Uri.Builder();
                        builder.appendQueryParameter("sql", sql);
                        String query = builder.build().getEncodedQuery();

                        OutputStream opstrm = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(opstrm, "UTF-8"));
                        writer.write(query);
                        writer.flush();
                        writer.close();
                        //opstrm.write(param.getBytes());
                        opstrm.flush();
                        opstrm.close();

                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            while (true) {
                                // ?뱀긽??蹂댁뿬吏???띿뒪?몃? ?쇱씤 ?⑥쐞濡??쎌뼱 ???
                                String line = br.readLine();
                                if (line == null) break;
                                // ??λ맂 ?띿뒪???쇱씤??jsonString??遺숈뿬 ?ｌ쓬
                                output.append(line);
                            }
                            br.close();
                        }
                        conn.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


