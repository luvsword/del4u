package com.kaist.delforyou.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.kaist.delforyou.R;

/**
 * Created by user on 2016-07-23.
 */
public class DeliveryMenViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverymenview);
    }

    //메세지전송 버튼 눌렀을 시,
    public void connectMessage(View v) {
     //TODO :처리해줘야함
    }

    //전화연결 버튼 눌렀을 시,
    public void connectCall(View v) {
    //TODO :처리해줘야함
    }

}
