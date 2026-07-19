package com.example.android_project.home.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recently_viewed")
public class RecentlyViewedMovie {
    @PrimaryKey
    @NonNull
    private String imdbId;
    private long timestamp;

    public RecentlyViewedMovie(@NonNull String imdbId, long timestamp) {
        this.imdbId = imdbId;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getImdbId() { return imdbId; }
    public long getTimestamp() { return timestamp; }
}
