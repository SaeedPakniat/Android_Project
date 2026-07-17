package com.example.android_project.home.data.model

import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(
    @SerializedName("Title")
    val title: String?,
    @SerializedName("Year")
    val year: String?,
    @SerializedName("Rated")
    val rated: String?,
    @SerializedName("Released")
    val released: String?,
    @SerializedName("Runtime")
    val runtime: String?,
    @SerializedName("Genre")
    val genre: String?,
    @SerializedName("Director")
    val director: String?,
    @SerializedName("Actors")
    val actors: String?,
    @SerializedName("Plot")
    val plot: String?,
    @SerializedName("Language")
    val language: String?,
    @SerializedName("Country")
    val country: String?,
    @SerializedName("Awards")
    val awards: String?,
    @SerializedName("Poster")
    val poster: String?,
    @SerializedName("imdbRating")
    val imdbRating: String?,
    @SerializedName("imdbID")
    val imdbId: String?,
    @SerializedName("Type")
    val type: String?,
    @SerializedName("Ratings")
    val ratings: List<Rating>?,
    @SerializedName("Response")
    val response: String?
)

data class Rating(
    @SerializedName("Source")
    val source: String?,
    @SerializedName("Value")
    val value: String?
)
