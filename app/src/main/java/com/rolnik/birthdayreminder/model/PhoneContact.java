package com.rolnik.birthdayreminder.model;

import com.rolnik.birthdayreminder.BR;

import java.io.Serializable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import lombok.Builder;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = false)
@Builder

public class PhoneContact extends BaseObservable implements Serializable {
    private String name;
    private String phoneNumber;

    public PhoneContact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }
}
