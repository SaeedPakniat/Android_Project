package com.example.android_project.home.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.android_project.home.data.model.Movie;
import com.example.android_project.home.data.model.FavoriteMovie;
import com.example.android_project.home.data.model.WatchlistMovie;
import com.example.android_project.home.data.model.RecentlyViewedMovie;

@Database(entities = {Movie.class, FavoriteMovie.class, WatchlistMovie.class, RecentlyViewedMovie.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "movie_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
