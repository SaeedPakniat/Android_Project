package com.example.android_project.home.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "watchlist_movies")
public class WatchlistMovie {
    @PrimaryKey
    @androidx.annotation.NonNull
    public String imdbId;

    public WatchlistMovie() {}

    public WatchlistMovie(String imdbId) {
        this.imdbId = imdbId;
    }
}
