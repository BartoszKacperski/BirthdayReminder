package com.rolnik.birthdayreminder.database;

import android.content.Context;

import com.rolnik.birthdayreminder.BirthdayReminderApplication;
import com.rolnik.birthdayreminder.model.Event;
import com.rolnik.birthdayreminder.model.PhoneContact;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import androidx.room.Room;

import static org.junit.Assert.*;

public class EventDaoTest {

    private EventDao eventDao;
    private EventDataBase db;

    @Before
    public void createDb() {
        Context context = BirthdayReminderApplication.getAppContext();
        db = Room.inMemoryDatabaseBuilder(context, EventDataBase.class).build();
        eventDao = db.eventDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsertAndGetAll() {
        PhoneContact phoneContact = PhoneContact.builder()
                .phoneNumber("123")
                .name("name")
                .build();

        Event event = Event.builder()
                .id(1)
                .title("tak")
                .eventType(Event.EventType.PARTY)
                .date(Calendar.getInstance())
                .hasNotification(true)
                .phoneContact(phoneContact)
                .build();

        long id = eventDao.insert(event).blockingGet();

        List<Event> events = eventDao.getAll().blockingFirst();

        assertEquals(1, events.size());
        assertEquals(1, events.get(0).getId());
        assertEquals(1, id);
    }

    @Test
    public void testInsertAndGetEventWith() {
        PhoneContact phoneContact = PhoneContact.builder()
                .phoneNumber("123")
                .name("name")
                .build();

        Event event = Event.builder()
                .id(1)
                .title("tak")
                .eventType(Event.EventType.PARTY)
                .date(Calendar.getInstance())
                .hasNotification(true)
                .phoneContact(phoneContact)
                .build();

        long id = eventDao.insert(event).blockingGet();

        Event eventById = eventDao.getEventWith(1).blockingGet();

        assertEquals(1, id);
        assertEquals(1, eventById.getId());
    }


    @Test
    public void testInsertAndDeleteAll() {
        PhoneContact phoneContact = PhoneContact.builder()
                .phoneNumber("123")
                .name("name")
                .build();

        Event event = Event.builder()
                .id(1)
                .title("tak")
                .eventType(Event.EventType.PARTY)
                .date(Calendar.getInstance())
                .hasNotification(true)
                .phoneContact(phoneContact)
                .build();

        long id = eventDao.insert(event).blockingGet();

        int amount = eventDao.deleteAll(Collections.singletonList(event)).blockingGet();
        List<Event> events = eventDao.getAll().blockingFirst();

        assertEquals(1, amount);
        assertEquals(1, id);
        assertEquals(0, events.size());
    }

    @Test
    public void testInsertAndUpdate() {
        PhoneContact phoneContact = PhoneContact.builder()
                .phoneNumber("123")
                .name("name")
                .build();

        Event event = Event.builder()
                .id(1)
                .title("tak")
                .eventType(Event.EventType.PARTY)
                .date(Calendar.getInstance())
                .hasNotification(true)
                .phoneContact(phoneContact)
                .build();

        long id = eventDao.insert(event).blockingGet();
        Event event1 = eventDao.getEventWith((int) id).blockingGet();

        assertEquals(1, id);
        assertEquals("tak", event1.getTitle());

        event.setTitle("tak1");
        id = eventDao.update(event).blockingGet();
        event1 = eventDao.getEventWith((int) id).blockingGet();

        assertEquals(1, id);
        assertEquals("tak1", event1.getTitle());
    }
}