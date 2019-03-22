package com.rolnik.birthdayreminder.notificationserivces;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rolnik.birthdayreminder.utils.DateUtils;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.model.Event;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmCreator {

    private AlarmCreator() {

    }

    public static void createAlarm(final Context context, final Event event) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if(calendar.get(Calendar.MONTH) <= event.getDate().get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) <= event.getDate().get(Calendar.DAY_OF_MONTH)){
            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
            notificationIntent.putExtra(context.getString(R.string.event_id), event.getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId(), notificationIntent, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            calendar.set(Calendar.MONTH, event.getDate().get(Calendar.MONTH));
            calendar.set(Calendar.DAY_OF_MONTH, event.getDate().get(Calendar.DAY_OF_MONTH));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 15);

            Log.i("Time = ", DateUtils.formatDate(calendar));
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public static void cancelAlarm(final Context context, final Event event) {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(context.getString(R.string.event_id), event.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, event.getId(), notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public static void createYearAlarm(final Context context) {
        Intent intervalIntent = new Intent(context, NotificationIntervalService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, context.getResources().getInteger(R.integer.pendingYearAlarmID), intervalIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = new GregorianCalendar();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
