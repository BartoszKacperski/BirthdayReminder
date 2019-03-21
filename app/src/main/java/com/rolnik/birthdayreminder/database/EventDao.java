package com.rolnik.birthdayreminder.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.rolnik.birthdayreminder.model.Event;

import java.util.List;


@Dao
public interface EventDao {
    @Query("Select * from events")
    Observable<List<Event>> getAll();
    @Query("Select * from events where id = :id")
    Single<Event> getEventWith(int id);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Maybe<Long> update(Event event);
    @Insert()
    Maybe<Long> insert(Event event);
    @Delete
    Maybe<Integer> deleteAll(List<Event> events);
}
