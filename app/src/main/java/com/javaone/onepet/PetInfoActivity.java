package com.javaone.onepet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

public class PetInfoActivity extends AppCompatActivity {
    private Button modify;
    private TextView nick_name;
    private RadioGroup sex_group;
    private RadioButton male, female;
    private TextView level;
    private TextView character;
    private TextView partner;
    private TextView wechat;
    private Intent petServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_info);
        modify = (Button)findViewById(R.id.modify_btn);
        nick_name = (TextView)findViewById(R.id.nick_name);
        sex_group = (RadioGroup)findViewById(R.id.sex_btn);
        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);
        level = (TextView)findViewById(R.id.level_value);
        character = (TextView)findViewById(R.id.character_value);
        partner = (TextView)findViewById(R.id.partner_value);
        wechat = (TextView)findViewById(R.id.wechat_value);

/*        PetDBOperator pet_db_operator = new PetDBOperator(PetInfoActivity.this);
        if(pet_db_operator.isPetExisted()) {
            // 同步宠物信息
            Pet mpet = pet_db_operator.getPet();
            nick_name.setText(mpet.name);
            level.setText("Level " + mpet.level);
            character.setText(mpet.character);
            partner.setText(mpet.partner);
            wechat.setText(mpet.wechat);

            if(mpet.sex == 1) {
                male.setTextColor(Color.parseColor("#FF000000"));
                male.setEnabled(true);
                male.setChecked(true);
                female.setTextColor(Color.parseColor("#aaaaaa"));
                female.setEnabled(false);
            } else if(mpet.sex == 0) {
                female.setTextColor(Color.parseColor("#FF000000"));
                female.setEnabled(true);
                female.setChecked(true);
                male.setTextColor(Color.parseColor("#aaaaaa"));
                male.setEnabled(false);
            }
        }
        else {
            // 在数据库中新建宠物信息
            Pet new_pet = new Pet();
            pet_db_operator.add(new_pet);

        }*/
        showSettingInfo();
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetInfoActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showSettingInfo() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        nick_name.setText(settings.getString("name_key", ""));
        if("男".equals(settings.getString("sex_key", "None"))) {
            male.setTextColor(Color.parseColor("#FF000000"));
            male.setEnabled(true);
            male.setChecked(true);
            female.setTextColor(Color.parseColor("#f5f5f5"));
            female.setEnabled(false);
        }
        else{
            female.setTextColor(Color.parseColor("#FF000000"));
            female.setEnabled(true);
            female.setChecked(true);
            male.setTextColor(Color.parseColor("#f5f5f5"));
            male.setEnabled(false);
        }
        level.setText("Level" + settings.getString("level_key", "None"));
        character.setText(settings.getString("character_key", "None"));
        //settings.getBoolean(Consts.CHECKOUT_KEY, false)+"" for checkbox
    }
}
