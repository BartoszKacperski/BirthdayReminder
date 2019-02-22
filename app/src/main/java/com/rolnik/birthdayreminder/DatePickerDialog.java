package com.rolnik.birthdayreminder;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DatePickerDialog extends Dialog {
    @BindView(R.id.acceptButton)
    Button acceptButton;
    @BindView(R.id.cancelButton)
    Button cancelButton;
    @BindView(R.id.customDatePicker)
    CustomDatePicker customDatePicker;


    public DatePickerDialog(Context context) {
        super(context);
        setContentView(R.layout.datepicker_dialog);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        cancelButton.setOnClickListener(view -> dismiss());
    }

    public void setOnAcceptListener(View.OnClickListener onAcceptListener){
        acceptButton.setOnClickListener(onAcceptListener);
    }

    public Calendar getDate(){
        return customDatePicker.getCalendarDate();
    }
}
