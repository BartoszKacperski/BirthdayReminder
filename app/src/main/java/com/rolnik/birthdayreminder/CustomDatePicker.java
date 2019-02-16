package com.rolnik.birthdayreminder;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.NumberPicker;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CustomDatePicker extends ConstraintLayout {
    private NumberPicker day;
    private NumberPicker month;
    private Calendar calendar;
    private DateFormatSymbols dateFormatSymbols;

    public CustomDatePicker(Context context) {
        super(context);
        initNumberPicker();
    }

    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNumberPicker();
    }

    public CustomDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initNumberPicker();
    }

    private void initNumberPicker(){
        initViews();
        initDateServices();

        month.setDisplayedValues(dateFormatSymbols.getShortMonths());

        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                calendar.set(Calendar.MONTH, i1);
                day.setMaxValue(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        });
    }

    private void initDateServices() {
        calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2020);
        dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
    }


    private void initViews(){
        LayoutInflater.from(getContext()).inflate(R.layout.date_picker, this);

        day = findViewById(R.id.day);
        day.setMinValue(1);
        day.setMaxValue(31);
        month = findViewById(R.id.month);
        month.setMaxValue(11);
    }

    public Calendar getCalendarDate() {
        Calendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(Calendar.MONTH, month.getValue());
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, day.getValue());

        return gregorianCalendar;
    }
}
