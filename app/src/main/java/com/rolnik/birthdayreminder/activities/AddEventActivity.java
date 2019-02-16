package com.rolnik.birthdayreminder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.rolnik.birthdayreminder.DatePickerDialog;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.adapters.EventTypeAdapter;
import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.databinding.ActivityAddEventBinding;
import com.rolnik.birthdayreminder.model.Event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.MaybeObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddEventActivity extends AppCompatActivity {
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.eventRoot)
    LinearLayout eventRoot;
    @BindView(R.id.eventType)
    Spinner eventType;

    private DatePickerDialog datePickerDialog;
    private ActivityAddEventBinding activityAddEventBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        ButterKnife.bind(this);

        initDatePickerDialog();
        initSpinner();
    }

    private void initBinding() {
        activityAddEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_event);
        activityAddEventBinding.setEvent(new Event());
    }

    private void initSpinner() {
        eventType.setAdapter(new EventTypeAdapter(this, android.R.layout.simple_list_item_1));
    }

    private void initDatePickerDialog() {
        datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnAcceptListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityAddEventBinding.getEvent().setDate(datePickerDialog.getDate());
                datePickerDialog.dismiss();
            }
        });
    }

    public void showDatePicker(View view) {
        datePickerDialog.show();
    }

    public void save(View view) {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        Event event = activityAddEventBinding.getEvent();

        eventDataBase.eventDao().insert(event).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Long aLong) {
                moveToEventsActivity();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void cancel(View view) {
        moveToEventsActivity();
    }

    public void moveToEventsActivity(){
        Intent intent = new Intent(this, EventsActivity.class);

        startActivity(intent);
    }
}
