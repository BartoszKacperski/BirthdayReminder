package com.rolnik.birthdayreminder;

import android.app.Application;
import android.content.SharedPreferences;

import com.rolnik.birthdayreminder.components.DBComponent;
import com.rolnik.birthdayreminder.components.DaggerDBComponent;
import com.rolnik.birthdayreminder.database.DBModule;
import com.rolnik.birthdayreminder.notificationserivces.AlarmCreator;

public class BirthdayReminderApplication extends Application {
    private DBComponent dbComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (checkIfFirstRun()) {
            scheduleAlarms();
        }
    }

    private boolean checkIfFirstRun() {
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

    public DBComponent getDbComponent() {
        if (dbComponent == null) {
            dbComponent = DaggerDBComponent.builder()
                    .dBModule(new DBModule(getApplicationContext()))
                    .build();
        }

        return dbComponent;
    }
}
