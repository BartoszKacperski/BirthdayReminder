package com.rolnik.birthdayreminder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.databinding.ActivityNotificationEventBinding;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.model.PhoneContact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NotificationEvent extends AppCompatActivity {

    private ActivityNotificationEventBinding activityNotificationEventBinding;
    private Disposable d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNotificationEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification_event);

        if (getIntent().hasExtra(getString(R.string.event_id))) {
            int id = getIntent().getIntExtra(getString(R.string.event_id), -1);
            Log.i("Event id =", " " + id);
            downloadEvent(id);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (d != null) {
            d.dispose();
        }
    }

    private void downloadEvent(final int id) {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        d = eventDataBase.eventDao().getEventWith(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    activityNotificationEventBinding.setEvent(event);
                    Log.i("Event id =", " " + event.getId());
                }, throwable -> Log.e("Error occured", "Message = " + throwable.getMessage()));
    }

    public void call(View view) {
        PhoneContact phoneContact = activityNotificationEventBinding.getEvent().getPhoneContact();
        if(phoneContact != null){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneContact.getPhoneNumber()));

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            showToast(getString(R.string.phone_contact_missing));
        }
    }

    public void openSms(View view) {
        PhoneContact phoneContact = activityNotificationEventBinding.getEvent().getPhoneContact();

        if(phoneContact != null){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("address", phoneContact.getPhoneNumber());

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            showToast(getString(R.string.phone_contact_missing));
        }
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
