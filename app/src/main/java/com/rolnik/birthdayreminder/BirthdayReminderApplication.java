package com.rolnik.birthdayreminder;

import android.app.Application;
import android.content.SharedPreferences;

import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.notificationserivces.AlarmCreator;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BirthdayReminderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(checkIfScheduleAlarms()){
            scheduleAlarms();
        }
    }

    private boolean checkIfScheduleAlarms(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();

        int year = sharedPreferences.getInt(getString(R.string.update_notification_year), calendar.get(Calendar.YEAR));

        return year != calendar.get(Calendar.YEAR);
    }

    private void scheduleAlarms() {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        eventDataBase.eventDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext(events -> {
            for (Event event : events) {
                AlarmCreator.createAlarm(getApplicationContext(), event);
            }

            updateYear();
        }).subscribe();
    }

    private void updateYear(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(getString(R.string.update_notification_year), Calendar.getInstance().get(Calendar.YEAR));

        editor.apply();
    }
}
