package com.javaone.onepet.myAlarm;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

import com.javaone.onepet.R;

public class AlarmActivity extends Activity {
    private static TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_view);
    }
}
