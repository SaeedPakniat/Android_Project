package com.example.android_project.watchlist.presentation.viewModel

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

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository by lazy {
        val database = AppDatabase.getDatabase(application)
        val settings = SettingsManager(application)
        MovieRepository(API.movieService, database.movieDao(), settings)
    }

    suspend fun getWatchlist(): List<Movie> = withContext(Dispatchers.IO) {
        repository.getWatchlist()
    }

    suspend fun removeFromWatchlist(imdbId: String) = withContext(Dispatchers.IO) {
        repository.removeFromWatchlist(imdbId)
    }

    suspend fun addToFavourites(movie: Movie) = withContext(Dispatchers.IO) {
        repository.addToFavourites(movie)
    }

    suspend fun isFavourite(imdbId: String): Boolean = withContext(Dispatchers.IO) {
        repository.isFavourite(imdbId)
    }

    suspend fun getSortType() = withContext(Dispatchers.IO) {
        repository.getSortType()
    }

    suspend fun isSortEnabled() = withContext(Dispatchers.IO) {
        repository.isSortEnabled()
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
