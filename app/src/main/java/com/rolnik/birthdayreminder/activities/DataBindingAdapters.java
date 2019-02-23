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
import com.rolnik.birthdayreminder.model.Event;

import java.util.Calendar;
import java.util.Locale;

public class DataBindingAdapters {
    private static final String[] romanMonths =  {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};

    @BindingAdapter("load_image")
    public static void setImageResource(ImageView imageView, Event.EventType eventType){
        switch(eventType){
            case PARTY: {
                imageView.setImageResource(R.drawable.party);
                break;
            }
            case BIRTHDAY:{
                imageView.setImageResource(R.drawable.birthday);
                break;
            }
            case NAME_DAY:{
                imageView.setImageResource(R.drawable.name);
                break;
            }
            case ANNIVERSARY: {
                imageView.setImageResource(R.drawable.anniversary);
                break;
            }
        }
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
        switch(eventType){
            case PARTY: {
                textView.setText(R.string.party);
                break;
            }
            case BIRTHDAY:{
                textView.setText(R.string.birthday);
                break;
            }
            case NAME_DAY:{
                textView.setText(R.string.name_day);
                break;
            }
            case ANNIVERSARY: {
                textView.setText(R.string.anniversary);
                break;
            }
        }
    }

    public static int eventTypeToStringResourceId(Event.EventType eventType){
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

    @BindingAdapter(value = {"selectedValue", "selectedValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(Spinner spinner, Event.EventType eventType, final InverseBindingListener newTextAttrChanged) {
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
    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
    public static Event.EventType captureSelectedValue(Spinner spinner) {
        return (Event.EventType) spinner.getSelectedItem();
    }
}
