package com.kaist.delforyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.kaist.delforyou.R;
import com.kaist.delforyou.helper.SQLiteHandler;
import com.kaist.delforyou.helper.SessionManager;

/**
 * Created by user on 2016-07-23.
 */
public class SettingActivity extends PreferenceActivity {

    Button modifyInformation;
    Button logoutButton;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.setting);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        //'내 정보수정' 버튼 클릭시
        ListView v = getListView();
        modifyInformation = new Button(this);
        v.addFooterView(modifyInformation);
        modifyInformation.setText("내 정보 수정");
        modifyInformation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ModefySignInfoActivity.class);
                startActivity(i);
            }
        });

        logoutButton = new Button(this);
        v.addFooterView(logoutButton);
        logoutButton.setText("로그 아웃");
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                session.setLogin(false);

                db.deleteUsers();

                // Launching the login activity
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

// TODO:알림설정, 소리알림, 진동알림 각각에 대해서 처리 로직 필요

}
