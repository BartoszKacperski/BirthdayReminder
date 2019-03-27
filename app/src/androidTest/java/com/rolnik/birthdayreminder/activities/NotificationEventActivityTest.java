package com.rolnik.birthdayreminder.activities;


import android.content.Intent;
import android.net.Uri;

import com.rolnik.birthdayreminder.AsyncTaskSchedulerRule;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.database.EventDataBase;
import com.rolnik.birthdayreminder.mock.MockApplication;
import com.rolnik.birthdayreminder.mock.MockDBComponent;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.model.PhoneContact;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import javax.inject.Inject;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

public class NotificationEventActivityTest {
    private static final String PHONE_NUMBER = "123-123-123" ;
    @Rule
    public IntentsTestRule<NotificationEventActivity> notificationEventActivityIntentsTestRule
            = new IntentsTestRule<>(NotificationEventActivity.class, false ,false);

    @Rule
    public AsyncTaskSchedulerRule asyncTaskSchedulerRule = new AsyncTaskSchedulerRule();

    @Inject
    EventDataBase eventDataBase;

    private int id;

    @Before
    public void setUp(){
        ((MockDBComponent)((MockApplication) MockApplication.getAppContext()).getDbComponent()).inject(this);
        addEvent();
    }


    @Test
    public void testSuccessfulCallIntent() {
        launchActivityWithAddedEvent();
        onView(withId(R.id.phone)).perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_DIAL),
                hasData(Uri.parse("tel:" + PHONE_NUMBER))
        ));
    }

    @Test
    public void testSuccessfulOpenSmsIntent() {
        launchActivityWithAddedEvent();
        onView(withId(R.id.sms)).perform(click());
        intended(allOf(
                hasAction(Intent.ACTION_SENDTO),
                hasData(Uri.parse("smsto:" + PHONE_NUMBER))
        ));
    }


    private void addEvent(){
        PhoneContact phoneContact = PhoneContact.builder()
                .phoneNumber(PHONE_NUMBER)
                .name("name")
                .build();

        Event event = Event.builder()
                .title("tak")
                .eventType(Event.EventType.PARTY)
                .date(Calendar.getInstance())
                .hasNotification(true)
                .phoneContact(phoneContact)
                .build();

        id = Math.toIntExact(eventDataBase.eventDao().insert(event).blockingGet());
    }

    private void launchActivityWithAddedEvent(){
        Intent intent = new Intent();
        intent.putExtra(MockApplication.getAppContext().getString(R.string.event_id), id);
        notificationEventActivityIntentsTestRule.launchActivity(intent);
    }
}