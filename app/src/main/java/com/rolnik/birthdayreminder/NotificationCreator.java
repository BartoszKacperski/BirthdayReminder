package com.rolnik.birthdayreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.rolnik.birthdayreminder.model.Event;

import androidx.core.app.NotificationCompat;

public class NotificationCreator {

    private NotificationCreator(){

    }

    public static Notification createNotification(final Context context, final Event currentEvent){
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

    public static int getNotificationId(final Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        int value = sharedPreferences.getInt(context.getString(R.string.notification_id), 0);

        sharedPreferencesEditor.putInt(context.getString(R.string.notification_id), value + 1);
        sharedPreferencesEditor.apply();


        return value;
    }
}
