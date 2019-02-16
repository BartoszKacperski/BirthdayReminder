package com.rolnik.birthdayreminder.database;

import androidx.room.TypeConverter;

import com.rolnik.birthdayreminder.model.Event;

import java.util.Calendar;

public class DatabaseTypeConverters {
    @TypeConverter
    public static Calendar longToCalendar(Long value) {
        if(value == null){
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value);

        return calendar;
    }

    @TypeConverter
    public static Long calendarToLong(Calendar value) {
        return value == null ? null : value.getTimeInMillis();
    }

    @TypeConverter
    public static int eventTypeToInt(Event.EventType eventType) {
        return eventType.getCode();
    }

    @TypeConverter
    public static Event.EventType intToEventType(int value) {
        for(Event.EventType eventType : Event.EventType.values()){
            if(eventType.getCode() == value){
                return eventType;
            }
        }

        return null;
    }
}
