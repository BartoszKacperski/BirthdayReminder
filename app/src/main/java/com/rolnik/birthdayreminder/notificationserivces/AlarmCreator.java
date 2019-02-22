package com.rolnik.birthdayreminder.notificationserivces;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.model.Event;

import java.util.Calendar;

public class AlarmCreator {

    private AlarmCreator(){

    }

    public static void createAlarm(final Context context, final Event event){
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(context.getString(R.string.event_id), event.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId(), notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.MONTH, event.getDate().get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, event.getDate().get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 4);
        calendar.set(Calendar.MINUTE, 0);


        Log.i("Time = ", calendar.toString());
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
