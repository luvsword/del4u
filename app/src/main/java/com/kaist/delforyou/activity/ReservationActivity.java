package com.kaist.delforyou.activity;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.view.Menu;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.util.Log;

import com.kaist.delforyou.R;
import com.kaist.delforyou.helper.SQLiteHandler;
import com.kaist.delforyou.helper.SessionManager;

import org.w3c.dom.Text;

/**
 * Created by user on 2016-07-23.
 */
public class ReservationActivity  extends Activity {
    private static final String TAG = ReservationActivity.class.getSimpleName();
//    private DatePicker datePicker;
    private Calendar calendar;
    private TextView visitdateView, visittimeView;
    private int year, month, day, hour, minutes;
    private int datePickerId = 999;
    private int timePickerId =998;
    private SQLiteHandler db;

//    Button reservationCancelButton = (Button) findViewById(R.id.reservationCancelButton);
  //  Button reservationConfirmButton = (Button) findViewById(R.id.reservationConfirmButton);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        Log.d(TAG, "name = " + name + ", email = " + email);

        visitdateView = (TextView) findViewById(R.id.visitDate);
        visittimeView = (TextView) findViewById(R.id.visitTime);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

        showDate(year, month + 1, day);
        showTime(hour, minutes);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(datePickerId);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == datePickerId) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        } else if (id == timePickerId) {
            return new TimePickerDialog(this, myTimeListerner, hour, minutes, true );
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        visitdateView.setText(new StringBuilder().append(year).append("/").append(month).append("/")
                .append(day));
    }

    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(timePickerId);
    }

    private TimePickerDialog.OnTimeSetListener myTimeListerner = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            showTime(hourOfDay,  minute);
        }
    };

    private void showTime(int hourOfDay, int minute) {
        visittimeView.setText(new StringBuilder().append(hourOfDay).append("::").append(minute));
    }

    //[보내는 분] filed


    // 요청사항 버튼 눌렀을 시,
    public void requirement(View v) {
        Intent intent = new Intent(ReservationActivity.this, RequirementActivity.class);
        startActivity(intent);
    }

    // 예약확인 버튼 눌렀을 시,
    public void reservationConfirm(View v) {
    }

    // 예약취소 버튼 눌렀을 시,
    public void reservationCancel(View v) {

    }

}

