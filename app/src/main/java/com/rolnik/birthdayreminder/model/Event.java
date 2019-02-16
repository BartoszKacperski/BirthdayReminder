package com.rolnik.birthdayreminder.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.annotation.NonNull;

import java.util.Calendar;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)

@Entity(tableName = "events")
public class Event extends BaseObservable {
    public enum EventType {
        BIRTHDAY(0), NAME_DAY(1), ANNIVERSARY(2), PARTY(3);

        private int code;

        EventType(int code){
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private Calendar date;
    private EventType eventType;

    public Event(int id, String title, Calendar date, EventType eventType) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.eventType = eventType;
    }

    @Ignore
    public Event(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @Bindable
    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    @Bindable
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
