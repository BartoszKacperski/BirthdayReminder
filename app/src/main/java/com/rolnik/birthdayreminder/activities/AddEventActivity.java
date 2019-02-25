package com.rolnik.birthdayreminder.activities;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.TextWatcherAdapter;
import com.rolnik.birthdayreminder.adapters.EventTypeAdapter;
import com.rolnik.birthdayreminder.adapters.PhoneContactsAdapter;
import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.databinding.ActivityAddEventBinding;
import com.rolnik.birthdayreminder.dialogs.DatePickerDialog;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.model.PhoneContact;
import com.rolnik.birthdayreminder.notificationserivces.AlarmCreator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.github.florent37.runtimepermission.RuntimePermission.askPermission;

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
    TextInputEditText eventTitle;
    @BindView(R.id.contacts)
    Spinner contacts;
    @BindView(R.id.phoneSwitch)
    CheckBox phoneSwitch;

    private DatePickerDialog datePickerDialog;
    private ActivityAddEventBinding activityAddEventBinding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_event);
        ButterKnife.bind(this);

        initPhoneSwitch();
        initEventBinding();
        initEventTypeSpinner();
        initTitleText();
        initDatePickerDialog();
        askForPermission();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public void showDatePicker(View view) {
        datePickerDialog.show();
    }

    public void save(View view) {
        if (checkIfEventsIsValid()) {
            saveEvent();
        }
    }

    public void cancel(View view) {
        moveToEventsActivity();
    }


    private void initEventBinding() {
        Event event;

        if (getIntent().hasExtra(getString(R.string.event))) {
            event = (Event) getIntent().getSerializableExtra(getString(R.string.event));
        } else {
            event = new Event();
            event.setDate(Calendar.getInstance());
        }

        activityAddEventBinding.setEvent(event);
    }

    private void initEventTypeSpinner() {
        eventType.setAdapter(new EventTypeAdapter(this, android.R.layout.simple_list_item_1));
    }

    private void initContactsSpinner(List<PhoneContact> phoneContacts) {
        Collections.sort(phoneContacts, (phoneContact, t1) -> phoneContact.getName().compareTo(t1.getName()));
        contacts.setAdapter(new PhoneContactsAdapter(this, R.layout.phonecontact_spinner_layout, phoneContacts));

        PhoneContact currentPhoneContact = activityAddEventBinding.getEvent().getPhoneContact();

        if(currentPhoneContact != null){
            int pos = ((PhoneContactsAdapter) contacts.getAdapter()).getPosition(currentPhoneContact);
            contacts.setSelection(pos, true);
        }
    }

    private void initDatePickerDialog() {
        datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnAcceptListener(view -> {
            activityAddEventBinding.getEvent().setDate(datePickerDialog.getDate());
            datePickerDialog.dismiss();
        });
    }

    private void initTitleText() {
        eventTitle.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    textInputLayout.setError(getString(R.string.fill_title));
                } else {
                    textInputLayout.setError(null);
                }
            }
        });
    }

    private void initPhoneSwitch() {
        phoneSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            int visibility = b ? View.VISIBLE : View.GONE;
            TransitionManager.beginDelayedTransition(root);
            contacts.setVisibility(visibility);
            if(!b){
                activityAddEventBinding.getEvent().setPhoneContact(null);
            }
        });
    }

    private void askForPermission() {
        askPermission(this).request(Manifest.permission.READ_CONTACTS).onAccepted(result -> {
            downloadContacts();
        }).ask();
    }

    private boolean checkIfEventsIsValid() {
        Event createdEvent = activityAddEventBinding.getEvent();

        if (createdEvent == null) {
            //TODO
            return false;
        } else if (createdEvent.getDate() == null) {
            showToast(getString(R.string.fill_date));
            return false;
        } else if (createdEvent.getTitle() == null || createdEvent.getTitle().isEmpty()) {
            textInputLayout.setError(getString(R.string.fill_title));
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void saveEvent() {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        Event event = activityAddEventBinding.getEvent();

        compositeDisposable.add(eventDataBase.eventDao().insert(event).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    Log.i("Saving event", "Event successfully add with id = " + id);
                    if (event.isHasNotification()) {
                        event.setId(id.intValue());
                        AlarmCreator.createAlarm(getApplicationContext(), event);
                    }
                    moveToEventsActivity();
                }, t -> {
                    Log.e("Saving event", "Error message = " + t.getMessage());
                    showToast(getString(R.string.saving_error));
                }));

    }


    private void moveToEventsActivity() {
        Intent intent = new Intent(this, EventsActivity.class);

        startActivity(intent);
    }

    private void downloadContacts() {
        Callable<List<PhoneContact>> listCallable = this::getContactList;
        Observable<List<PhoneContact>> phoneContactObservable = Observable.fromCallable(listCallable);

        compositeDisposable.add(phoneContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(phoneContacts -> {
                    Log.i("Download contacts", "Downloaded " + phoneContacts.size() + " contacts");
                    initContactsSpinner(phoneContacts);
                }, t -> {
                    Log.e("Download contacts", "Error occured " + t.getMessage());
                }));
    }


    private List<PhoneContact> getContactList() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        List<PhoneContact> phoneContacts = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phones != null) {
                    if (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        phoneContacts.add(new PhoneContact(name, phoneNumber));
                    }
                    phones.close();
                }

            }
            cursor.close();
        }

        return phoneContacts;
    }
}
