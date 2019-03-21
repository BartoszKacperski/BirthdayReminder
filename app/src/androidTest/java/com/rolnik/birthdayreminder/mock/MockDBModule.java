package com.rolnik.birthdayreminder.mock;

import android.content.Context;

import com.rolnik.birthdayreminder.database.DBModule;
import com.rolnik.birthdayreminder.database.EventDataBase;

import androidx.room.Room;

public class MockDBModule extends DBModule {
    public MockDBModule(Context context) {
        super(context);
    }

    @Override
    public EventDataBase getDataBase(){
        return Room.inMemoryDatabaseBuilder(getContext(), EventDataBase.class).build();
    }
}
