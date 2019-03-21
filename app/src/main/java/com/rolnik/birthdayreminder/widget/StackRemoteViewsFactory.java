package com.rolnik.birthdayreminder.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.activities.DataBindingAdapters;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.model.Event;

import java.util.ArrayList;
import java.util.List;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<Event> events = new ArrayList<>();
    private Context context;
    private EventDataBase eventDataBase;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent, EventDataBase eventDataBase) {
        this.context = context;
        this.mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        this.eventDataBase = eventDataBase;
    }

    public void onCreate() {
        downloadEvents();
    }

    public void onDestroy() {
        events.clear();
    }

    public int getCount() {
        return events.size();
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item);

        if(position < getCount()){
            Event event = events.get(position);
            rv.setTextViewText(R.id.eventTitle, event.getTitle());
            rv.setTextViewText(R.id.eventDate, DataBindingAdapters.calendarToString(event.getDate()));
            rv.setImageViewResource(R.id.eventImage, DataBindingAdapters.eventTypeToDrawableResourceId(event.getEventType()));
        }

        return rv;
    }

    public RemoteViews getLoadingView() {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.eventTitle, "Loading");
        rv.setTextViewText(R.id.eventDate, "...");
        rv.setImageViewResource(R.id.eventImage, R.drawable.ic_error_black);
        return rv;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        downloadEvents();
    }

    public void downloadEvents(){
        this.events.addAll(eventDataBase.eventDao().getAll().blockingFirst());
    }
}