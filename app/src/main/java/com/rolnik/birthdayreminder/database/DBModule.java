package com.rolnik.birthdayreminder.database;

import android.content.Context;

import com.rolnik.birthdayreminder.R;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class DBModule {
    private Context context;

    public DBModule(Context context){
        this.context = context;
    }

    @Provides
    @Singleton
    public EventDataBase getDataBase(){
        String databaseName = context.getString(R.string.database_name);
        return Room.databaseBuilder(context, EventDataBase.class, databaseName).build();
    }
}
