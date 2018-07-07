package com.javaone.onepet.myAlarm;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.javaone.onepet.R;

public class PlayAlarmAty extends Activity {
    private MediaPlayer mp;
    //音乐播放器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_player_aty);
        mp = MediaPlayer.create(this,R.raw.fallbackring);
        mp.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();
    }
}