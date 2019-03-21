package com.rolnik.birthdayreminder.components;

import com.rolnik.birthdayreminder.activities.EventsActivity;
import com.rolnik.birthdayreminder.activities.NotificationEventActivity;
import com.rolnik.birthdayreminder.database.DBModule;
import com.rolnik.birthdayreminder.fragments.AddEventFragment;
import com.rolnik.birthdayreminder.notificationserivces.NotificationIntervalService;
import com.rolnik.birthdayreminder.notificationserivces.NotificationPublisher;
import com.rolnik.birthdayreminder.notificationserivces.NotificationWakeupPublisher;
import com.rolnik.birthdayreminder.widget.StackWidgetService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {DBModule.class})
public interface DBComponent {
    void inject(EventsActivity eventsActivity);
    void inject(NotificationEventActivity notificationEventActivity);
    void inject(AddEventFragment addEventFragment);
    void inject(NotificationPublisher notificationPublisher);
    void inject(NotificationIntervalService notificationIntervalService);
    void inject(NotificationWakeupPublisher notificationWakeupPublisher);
    void inject(StackWidgetService stackWidgetService);
}
