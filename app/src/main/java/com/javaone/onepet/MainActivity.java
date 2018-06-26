package com.javaone.onepet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button show_button;
    private Button hide_button;
    private Intent petServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show_button = (Button)findViewById(R.id.btn_show_pet);
        hide_button = (Button)findViewById(R.id.btn_hide_pet);

        show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petServiceIntent = new Intent(MainActivity.this, PetService.class);
                getApplication().startService(petServiceIntent);
                finish();
            }
        });

        hide_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petServiceIntent = new Intent(MainActivity.this, PetService.class);
                getApplication().stopService(petServiceIntent);
            }
        });
    }
}
