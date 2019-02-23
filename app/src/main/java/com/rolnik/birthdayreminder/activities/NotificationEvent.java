package com.rolnik.birthdayreminder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.databinding.ActivityNotificationEventBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationEvent extends AppCompatActivity {

    private ActivityNotificationEventBinding activityNotificationEventBinding;
    private Disposable d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNotificationEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification_event);

        if(getIntent().hasExtra(getString(R.string.event_id))){
            downloadEvent(getIntent().getIntExtra(getString(R.string.event_id), -1));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(d != null){
            d.dispose();
        }
    }

    private void downloadEvent(final int id){
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(getApplicationContext());

        eventDataBase.eventDao().getEventWith(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> d = disposable)
                .doOnSuccess(event -> activityNotificationEventBinding.setEvent(event))
                .subscribe();
    }

    public void openContacts(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    public void openSms(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "", null));

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }
}
