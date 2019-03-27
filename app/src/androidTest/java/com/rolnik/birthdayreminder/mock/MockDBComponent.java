package com.rolnik.birthdayreminder.mock;

import com.rolnik.birthdayreminder.activities.NotificationEventActivity;
import com.rolnik.birthdayreminder.activities.NotificationEventActivityTest;
import com.rolnik.birthdayreminder.components.DBComponent;
import com.rolnik.birthdayreminder.database.DBModule;


import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DBModule.class)
public interface MockDBComponent extends DBComponent {
    void inject(NotificationEventActivityTest notificationEventActivity);
}
