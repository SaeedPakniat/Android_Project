package com.example.android_project.home.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {
    @PrimaryKey
    @androidx.annotation.NonNull
    public String imdbId;
    public String title;
    public String year;
    public String rated;
    public String released;
    public String runtime;
    public String genre;
    public String director;
    public String actors;
    public String plot;
    public String language;
    public String country;
    public String awards;
    public String poster;
    public String imdbRating;
    public String type;
    public String rottenTomatoesRating;
    public String metacriticRating;
    public boolean isFavorite;
    public boolean isInWatchlist;
    public float userRating;

    public Movie() {}

    @androidx.room.Ignore
    public Movie(String imdbId, String title, String year, String poster, String type) {
        this.imdbId = imdbId;
        this.title = title;
        this.year = year;
        this.poster = poster;
        this.type = type;
        this.rottenTomatoesRating = "N/A";
        this.metacriticRating = "N/A";
    }
}
