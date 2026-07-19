package com.example.android_project.detail.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android_project.home.data.local.AppDatabase
import com.example.android_project.home.data.local.SettingsManager
import com.example.android_project.home.data.model.Movie
import com.example.android_project.home.data.repository.MovieRepository
import com.example.android_project.shared_component.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository by lazy {
        val database = AppDatabase.getDatabase(application)
        val settings = SettingsManager(application)
        MovieRepository(API.movieService, database.movieDao(), settings)
    }

    suspend fun getMovieDetail(imdbId: String): Movie? = withContext(Dispatchers.IO) {
        repository.getMovieDetail(imdbId)
    }

    suspend fun addToFavorites(movie: Movie) = withContext(Dispatchers.IO) {
        repository.addToFavorites(movie)
    }

    suspend fun removeFromFavorites(imdbId: String) = withContext(Dispatchers.IO) {
        repository.removeFromFavorites(imdbId)
    }

    suspend fun addToWatchlist(movie: Movie) = withContext(Dispatchers.IO) {
        repository.addToWatchlist(movie)
    }

    suspend fun removeFromWatchlist(imdbId: String) = withContext(Dispatchers.IO) {
        repository.removeFromWatchlist(imdbId)
    }

    suspend fun isFavorite(imdbId: String): Boolean = withContext(Dispatchers.IO) {
        repository.isFavorite(imdbId)
    }
    
    suspend fun isInWatchlist(imdbId: String): Boolean = withContext(Dispatchers.IO) {
        repository.isInWatchlist(imdbId)
    }

    suspend fun addToRecentlyViewed(movie: Movie) = withContext(Dispatchers.IO) {
        repository.addToRecentlyViewed(movie)
    }

    suspend fun updateUserRating(imdbId: String, rating: Float) = withContext(Dispatchers.IO) {
        val movie = repository.getMovieDetail(imdbId)
        if (movie != null) {
            movie.userRating = rating
            repository.insertMovie(movie)
        }
    }

    suspend fun getRecommendations(genre: String): List<Movie> = withContext(Dispatchers.IO) {
        // Extract first genre for more targeted search
        val firstGenre = genre.split(",").firstOrNull()?.trim() ?: genre
        repository.searchMovies(firstGenre).take(10)
    }
}

class DetailViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
