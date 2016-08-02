package com.kaist.delforyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.kaist.delforyou.R;

/**
 * Created by user on 2016-07-23.
 */
public class SettingActivity extends PreferenceActivity {

    Button modifyInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.setting);

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
    }

// TODO:알림설정, 소리알림, 진동알림 각각에 대해서 처리 로직 필요

}
