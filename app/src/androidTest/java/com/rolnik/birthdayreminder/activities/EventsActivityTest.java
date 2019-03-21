package com.rolnik.birthdayreminder.activities;

import com.rolnik.birthdayreminder.AsyncTaskSchedulerRule;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.Utils;
import com.rolnik.birthdayreminder.mock.MockApplication;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.model.PhoneContact;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

@LargeTest
public class EventsActivityTest {
    @Rule
    public ActivityTestRule<EventsActivity> activityActivityTestRule = new ActivityTestRule<>(EventsActivity.class);
    @Rule
    public AsyncTaskSchedulerRule asyncTaskSchedulerRule = new AsyncTaskSchedulerRule();

    @Before
    public void setUp(){
        Intents.init();
    }

    @Test
    public void testFirstAdd() {
        onView(withId(R.id.addFirstButton)).perform(click());
        intended(hasComponent(AddEventActivity.class.getName()));
    }

    @Test
    public void testFirstAddTablet(){
        if(Utils.isTablet(MockApplication.getAppContext())){
            onView(withId(R.id.addFirstButton)).perform(click());
            onView(withId(R.id.root)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testFilledRecyclerView(){
        Calendar calendar = new GregorianCalendar(2, 3, 4);
        PhoneContact phoneContact = PhoneContact.builder()
                .phoneNumber("123")
                .name("name")
                .build();

        Event event = Event.builder()
                .id(1)
                .title("tak")
                .eventType(Event.EventType.PARTY)
                .date(calendar)
                .hasNotification(true)
                .phoneContact(phoneContact)
                .build();

        long id = activityActivityTestRule.getActivity().eventDataBase.eventDao().insert(event).blockingGet();

        onData(anything()).inAdapterView(withId(R.id.events)).atPosition(0).
                onChildView(withId(R.id.eventDate)).
                check(matches(withText("4 IV")));

    }

    @Test
    public void testDeleteFromRecyclerView(){
        Calendar calendar = new GregorianCalendar(2, 3, 4);
        PhoneContact phoneContact = PhoneContact.builder()
                .phoneNumber("123")
                .name("name")
                .build();

        Event event = Event.builder()
                .id(1)
                .title("tak")
                .eventType(Event.EventType.PARTY)
                .date(calendar)
                .hasNotification(true)
                .phoneContact(phoneContact)
                .build();

        long id = activityActivityTestRule.getActivity().eventDataBase.eventDao().insert(event).blockingGet();

        openActionBarOverflowOrOptionsMenu(MockApplication.getAppContext());
        onView(withText("Usuń")).perform(click());
        onView(withId(R.id.events)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.delete)).perform(click());
        onView(withId(R.id.addFirstButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testShowNotificationDialog(){
        openActionBarOverflowOrOptionsMenu(MockApplication.getAppContext());
        onView(withText("Powiadomienia")).perform(click());
        onView(withText("Aby powiadomienia przychodziły pamiętaj o ich włączęniu w ustawieniach aplikacji")).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
    }

    @Test
    public void testShowIconDialog(){
        openActionBarOverflowOrOptionsMenu(MockApplication.getAppContext());
        onView(withText("Ikony")).perform(click());
        onView(withText("Autorzy")).check(matches(isDisplayed()));
        onView(withText("OK")).perform(click());
    }

    @Test
    public void testAddFromActionBar(){
        openActionBarOverflowOrOptionsMenu(MockApplication.getAppContext());
        onView(withText("Dodaj")).perform(click());
        intended(hasComponent(AddEventActivity.class.getName()));
    }

}