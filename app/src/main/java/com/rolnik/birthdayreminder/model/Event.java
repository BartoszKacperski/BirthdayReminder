package com.rolnik.birthdayreminder.model;

import com.rolnik.birthdayreminder.BR;

import java.io.Serializable;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)

@Entity(tableName = "events")
public class Event extends BaseObservable implements Serializable {
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
    private boolean hasNotification;
    @Embedded
    private PhoneContact phoneContact;

    public Event(int id, String title, Calendar date, EventType eventType, boolean hasNotification, PhoneContact phoneContact) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.eventType = eventType;
        this.hasNotification = hasNotification;
        this.phoneContact = phoneContact;
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
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
        notifyPropertyChanged(BR.date);
    }

    @Bindable
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
        notifyPropertyChanged(BR.eventType);
    }

    @Bindable
    public boolean isHasNotification() {
        return hasNotification;
    }

    public void setHasNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
        notifyPropertyChanged(BR.hasNotification);
    }

    @Bindable
    public PhoneContact getPhoneContact() {
        return phoneContact;
    }

    public void setPhoneContact(PhoneContact phoneContact) {
        this.phoneContact = phoneContact;
        notifyPropertyChanged(BR.phoneContact);
    }
}
