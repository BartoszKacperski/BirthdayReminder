package com.rolnik.birthdayreminder.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.utils.TextWatcherAdapter;
import com.rolnik.birthdayreminder.adapters.EventTypeAdapter;
import com.rolnik.birthdayreminder.adapters.PhoneContactsAdapter;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.databinding.AddEventFragmentBinding;
import com.rolnik.birthdayreminder.dialogs.DatePickerDialog;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.model.PhoneContact;
import com.rolnik.birthdayreminder.notificationserivces.AlarmCreator;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.github.florent37.runtimepermission.RuntimePermission.askPermission;

public class AddEventFragment extends Fragment implements View.OnClickListener {

    private static final String EVENT_BUNDLE = "event";

    public static AddEventFragment getInstance(@Nullable Event event){
        AddEventFragment addEventFragment = new AddEventFragment();
        if(event != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable(EVENT_BUNDLE, event);
            addEventFragment.setArguments(bundle);
        }

        return addEventFragment;
    }

    @BindView(R.id.root)
    ConstraintLayout root;
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
    @BindView(R.id.saveButton)
    Button saveButton;
    @BindView(R.id.cancelButton)
    Button cancelButton;
    @BindView(R.id.showDatePicker)
    Button showDatePicker;

    @Inject
    EventDataBase eventDataBase;
    public AddEventFragmentBinding addEventFragmentBinding;


    private DatePickerDialog datePickerDialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Activity activity;
    private FragmentCallback fragmentCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addEventFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.add_event_fragment, container, false);

        Event event;
        Bundle bundle = getArguments();
        if(bundle != null){
            event = (Event) bundle.getSerializable(EVENT_BUNDLE);
        } else {
            event = new Event();
            event.setDate(Calendar.getInstance());
        }

        addEventFragmentBinding.setEvent(event);
        return addEventFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        ButterKnife.bind(this, view);
        bindViewClicks();
        activity = getActivity();

        initPhoneSwitch();
        initEventTypeSpinner();
        initPhoneContactsSpinner();
        initTitleText();
        initDatePickerDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            fragmentCallback = (FragmentCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement " + FragmentCallback.class.getName());
        }
        ((BirthdayReminderApplication)context.getApplicationContext()).getDbComponent().inject(this);
    }

    @Override
    public void onClick(View view) {
        Log.i("Click", " " + view.getId());
        switch (view.getId()){
            case R.id.showDatePicker:
                showDatePicker();
                break;
            case R.id.saveButton:
                save();
                break;
            case R.id.cancelButton:
                cancel();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    private void bindViewClicks() {
        showDatePicker.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    private void showDatePicker() {
        datePickerDialog.show();
    }

    private void save() {
        if (checkIfEventsIsValid()) {
            saveEvent();
        }
    }

    private void cancel() {
        fragmentCallback.onCancel();
    }

    private void initEventTypeSpinner() {
        eventType.setAdapter(new EventTypeAdapter(activity, android.R.layout.simple_list_item_1));
    }

    private void initPhoneContactsSpinner(){
        contacts.setAdapter(new PhoneContactsAdapter(activity, R.layout.phonecontact_spinner_layout, new ArrayList<>()));

        PhoneContact eventPhoneContact = addEventFragmentBinding.getEvent().getPhoneContact();

        if(eventPhoneContact != null){
            ((PhoneContactsAdapter)contacts.getAdapter()).add(eventPhoneContact);
        }
    }

    private void sortContacts(List<PhoneContact> phoneContacts){
        Collator coll = Collator.getInstance(Locale.getDefault());
        coll.setStrength(Collator.PRIMARY);

        Collections.sort(phoneContacts, (phoneContact1, phoneContact2) -> coll.compare(phoneContact1.getName(), phoneContact2.getName()));
    }

    private void addContactsToSpinner(List<PhoneContact> phoneContacts) {
        sortContacts(phoneContacts);
        ((PhoneContactsAdapter)contacts.getAdapter()).addAll(phoneContacts);
    }

    private void initDatePickerDialog() {
        datePickerDialog = new DatePickerDialog(activity);
        datePickerDialog.setOnAcceptListener(view -> {
            addEventFragmentBinding.getEvent().setDate(datePickerDialog.getDate());
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
            askForPermission();
            TransitionManager.beginDelayedTransition(root);
            contacts.setVisibility(visibility);
            if(!b){
                addEventFragmentBinding.getEvent().setPhoneContact(null);
            }
        });
    }

    private void askForPermission() {
        askPermission(this).request(Manifest.permission.READ_CONTACTS).onAccepted(result -> {
            downloadContacts();
        }).ask();
    }

    private boolean checkIfEventsIsValid() {
        Event createdEvent = addEventFragmentBinding.getEvent();

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
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    private void saveEvent() {
        Event event = addEventFragmentBinding.getEvent();

        compositeDisposable.add(eventDataBase.eventDao().update(event).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    Log.i("Saving event", "Event successfully add with id = " + id);
                    if (event.isHasNotification()) {
                        event.setId(id.intValue());
                        AlarmCreator.createAlarm(activity, event);
                    }

                    fragmentCallback.onSave();
                }, t -> {
                    Log.e("Saving event", "Error message = " + t.getMessage());
                    showToast(getString(R.string.saving_error));
                }));

    }


    private void downloadContacts() {
        Callable<List<PhoneContact>> listCallable = this::getContactList;
        Observable<List<PhoneContact>> phoneContactObservable = Observable.fromCallable(listCallable);

        compositeDisposable.add(phoneContactObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(phoneContacts -> {
                    Log.i("Download contacts", "Downloaded " + phoneContacts.size() + " contacts");
                    addContactsToSpinner(phoneContacts);
                }, t -> Log.e("Download contacts", "Error occured " + t.getMessage())));
    }


    private List<PhoneContact> getContactList() {
        Cursor cursor = activity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        List<PhoneContact> phoneContacts = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phones = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
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
