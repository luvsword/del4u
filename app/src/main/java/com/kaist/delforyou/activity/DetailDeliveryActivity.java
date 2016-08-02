package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kaist.delforyou.R;

/**
 * Created by user on 2016-07-23.
 */
public class DetailDeliveryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: 배달고객, 배달원 구분에 따라 로직 처리되야함.
        //if(배달고객이면) {
         //   setContentView(R.layout.detaildelivery);
        //} else{
            setContentView(R.layout.detaildeliveryfordeliverymen);
       // }
    }

    //  배달원 정보 조회 버튼 눌렀을 시,
    public void deliveryMenView(View v) {
        Intent intent = new Intent(DetailDeliveryActivity.this, DeliveryMenViewActivity.class);
        startActivity(intent);
    }

}
