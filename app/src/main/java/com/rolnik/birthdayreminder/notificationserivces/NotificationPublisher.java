package com.rolnik.birthdayreminder.notificationserivces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.database.EventDataBase;

import javax.inject.Inject;

import androidx.core.app.NotificationManagerCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationPublisher extends BroadcastReceiver {
    @Inject
    EventDataBase eventDataBase;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((BirthdayReminderApplication) context.getApplicationContext()).getDbComponent().inject(this);
        int id = intent.getIntExtra(context.getString(R.string.event_id), -1);

        downloadEventAndShowNotification(id, context);
    }

    private void downloadEventAndShowNotification(int id, Context context) {
        Disposable d = eventDataBase.eventDao().getEventWith(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                event -> {
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    notificationManagerCompat.notify(NotificationCreator.getNotificationId(context), NotificationCreator.createNotification(context, event));
                }
        );
    }
}
