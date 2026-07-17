package com.example.android_project.shared_component

import com.example.android_project.home.data.model.MovieDetailResponse
import com.example.android_project.home.data.model.MovieSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET(".")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("apikey") apiKey: String = "359d64e8"
    ): Response<MovieSearchResponse>

    @GET(".")
    suspend fun getMovieDetail(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String = "359d64e8"
    ): Response<MovieDetailResponse>
}
