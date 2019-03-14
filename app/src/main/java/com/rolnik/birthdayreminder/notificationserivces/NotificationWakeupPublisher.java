package com.rolnik.birthdayreminder.notificationserivces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.notificationserivces.AlarmCreator;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotificationWakeupPublisher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("Restarted device", "Remake alarms");
            renewEventAlarms(context);
            renewYearlyAlarm(context);
        }
    }


    private void renewEventAlarms(final Context context){
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(context);

        eventDataBase.eventDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(events -> {
            for (Event event : events) {
                AlarmCreator.createAlarm(context, event);
            }
        }).subscribe();
    }

    private void renewYearlyAlarm(final Context context){
        AlarmCreator.createYearAlarm(context);
    }


}
