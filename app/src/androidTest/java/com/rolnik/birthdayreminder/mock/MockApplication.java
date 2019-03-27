package com.rolnik.birthdayreminder.mock;

import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.components.DBComponent;

public class MockApplication extends BirthdayReminderApplication {
    private DBComponent dbComponent;

    @Override
    public DBComponent getDbComponent() {
        if (dbComponent == null) {
            dbComponent = DaggerMockDBComponent.builder()
                    .dBModule(new MockDBModule(getApplicationContext()))
                    .build();
        }
        return dbComponent;
    }
}
