package com.socialdistance;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.util.Calendar;

import static android.app.AlarmManager.*;
import static android.app.PendingIntent.*;

public class AlarmHandler {

    private static final String TAG = AlarmHandler.class.getSimpleName();

    private Context mContext;

    public AlarmHandler(Context mContext) {
        this.mContext = mContext;
    }


    public  void setAlarmManager() {

        Calendar rightNow = Calendar.getInstance();

        Intent intent = new Intent(mContext, MyCustomBroadcastReceiver.class);
        intent.setAction("alarmrecieved");
        PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        //Log.e(TAG,"Alarm triggered.");
        //Ask from user.
        int triggerEvery = 1 * 10 * 1000;
        if (alarmManager != null) {
            alarmManager.setRepeating(RTC_WAKEUP, rightNow.getTimeInMillis(), triggerEvery, sender);
        }
    }

    public void cancelAlarmManager() {

        Intent intent = new Intent(mContext, MyCustomBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(sender);
        }

        //Log.e(TAG,"Alarm Cancelled.");
    }


}
