package com.imran.mapsync.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.imran.mapsync.room.dao.MapDao;
import com.imran.mapsync.room.model.Map;

@Database(entities = {Map.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;
    public abstract MapDao postDao();
    public static AppDatabase getAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "MapSync")
                    .build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }
}
