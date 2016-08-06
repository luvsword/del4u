package com.kaist.delforyou.activity;

/**
 * Created by luvsword on 2016-07-27.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;



import com.kaist.delforyou.MainActivity;
import com.kaist.delforyou.R;
import com.kaist.delforyou.app.AppConfig;
import com.kaist.delforyou.app.AppController;
import com.kaist.delforyou.helper.SQLiteHandler;
import com.kaist.delforyou.helper.SessionManager;
import com.kaist.delforyou.activity.MainMenuActivityforDeliveryMen;
import com.kaist.delforyou.activity.SignupActivity;
import com.kaist.delforyou.activity.UserData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
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

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private DeliveryMenViewActivity DeliveryMenViewActivity;

    private SessionManager session;
    private SQLiteHandler db;
    String email_id = "delforyou@kaist.ac.kr";
    String getLastname = "KIM";
    Context context;

    int NameID;
    String Name;
    String Birth;
    String routine;
    String view_info;

    String firstname;
    String lastname;
    String phoneno;
    String divisionid;
    String workplace;
    String employeeid;
    String getinfoDB = "firstname, lastname, phoneno, divisionid, workplace, employeeid";
    String fromQ = "employee where firstname = bum";
    TextView textView;
//    String url ="http://localhost/family.php";
//    String url ="http://gyeongmo.synology.me/ojin/index.php";
//    String url ="http://125.131.73.79:4002/delforyou/login.php";

    // 서버에서 게시글 정보 가져오기 (JSON 형태)


    public void loadEmployee() {

        ComWithServer cws = new ComWithServer(context); // 서버와 통신하는 Class
//        String sql = "select" + getinfoDB + "from employee where email =  " + '"' + email_id + '"' + ";" ;
        String sql = "select workplace, lastname, firstname, divisionid, phoneno, employeeid from employee where email = " + '"' + email_id + '"' + ";";
//        String sql = "select workplace, lastname, firstname, divisionid, phoneno, employeeid from employee where lastname = " + '"' + getLastname + '"' + ";";
//        String sql = "select * from employee where email = delforyou@kaist.ac.kr;" ;
//        String sql = "select * from "+ fromQ + ";";//employee;";
        String employee = cws.communicationTask(sql, "delivermen");
        // json 값 parsing
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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverymenview);
        context = getApplicationContext();

        loadEmployee();
        view_info = firstname + "\n" + lastname + "\n" + divisionid + "\n" + phoneno + "\n" + workplace;
        textView = (TextView) findViewById(R.id.mn_email);
        textView.setText(view_info);
//        textView.setText(NameID);
//        textView.setText(Birth);
        textView.setTextSize(20);


    }


//    testView.setText(Birth);

//    textElement.setText("I love you");

    public void connectMessage(View v) {

//        loadEmployee();
        String errorMsg = "error_msg";
        Toast.makeText(getApplicationContext(),
                errorMsg, Toast.LENGTH_LONG).show();
        //        Name, Toast.LENGTH_LONG).show();

    }

    //전화연결 버튼 눌렀을 시,
    public void connectCall(View v) {
        //TODO :처리해줘야함
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


//////////////////////////////////////////////////////////////////////////////////////////////////
//   서버에서 DB data 가져오는 녀석
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
                thread.join(); // thread가 종료될때 까지 기다린다.
            } catch (Exception e) {
                return "Communication Error";
            }

            return thread.getResult();
        }


        private class CommunicationThread extends Thread {
            private String communicationType;
            private ArrayList<String> params;
            private String sql;
            private String mode;
            private String serverUrl;
            private StringBuilder output; // thread에서 나온 output

            public CommunicationThread(ArrayList<String> params) {
                this.params = params;
                communicationType = params.get(0);
//            if (communicationType.equals("sql")) {
                this.mode = params.get(1);
                this.sql = params.get(2);

                if (mode.equals("delivermen"))
                    serverUrl = "http://125.131.73.79:4002/delforyou/deliver.php";
                    //serverUrl = "http://gyeongmo.synology.me/ojin/deliver.php";
                else
                    serverUrl = "http://gyeongmo.synology.me/ojin/deliver.php";

                output = new StringBuilder();
            }

            public String getResult() {
                return output.toString();
            }

            @Override
            public void run() {

                try {
                    // 연결 url 설정
                    URL url = new URL(serverUrl);
                    // 커넥션 객체 생성
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 연결되었스면,
                    if (conn != null) {
                        conn.setReadTimeout(CONNECTION_TIMEOUT);
                        conn.setConnectTimeout(CONNECTION_TIMEOUT);
                        conn.setUseCaches(false);
                        conn.setDoInput(true);
                        // 헤더값을 설정한다.
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        // 전달 방식을 설정한다. POST or GET, 기본값은 GET 이다.
                        conn.setRequestMethod("POST");
                        // 서버로 데이터를 전송할 수 있도록 한다. GET방식이면 사용될 일이 없으나, true로 설정하면 자동으로 POST로 설정된다. 기본값은 false이다.
                        conn.setDoOutput(true);
                        conn.setDefaultUseCaches(false);

                        // Append parameters to URL
                        Uri.Builder builder = new Uri.Builder();
                        builder.appendQueryParameter("sql", sql);
                        String query = builder.build().getEncodedQuery();

                        // server에 파라미터 보내줌
                        OutputStream opstrm = conn.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(opstrm, "UTF-8"));
                        writer.write(query);
                        writer.flush();
                        writer.close();
                        //opstrm.write(param.getBytes());
                        opstrm.flush();
                        opstrm.close();

                        //연결되었음 코드가 리턴되면,
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            while (true) {
                                // 웹상에 보여지는 텍스트를 라인 단위로 읽어 저장.
                                String line = br.readLine();
                                if (line == null) break;
                                // 저장된 텍스트 라인을 jsonString에 붙여 넣음
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
