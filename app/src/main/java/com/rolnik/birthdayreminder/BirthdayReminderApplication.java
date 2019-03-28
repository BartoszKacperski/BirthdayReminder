package com.rolnik.birthdayreminder;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.rolnik.birthdayreminder.components.DBComponent;
import com.rolnik.birthdayreminder.components.DaggerDBComponent;
import com.rolnik.birthdayreminder.database.DBModule;
import com.rolnik.birthdayreminder.notificationserivces.AlarmCreator;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;
import org.acra.annotation.AcraToast;
import org.acra.data.StringFormat;

@AcraCore(buildConfigClass = BuildConfig.class,
        reportFormat = StringFormat.JSON,
        reportContent = {ReportField.ANDROID_VERSION,
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.BRAND,
                ReportField.DEVICE_ID,
                ReportField.PHONE_MODEL,
                ReportField.PRODUCT,
                ReportField.SHARED_PREFERENCES,
                ReportField.STACK_TRACE,
                ReportField.USER_EMAIL
        })
@AcraToast(resText = R.string.acra_toast)
@AcraMailSender(mailTo = "kacperskib1@gmail.com")
public class BirthdayReminderApplication extends Application {
    private DBComponent dbComponent;
    private static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        BirthdayReminderApplication.applicationContext = getApplicationContext();

        if (checkIfFirstRun()) {
            scheduleAlarms();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }

    public static Context getAppContext() {
        return BirthdayReminderApplication.applicationContext;
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
