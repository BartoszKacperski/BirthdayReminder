package com.rolnik.birthdayreminder.activities;

import com.rolnik.birthdayreminder.AsyncTaskSchedulerRule;
import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.Utils;
import com.rolnik.birthdayreminder.fragments.AddEventFragment;
import com.rolnik.birthdayreminder.model.Event;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class AddEventActivityTest {
    @Rule
    public ActivityTestRule<AddEventActivity> addEventActivityActivityTestRule = new ActivityTestRule<>(AddEventActivity.class);
    @Rule
    public AsyncTaskSchedulerRule asyncTaskSchedulerRule = new AsyncTaskSchedulerRule();
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_CONTACTS);

    @Test
    public void testSuccessfulAddEvent() {
        onView(withId(R.id.eventType)).perform(click());
        onData(Matchers.is(Matchers.instanceOf(Event.EventType.class))).atPosition(0).perform(click());
        onView(withId(R.id.eventTitle)).perform(typeText("title"), closeSoftKeyboard());
        onView(withId(R.id.showDatePicker)).perform(click());
        onView(withId(R.id.month)).perform(Utils.setNumberPickerValue(2));
        onView(withId(R.id.day)).perform(Utils.setNumberPickerValue(2));
        onView(withId(R.id.acceptButton)).perform(click());
        onView(withId(R.id.notification)).perform(click());
        onView(withId(R.id.phoneSwitch)).perform(click());
        onView(withId(R.id.contacts)).check(matches(isDisplayed()));

        Event event = ((AddEventFragment) addEventActivityActivityTestRule.getActivity().getSupportFragmentManager().getFragments().get(0)).addEventFragmentBinding.getEvent();

        assertThat(event.getTitle()).isEqualTo("title");
        assertThat(event.getEventType()).isEqualTo(Event.EventType.values()[0]);
        assertThat(event.getDate().get(Calendar.MONTH)).isEqualTo(2);
        assertThat(event.getDate().get(Calendar.DAY_OF_MONTH)).isEqualTo(2);
        assertThat(event.isHasNotification()).isEqualTo(true);
    }

    @Test
    public void testUnsuccessfulAddEvent() {
        onView(withId(R.id.saveButton)).perform(click());
        onView(withText(R.string.fill_title)).check(matches(isDisplayed()));
    }
}