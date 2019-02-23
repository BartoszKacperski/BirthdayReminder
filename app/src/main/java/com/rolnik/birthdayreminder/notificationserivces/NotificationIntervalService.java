package com.rolnik.birthdayreminder.notificationserivces;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotificationIntervalService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(context);

        eventDataBase.eventDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(events -> {
            for (Event event : events) {
                AlarmCreator.createAlarm(context, event);
            }
            AlarmCreator.createYearAlarm(context);
        }).subscribe();
    }
}
