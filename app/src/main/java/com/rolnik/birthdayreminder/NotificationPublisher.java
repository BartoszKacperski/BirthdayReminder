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
        long id = intent.getLongExtra(context.getString(R.string.event_id), -1);

        downloadEventAndShowNotification(id, context);

    }

    private void downloadEventAndShowNotification(long id, Context context) {
        EventDataBase eventDataBase = DataBaseService.getEventDataBaseInstance(context);

        eventDataBase.eventDao().getEventWith((int)id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Event>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Event event) {
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(getNotificationId(context), createNotification(context, event));
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Error", "message = " + e.getMessage());
            }
        });
    }

    private Notification createNotification(final Context context, final Event currentEvent){
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(context.getString(R.string.channel_id), context.getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new Notification.Builder(context, context.getString(R.string.channel_id));
        } else {
            builder = new Notification.Builder(context);
        }

        builder.setSmallIcon(DataBindingAdapters.eventTypeToDrawableResourceId(currentEvent.getEventType()));
        builder.setContentTitle(context.getString(DataBindingAdapters.eventTypeToStringResourceId(currentEvent.getEventType())));
        builder.setContentText(currentEvent.getTitle());
        builder.setAutoCancel(true);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);

        return builder.build();
    }

    private int getNotificationId(final Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        int value = sharedPreferences.getInt(context.getString(R.string.notification_id), 0);

        sharedPreferencesEditor.putInt(context.getString(R.string.notification_id), value + 1);
        sharedPreferencesEditor.apply();


        return value;
    }
}
