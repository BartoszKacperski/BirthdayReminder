package com.rolnik.birthdayreminder.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.rolnik.birthdayreminder.DataBindingAdapters;
import com.rolnik.birthdayreminder.DatePickerDialog;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.TextWatcherAdapter;
import com.rolnik.birthdayreminder.adapters.EventTypeAdapter;
import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.databinding.ActivityAddEventBinding;
import com.rolnik.birthdayreminder.model.Event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;
    @BindView(R.id.eventTitle)
    EditText eventTitle;

    private DatePickerDialog datePickerDialog;
    private ActivityAddEventBinding activityAddEventBinding;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        ButterKnife.bind(this);

        initDatePickerDialog();
        initSpinner();
        initTitleText();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(disposable != null){
            disposable.dispose();
        }
    }

    public void showDatePicker(View view) {
        datePickerDialog.show();
    }

    public void save(View view) {
        if(checkIfEventsIsValid()){
            saveEvent();
        }
    }

    public void cancel(View view) {
        moveToEventsActivity();
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
        datePickerDialog.setOnAcceptListener(view -> {
            activityAddEventBinding.getEvent().setDate(datePickerDialog.getDate());
            datePickerDialog.dismiss();
        });
    }

    private void initTitleText(){
        eventTitle.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    textInputLayout.setError(getString(R.string.fill_title));
                } else {
                    textInputLayout.setError(null);
                }
            }
        });
    }

    private boolean checkIfEventsIsValid(){
        Event createdEvent = activityAddEventBinding.getEvent();

        if(createdEvent == null){
            //TODO
            return false;
        } else if(createdEvent.getDate() == null){
            showToast(getString(R.string.fill_date));
            return false;
        } else if(createdEvent.getTitle().isEmpty()){
            textInputLayout.setError(getString(R.string.fill_title));
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void saveEvent(){
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        Event event = activityAddEventBinding.getEvent();

        eventDataBase.eventDao().insert(event).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MaybeObserver<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i("Saving event", "Starting saving event");
                disposable = d;
            }

            @Override
            public void onSuccess(Long aLong) {
                Log.i("Saving event", "Event successfully add with id = " + aLong);
                createNotification();
                moveToEventsActivity();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Saving event", "Error message = " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i("Saving event", "Saving completed");
            }
        });
    }

    private void createNotification(){
        Event currentEvent = activityAddEventBinding.getEvent();
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, getString(R.string.channel_id));
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setSmallIcon(DataBindingAdapters.eventTypeToDrawableResourceId(currentEvent.getEventType()));
        builder.setContentTitle(getString(DataBindingAdapters.eventTypeToStringResourceId(currentEvent.getEventType())));
        builder.setContentText(currentEvent.getTitle());
        builder.setAutoCancel(true);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(getNotificationId(), builder.build());
    }

    private int getNotificationId(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        int value = sharedPreferences.getInt(getString(R.string.notification_id), 0);

        sharedPreferencesEditor.putInt(getString(R.string.notification_id), value + 1);
        sharedPreferencesEditor.apply();

        return value;
    }


    private void moveToEventsActivity(){
        Intent intent = new Intent(this, EventsActivity.class);

        startActivity(intent);
    }
}
