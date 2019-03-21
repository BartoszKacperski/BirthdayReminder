package com.rolnik.birthdayreminder.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.database.EventDataBase;

import javax.inject.Inject;

public class StackWidgetService extends RemoteViewsService {
    @Inject
    EventDataBase eventDataBase;

    @Override
    public void onCreate() {
        ((BirthdayReminderApplication) getApplication()).getDbComponent().inject(this);
        super.onCreate();

    }
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent, eventDataBase);
    }
}
