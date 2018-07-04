package com.javaone.onepet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {
    private Button show_button;
    private Button hide_button;
    private Intent petServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show_button = (Button) findViewById(R.id.btn_show_pet);
        hide_button = (Button) findViewById(R.id.btn_hide_pet);

        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);

        show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //权限判断
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(getApplicationContext())) {
                        Toast.makeText(MainActivity.this, "需要开启悬浮窗权限", Toast.LENGTH_SHORT).show();
                        //启动Activity让用户授予悬浮窗权限
                        try {
                            Class clazz = Settings.class;
                            Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
                            Intent settingIntent = new Intent(field.get(null).toString());
                            settingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            settingIntent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(settingIntent);
                        } catch (Exception e) {
                            Log.e("PetService", Log.getStackTraceString(e));
                        }
                    } else {
                        petServiceIntent = new Intent(MainActivity.this, PetService.class);
                        getApplication().startService(petServiceIntent);
                    }
                } else {
                    petServiceIntent = new Intent(MainActivity.this, PetService.class);
                    getApplication().startService(petServiceIntent);
                }
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
