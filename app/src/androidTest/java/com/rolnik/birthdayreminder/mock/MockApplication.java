package com.rolnik.birthdayreminder.mock;

import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.components.DBComponent;
import com.rolnik.birthdayreminder.components.DaggerDBComponent;

public class MockApplication extends BirthdayReminderApplication {
    private DBComponent dbComponent;

    @Override
    public DBComponent getDbComponent() {
        if (dbComponent == null) {
            dbComponent = DaggerDBComponent.builder()
                    .dBModule(new MockDBModule(getApplicationContext()))
                    .build();
        }
        return dbComponent;
    }
}
