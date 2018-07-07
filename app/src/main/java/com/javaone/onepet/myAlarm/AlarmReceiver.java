package com.javaone.onepet.myAlarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Toast.makeText(context, "收到闹钟提醒了！"
                , Toast.LENGTH_SHORT).show();*/
        AlarmManager am= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(PendingIntent.getBroadcast(context,getResultCode(),new Intent(context,AlarmReceiver.class),0));

        Intent i = new Intent(context,PlayAlarmAty.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}