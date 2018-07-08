package com.javaone.onepet;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;

import com.javaone.onepet.Alarm.AlarmService;
import com.javaone.onepet.Alarm.activity.AlarmMainActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView show, hide, change;
    private ImageView wechat, bluetooth, alarm;
    private ImageView setting, random, qa;
    private Intent petServiceIntent;
	private AlarmService.RebootReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        show = (ImageView)findViewById(R.id.show);
        hide = (ImageView)findViewById(R.id.hide);
        change = (ImageView)findViewById(R.id.change);
        wechat = (ImageView)findViewById(R.id.wechat);
        bluetooth = (ImageView)findViewById(R.id.bluetooth);
        alarm = (ImageView)findViewById(R.id.alarm);
        setting = (ImageView)findViewById(R.id.setting);
        random = (ImageView)findViewById(R.id.random);
        qa = (ImageView)findViewById(R.id.qa);

        show.setOnClickListener(this);
        hide.setOnClickListener(this);
        change.setOnClickListener(this);
        wechat.setOnClickListener(this);
        bluetooth.setOnClickListener(this);
        alarm.setOnClickListener(this);
        setting.setOnClickListener(this);
        random.setOnClickListener(this);
        qa.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.show:
                showFloatWindow();
//                show.setClickable(false);
                break;
            case R.id.hide:
                hideFloatWindow();
//                show.setClickable(true);
                break;
            case R.id.change:
                PetService.getInstant().changeModel();
                break;
            case R.id.wechat:
                Intent intent_wechat = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent_wechat);
                Toast.makeText(getApplicationContext(), "在\"无障碍\"设置界面中点击\"OnePet\"选项开启或关闭微信消息通知助手", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bluetooth:
                Intent intent_bluetooth = new Intent();
                intent_bluetooth.setClass(v.getContext(), BlueToothService.class);
                startActivity(intent_bluetooth);
                break;
            case R.id.alarm:
                Intent intent_alarm = new Intent(MainActivity.this, AlarmMainActivity.class);
                startActivity(intent_alarm);
                break;
            case R.id.setting:
                Intent intent_change = new Intent(MainActivity.this, PetInfoActivity.class);
                startActivity(intent_change);
                break;
            case R.id.random:
                randomSetting();
                break;
            case R.id.qa:
                Intent intent_qa = new Intent(MainActivity.this, QaActivity.class);
                startActivity(intent_qa);
                break;
            default:
                break;
        }
    }

    private void showFloatWindow() {
        //权限判断
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(getApplicationContext())) {
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
                show.setClickable(false);
            }
        } else {
            petServiceIntent = new Intent(MainActivity.this, PetService.class);
            getApplication().startService(petServiceIntent);
            show.setClickable(false);
        }
    }

    private void hideFloatWindow() {
        petServiceIntent = new Intent(MainActivity.this, PetService.class);
        getApplication().stopService(petServiceIntent);
        show.setClickable(true);
    }

    private void randomSetting() {
        AlertDialog dialog = null;
        AlertDialog.Builder randomSet = new AlertDialog.Builder(MainActivity.this);
        dialog = randomSet.setTitle("设置")
                .setMessage("是否启动随机启动功能")
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            if(myReceiver != null) {
                                registerReceiver(myReceiver, new IntentFilter("android.intent.action.BOOT_COMPLETED"));
                                Toast.makeText(MainActivity.this, "关闭随机启动~", Toast.LENGTH_SHORT).show();
                                myReceiver = null;
                            }
                            else {
                                Toast.makeText(MainActivity.this, "未设置随机启动~", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            myReceiver = new AlarmService.RebootReceiver();
                            registerReceiver(myReceiver, new IntentFilter("android.intent.action.BOOT_COMPLETED"));
                            Toast.makeText(MainActivity.this, "设置随机启动~", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();             //创建AlertDialog对象
        dialog.show();
    }
}