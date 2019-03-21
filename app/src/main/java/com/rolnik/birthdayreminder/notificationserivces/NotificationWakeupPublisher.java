package com.rolnik.birthdayreminder.notificationserivces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;

import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationWakeupPublisher extends BroadcastReceiver {
    @Inject
    EventDataBase eventDataBase;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.BOOT_COMPLETED")) {
            ((BirthdayReminderApplication) context.getApplicationContext()).getDbComponent().inject(this);
            renewEventAlarms(context);
            renewYearlyAlarm(context);
        }
    }


    private void renewEventAlarms(final Context context){
        Disposable d = eventDataBase.eventDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(events -> {
            for (Event event : events) {
                AlarmCreator.createAlarm(context, event);
            }
        });
    }

    private void renewYearlyAlarm(final Context context){
        AlarmCreator.createYearAlarm(context);
    }


}
