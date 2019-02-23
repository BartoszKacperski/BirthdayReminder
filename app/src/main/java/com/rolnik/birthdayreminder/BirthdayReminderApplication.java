package com.rolnik.birthdayreminder;

import android.app.Application;
import android.content.SharedPreferences;

import com.rolnik.birthdayreminder.notificationserivces.AlarmCreator;

import java.util.Calendar;

public class BirthdayReminderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if(checkIfFirstRun()){
            scheduleAlarms();
        }
    }

    private boolean checkIfFirstRun(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean firstRun = sharedPreferences.getBoolean(getString(R.string.first_run), true);
        editor.putBoolean(getString(R.string.first_run), false);

        editor.apply();
        return firstRun;
    }

    private void scheduleAlarms() {
        AlarmCreator.createYearAlarm(getApplicationContext());
    }

}
