package com.rolnik.birthdayreminder.database;

import androidx.room.Room;
import android.content.Context;

public class DataBaseService {
    private static String databaseName = "BirthdayDB";
    private static EventDataBase INSTANCE = null;

    private DataBaseService(){

    }


    public static EventDataBase getEventDataBaseInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, EventDataBase.class, databaseName).build();
        }

        return INSTANCE;
    }
}
