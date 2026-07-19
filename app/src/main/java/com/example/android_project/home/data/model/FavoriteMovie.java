package com.example.android_project.home.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_movies")
public class FavoriteMovie {
    @PrimaryKey
    @androidx.annotation.NonNull
    public String imdbId;

    public FavoriteMovie() {}

    @androidx.room.Ignore
    public FavoriteMovie(String imdbId) {
        this.imdbId = imdbId;
    }
}
