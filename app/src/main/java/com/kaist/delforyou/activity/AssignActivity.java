package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kaist.delforyou.R;

/**
 * Created by user on 2016-07-30.
 */
public class AssignActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign);
    }

    //업데이트 버튼 눌렀을 시,
    public void updateDelivery(View v) {
        // TODO:
    }

    // '배송예약내역 더보기' 버튼 눌렀을 시,
    public void moreDeliveryVeiw(View v) {
        // TODO: 배송 내역 더 보여줘야함
    }

    //배송조회 버튼 눌렀을 시,
    public void deliveryView(View v) {
        Intent intent = new Intent(AssignActivity.this, MainMenuActivityforDeliveryMen.class);
        startActivity(intent);
    }

    public void assign(View v) {

    }

    //예약함 버튼 눌렀을 시,
    public void reservationHistory(View v) {
        Intent intent = new Intent(AssignActivity.this, ReservationHistoryActivity.class);
        startActivity(intent);
    }

    //설정 버튼 눌렀을 시,
    public void setting(View v) {
        Intent intent = new Intent(AssignActivity.this, SettingActivity.class);
        startActivity(intent);
    }
}
