package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kaist.delforyou.R;

/**
 * Created by user on 2016-07-23.
 */
public class ReservationHistoryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo:배달원 or 배달고객인지 따라 보여지는 view 달라지게 처리 필요
        //if(배달원이면){
        setContentView(R.layout.reservationhistoryfordeliverymen);
        //} else{
        //setContentView(R.layout.reservationhistory);
        //  }
    }

    //정렬 버튼 눌렀을 시,
    public void sort(View v) {
        // TODO:사람이름 or 날짜 순으로 정렬해야함
    }

    // TODO: EditTextView에 DB에서 각각의 배송내역들 읽어와서 내용보여줘야함

    // 'TextView1 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView1(View v) {
        Intent intent = new Intent(ReservationHistoryActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // 'TextView2 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView2(View v) {
        Intent intent = new Intent(ReservationHistoryActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // 'TextView3 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView3(View v) {
        Intent intent = new Intent(ReservationHistoryActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // '배송내역 더보기' 버튼 눌렀을 시,
    public void moreReservationHistory(View v) {
        // TODO: 배송 내역 더 보여줘야함
    }

    //배송조회 버튼 눌렀을 시,
    public void dliveryView(View v) {
        Intent intent = new Intent(ReservationHistoryActivity.this, MainMenuActivity.class);
        startActivity(intent);
    }

    //예약접수 버튼 눌렀을 시,
    public void reservation(View v) {
        Intent intent = new Intent(ReservationHistoryActivity.this, ReservationActivity.class);
        startActivity(intent);
    }
    //예약함 버튼 눌렀을 시,
    public void reservationHistory(View v) {
    }
    //설정 버튼 눌렀을 시,
    public void setting(View v) {
        Intent intent = new Intent(ReservationHistoryActivity.this, SettingActivity.class);
        startActivity(intent);
    }

}
