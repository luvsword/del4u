package com.kaist.delforyou.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.kaist.delforyou.R;

/**
 * Created by user on 2016-07-23.
 */
public class SettingActivity extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.setting);
    }

// TODO:알림설정, 소리알림, 진동알림 각각에 대해서 처리 로직 필요

}
