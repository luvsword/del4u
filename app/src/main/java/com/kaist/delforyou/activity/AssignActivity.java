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

    //정렬 버튼 눌렀을 시,
    public void sort(View v) {
        // TODO:사람이름 or 날짜 순으로 정렬해야함
    }

    //업데이트 버튼 눌렀을 시,
    public void updateDelivery(View v) {
        // TODO:
    }

    // TODO: EditTextView에 DB에서 각각의 배송내역들 읽어와서 내용보여줘야함

    // 'TextView1 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView1(View v) {
        Intent intent = new Intent(AssignActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // 'TextView2 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView2(View v) {
        Intent intent = new Intent(AssignActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // 'TextView3 배송내역 더보기' 버튼 눌렀을 시,
    public void TextView3(View v) {
        Intent intent = new Intent(AssignActivity.this, DetailDeliveryActivity.class);
        startActivity(intent);
    }

    // '배송예약내역 더보기' 버튼 눌렀을 시,
    public void moreDeliveryVeiw(View v) {
        // TODO: 배송 내역 더 보여줘야함
    }

    //배송조회 버튼 눌렀을 시,
    public void dliveryView(View v) {
        Intent intent = new Intent(AssignActivity.this, MainMenuActivityforDeliveryMen.class);
        startActivity(intent);
    }

    //작업할당 버튼 눌렀을 시,
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