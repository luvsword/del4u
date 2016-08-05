package com.kaist.delforyou.helper;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luvsword on 2016-08-04.
 */


public class DatabaseAsynTask extends AsyncTask<JSONObject, Void, String> {
    public int CONNECTION_TIMEOUT = 1500;
    @Override
    protected void onPreExecute() {
            /*
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            */
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject ... params) {
        String serverUrl="";
        try {
            serverUrl=params[0].getString("serverInfo");
        } catch (JSONException e){
            e.printStackTrace();
        }
        String retValStr =  "Json read Error";
        StringBuilder output = new StringBuilder(); // thread에서 나온 output

        try {
            // 연결 url 설정
            URL url = new URL(serverUrl);
            // 커넥션 객체 생성
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            // 연결되었스면,
            if(conn != null){
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

                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter(params[0].getString("connectType"), params[0].toString());

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
                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    while(true){
                        // 웹상에 보여지는 텍스트를 라인 단위로 읽어 저장.
                        String line = br.readLine();
                        if(line == null) break;
                        // 저장된 텍스트 라인을 jsonString에 붙여 넣음
                        output.append(line);
                    }
                    br.close();
                }
                conn.disconnect();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return output.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        //progressDialog.dismiss();
        super.onPostExecute(s);
    }
}