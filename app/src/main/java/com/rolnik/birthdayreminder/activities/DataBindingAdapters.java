package com.rolnik.birthdayreminder.activities;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.appcompat.widget.AppCompatSpinner;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.adapters.EventTypeAdapter;
import com.rolnik.birthdayreminder.adapters.PhoneContactsAdapter;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.model.PhoneContact;

import java.util.Calendar;
import java.util.Locale;

public class DataBindingAdapters {
    private static final String[] romanMonths =  {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};

    @BindingAdapter("load_image")
    public static void setImageResource(ImageView imageView, Event.EventType eventType){
        imageView.setImageResource(eventTypeToDrawableResourceId(eventType));
    }

    public static String calendarToString(Calendar calendar){
        if(calendar == null){
            return "";
        }

        return String.format(Locale.getDefault(), "%d %s", calendar.get(Calendar.DAY_OF_MONTH), romanMonths[calendar.get(Calendar.MONTH)]);
    }

    public static String calendarToStringFullNameMonths(Calendar calendar){
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        return calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    @BindingAdapter("load_event_type")
    public static void eventTypeToString(TextView textView, Event.EventType eventType){
        textView.setText(eventTypeToStringResourceId(eventType));
    }

    public static int eventTypeToStringResourceId(Event.EventType eventType){
        if(eventType == null){
            return R.string.error;
        }
        switch(eventType){
            case PARTY: {
                return R.string.party;
            }
            case BIRTHDAY:{
                return R.string.birthday;
            }
            case NAME_DAY:{
                return R.string.name_day;
            }
            case ANNIVERSARY: {
                return R.string.anniversary;
            }

            default:
                return R.string.error;
        }
    }

    public static int eventTypeToDrawableResourceId(Event.EventType eventType){
        if(eventType == null) {
            return R.drawable.ic_error_black;
        }
        switch(eventType){
            case PARTY: {
                return R.drawable.party;
            }
            case BIRTHDAY:{
                return R.drawable.birthday;
            }
            case NAME_DAY:{
                return R.drawable.name;
            }
            case ANNIVERSARY: {
                return R.drawable.anniversary;
            }

            default:
                return R.drawable.party;
        }
    }

    @BindingAdapter(value = {"selectedEventTypeValue", "selectedEventTypeValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerEventTypeData(Spinner spinner, Event.EventType eventType, final InverseBindingListener newTextAttrChanged) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newTextAttrChanged.onChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (eventType != null) {
            int pos = ((EventTypeAdapter) spinner.getAdapter()).getPosition(eventType);
            spinner.setSelection(pos, true);
        }
    }

    @InverseBindingAdapter(attribute = "selectedEventTypeValue", event = "selectedEventTypeValueAttrChanged")
    public static Event.EventType captureEventTypeSelectedValue(Spinner spinner) {
        return (Event.EventType) spinner.getSelectedItem();
    }

    @BindingAdapter(value = {"selectedPhoneContactValue", "selectedPhoneContactValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerPhoneContactData(Spinner spinner, PhoneContact phoneContact, final InverseBindingListener newTextAttrChanged) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newTextAttrChanged.onChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (phoneContact != null) {
            if(spinner.getAdapter() != null){
                int pos = ((PhoneContactsAdapter) spinner.getAdapter()).getPosition(phoneContact);
                if(pos > 0){
                    spinner.setSelection(pos, true);
                }
            }
        }
    }
    @InverseBindingAdapter(attribute = "selectedPhoneContactValue", event = "selectedPhoneContactValueAttrChanged")
    public static PhoneContact captureSelectedPhoneContactValue(Spinner spinner) {
        return (PhoneContact) spinner.getSelectedItem();
    }
}
