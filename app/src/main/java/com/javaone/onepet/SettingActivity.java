package com.javaone.onepet;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity{
    private Toolbar toolbar;
    private TextView save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 加载FragForPetSetting
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FragForPetSetting petSettingFragment = new FragForPetSetting();
        transaction.add(R.id.frag_setting, petSettingFragment, "pet_setting");
        transaction.commit();

        toolbar = (Toolbar)findViewById(R.id.toolbarset);
        save = (TextView)findViewById(R.id.save_setting);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this, "Save", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SettingActivity.this, PetInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}