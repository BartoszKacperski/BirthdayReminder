package com.rolnik.birthdayreminder.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.rolnik.birthdayreminder.model.Event;


@Database(entities = {Event.class}, version = 1, exportSchema = false)
@TypeConverters({DatabaseTypeConverters.class})
public abstract class EventDataBase extends RoomDatabase {
    public abstract EventDao eventDao();
}
