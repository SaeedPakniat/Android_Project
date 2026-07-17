package com.example.android_project.watchlist.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android_project.home.data.local.AppDatabase
import com.example.android_project.home.data.local.SettingsManager
import com.example.android_project.home.data.model.Movie
import com.example.android_project.home.data.repository.MovieRepository
import com.example.android_project.shared_component.API

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository by lazy {
        val database = AppDatabase.getDatabase(application)
        val settings = SettingsManager(application)
        MovieRepository(API.movieService, database.movieDao(), settings)
    }

    suspend fun getWatchlist(): List<Movie> {
        return repository.getWatchlist()
    }

    suspend fun removeFromWatchlist(imdbId: String) {
        repository.removeFromWatchlist(imdbId)
    }

    suspend fun addToFavourites(movie: Movie) {
        repository.addToFavourites(movie)
    }

    suspend fun isFavourite(imdbId: String): Boolean {
        return repository.isFavourite(imdbId)
    }
}

class WatchlistViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WatchlistViewModel::class.java)) {
            return WatchlistViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
