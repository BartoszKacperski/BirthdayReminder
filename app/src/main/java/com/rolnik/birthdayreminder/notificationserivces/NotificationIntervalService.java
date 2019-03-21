package com.rolnik.birthdayreminder.notificationserivces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationIntervalService extends BroadcastReceiver {
    @Inject
    EventDataBase eventDataBase;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((BirthdayReminderApplication) context.getApplicationContext()).getDbComponent().inject(this);

        Disposable d = eventDataBase.eventDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(events -> {
            for (Event event : events) {
                AlarmCreator.createAlarm(context, event);
            }
            AlarmCreator.createYearAlarm(context);
        });
    }
}
