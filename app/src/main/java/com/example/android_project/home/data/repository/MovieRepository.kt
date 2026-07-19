package com.example.android_project.home.data.repository

import com.example.android_project.home.data.local.MovieDao
import com.example.android_project.home.data.local.SettingsManager
import com.example.android_project.home.data.model.*
import com.example.android_project.shared_component.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class MovieRepository(
    private val api: APIService,
    private val dao: MovieDao,
    private val settings: SettingsManager
) {

    suspend fun getMovies(): List<Movie> = withContext(Dispatchers.IO) {
        dao.getAllMovies()
    }

    suspend fun searchMovies(query: String): List<Movie> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchMovies(query)
            if (response.isSuccessful && response.body()?.response == "True") {
                val searchItems = response.body()?.search ?: emptyList()
                val movies = searchItems.map { searchItem ->
                    val detailResponse = api.getMovieDetail(searchItem.imdbId)
                    if (detailResponse.isSuccessful) {
                        detailResponse.body()?.toEntity() ?: searchItem.toEntity()
                    } else {
                        searchItem.toEntity()
                    }
                }
                dao.insertMovies(movies)
                sortMovies(movies)
            } else {
                sortMovies(dao.searchMovies(query))
            }
        } catch (e: Exception) {
            sortMovies(dao.searchMovies(query))
        }
    }

    suspend fun getMovieDetail(imdbId: String): Movie? = withContext(Dispatchers.IO) {
        try {
            val response = api.getMovieDetail(imdbId)
            if (response.isSuccessful) {
                val movie = response.body()?.toEntity()
                if (movie != null) {
                    dao.insertMovie(movie)
                    movie
                } else {
                    dao.getMovieById(imdbId)
                }
            } else {
                dao.getMovieById(imdbId)
            }
        } catch (e: Exception) {
            dao.getMovieById(imdbId)
        }
    }

    suspend fun getFavorites(): List<Movie> = withContext(Dispatchers.IO) {
        sortMovies(dao.getFavoriteMovies())
    }

    suspend fun getWatchlist(): List<Movie> = withContext(Dispatchers.IO) {
        sortMovies(dao.getWatchlistMovies())
    }

    suspend fun insertMovie(movie: Movie) = withContext(Dispatchers.IO) {
        dao.insertMovie(movie)
    }

    suspend fun addToFavorites(movie: Movie) = withContext(Dispatchers.IO) {
        dao.insertMovie(movie)
        dao.insertFavorite(FavoriteMovie(movie.imdbId))
    }

    suspend fun removeFromFavorites(imdbId: String) = withContext(Dispatchers.IO) {
        dao.deleteFavorite(imdbId)
    }

    suspend fun clearFavorites() = withContext(Dispatchers.IO) {
        dao.clearFavorites()
    }

    suspend fun addToWatchlist(movie: Movie) = withContext(Dispatchers.IO) {
        dao.insertMovie(movie)
        dao.insertWatchlist(WatchlistMovie(movie.imdbId))
    }

    suspend fun removeFromWatchlist(imdbId: String) = withContext(Dispatchers.IO) {
        dao.deleteWatchlist(imdbId)
    }

    suspend fun clearWatchlist() = withContext(Dispatchers.IO) {
        dao.clearWatchlist()
    }

    suspend fun isFavorite(imdbId: String): Boolean = withContext(Dispatchers.IO) {
        dao.isFavorite(imdbId)
    }
    
    suspend fun isInWatchlist(imdbId: String): Boolean = withContext(Dispatchers.IO) {
        dao.isInWatchlist(imdbId)
    }

    suspend fun addToRecentlyViewed(movie: Movie) = withContext(Dispatchers.IO) {
        dao.insertMovie(movie)
        dao.insertRecentlyViewed(RecentlyViewedMovie(movie.imdbId, System.currentTimeMillis()))
    }

    suspend fun getRecentlyViewed(): List<Movie> = withContext(Dispatchers.IO) {
        dao.recentlyViewedMovies
    }

    suspend fun clearRecentlyViewed() = withContext(Dispatchers.IO) {
        dao.clearRecentlyViewed()
    }

    suspend fun sortMovies(movies: List<Movie>): List<Movie> {
        val isEnabled = settings.isSortEnabled()
        if (!isEnabled) return movies

        val sortType = settings.getSortType()
        return withContext(Dispatchers.Default) {
            movies.sortedWith(compareByDescending<Movie> { movie ->
                val rating = when (sortType) {
                    "Rotten Tomatoes" -> movie.rottenTomatoesRating
                    "Metacritic" -> movie.metacriticRating
                    else -> movie.imdbRating
                }
                if (rating == null || rating == "N/A" || rating.isEmpty()) -1.0 
                else rating.replace("%", "").replace("/100", "").replace("/10", "").toDoubleOrNull() ?: -1.0
            })
        }
    }

    suspend fun setSortSettings(type: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        settings.setSortType(type)
        settings.setSortEnabled(enabled)
    }
    
    suspend fun getSortType() = withContext(Dispatchers.IO) {
        settings.getSortType()
    }
    
    suspend fun isSortEnabled() = withContext(Dispatchers.IO) {
        settings.isSortEnabled()
    }

    private fun MovieDetailResponse.toEntity(): Movie {
        val rt = ratings?.find { it.source == "Rotten Tomatoes" }?.value ?: "N/A"
        val meta = ratings?.find { it.source == "Metacritic" }?.value ?: "N/A"
        val movie = Movie()
        movie.imdbId = imdbId ?: UUID.randomUUID().toString()
        movie.title = title ?: "Unknown"
        movie.year = year ?: "N/A"
        movie.rated = rated ?: "N/A"
        movie.released = released ?: "N/A"
        movie.runtime = runtime ?: "N/A"
        movie.genre = genre ?: "N/A"
        movie.director = director ?: "N/A"
        movie.actors = actors ?: "N/A"
        movie.plot = plot ?: "N/A"
        movie.language = language ?: "N/A"
        movie.country = country ?: "N/A"
        movie.awards = awards ?: "N/A"
        movie.poster = poster ?: ""
        movie.imdbRating = imdbRating ?: "N/A"
        movie.type = type ?: "movie"
        movie.rottenTomatoesRating = rt
        movie.metacriticRating = meta
        return movie
    }

    private fun MovieSearchItem.toEntity(): Movie {
        return Movie(imdbId, title, year, poster, type)
    }
}
