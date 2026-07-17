package com.example.android_project.home.data.model

import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    @SerializedName("Search")
    val search: List<MovieSearchItem>?,
    @SerializedName("totalResults")
    val totalResults: String?,
    @SerializedName("Response")
    val response: String?
)

data class MovieSearchItem(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("imdbID")
    val imdbId: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val poster: String
)
