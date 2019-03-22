package com.rolnik.birthdayreminder.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.activities.AddEventActivity;
import com.rolnik.birthdayreminder.activities.EventsActivity;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {


    @Override
    public void onReceive(Context context, Intent intent){
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            Intent serviceIntent = new Intent(context, StackWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(R.id.stack, serviceIntent);
            remoteViews.setEmptyView(R.id.stack, R.id.stackEmptyView);

            Intent openMainActivityIntent = new Intent(context, EventsActivity.class);

            PendingIntent pendingIntent1 = PendingIntent.getActivity(context, context.getResources().getInteger(R.integer.pendingWidgetID), openMainActivityIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.stackEmptyView, pendingIntent1);

            Intent openAddEventActivityIntent = new Intent(context, AddEventActivity.class);

            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, openAddEventActivityIntent, 0);
            remoteViews.setPendingIntentTemplate(R.id.stack, pendingIntent2);

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

