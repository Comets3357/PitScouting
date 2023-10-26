package com.example.pitscoutingapp2023;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.pitscoutingapp2023.common.ActiveEventKey;
import com.example.pitscoutingapp2023.common.Team;
import com.example.pitscoutingapp2023.common.TeamPitScout;
import com.example.pitscoutingapp2023.dao.ActiveEventKeyDao;
import com.example.pitscoutingapp2023.dao.TeamDao;
import com.example.pitscoutingapp2023.dao.TeamPitScoutDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Team.class, TeamPitScout.class, ActiveEventKey.class}, version = 2, exportSchema = true, autoMigrations = {
        @AutoMigration(from = 1, to = 2)
})
public abstract class AppDatabase extends RoomDatabase {
    public abstract TeamDao teamDao();
    public abstract TeamPitScoutDao teamPitScoutDao();
    public abstract ActiveEventKeyDao activeEventKeyDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
