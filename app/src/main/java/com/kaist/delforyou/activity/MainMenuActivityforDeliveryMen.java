package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kaist.delforyou.R;

/**
 * Created by user on 2016-07-24.
 */
public class MainMenuActivityforDeliveryMen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenuactivityfordeliverymen);
    }

    //배송조회 버튼 눌렀을 시,
    public void deliveryView(View v) {

    }

    //작업할당 버튼 눌렀을 시,
    public void assignJob(View v) {
        Intent intent = new Intent(MainMenuActivityforDeliveryMen.this, AssignActivity.class);
        startActivity(intent);
    }
    //예약함 버튼 눌렀을 시,
    public void reservationHistory(View v) {
        Intent intent = new Intent(MainMenuActivityforDeliveryMen.this, ReservationHistoryActivity.class);
        startActivity(intent);
    }
    //설정 버튼 눌렀을 시,
    public void setting(View v) {
        Intent intent = new Intent(MainMenuActivityforDeliveryMen.this, SettingActivity.class);
        startActivity(intent);
    }
}
