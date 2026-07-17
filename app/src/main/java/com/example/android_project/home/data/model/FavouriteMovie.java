package com.example.android_project.home.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourite_movies")
public class FavouriteMovie {
    @PrimaryKey
    @androidx.annotation.NonNull
    public String imdbId;

    public FavouriteMovie() {}

    public FavouriteMovie(String imdbId) {
        this.imdbId = imdbId;
    }
}
