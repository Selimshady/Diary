package com.example.diary.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Memory.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MemoryDao memoryDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context)
    {
        if(INSTANCE == null)
        {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"MEMORY")
                .allowMainThreadQueries()
                .build();
        }
        return  INSTANCE;
    }
}
