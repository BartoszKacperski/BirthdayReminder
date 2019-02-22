package com.rolnik.birthdayreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.rolnik.birthdayreminder.database.DataBaseService;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationPublisher extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Notification publisher", "RECEIVED!! " + intent.hasExtra(context.getString(R.string.event_id)));
        int id = intent.getIntExtra(context.getString(R.string.event_id), -1);

        downloadEventAndShowNotification(id, context);

    }

    private void downloadEventAndShowNotification(int id, Context context) {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(context);

        eventDataBase.eventDao().getEventWith(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Event>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Event event) {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(NotificationCreator.getNotificationId(context), NotificationCreator.createNotification(context, event));
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Error", "message = " + e.getMessage());
            }
        });
    }



}
