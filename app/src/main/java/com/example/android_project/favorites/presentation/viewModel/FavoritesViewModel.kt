package com.example.android_project.favorites.presentation.viewModel

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

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository by lazy {
        val database = AppDatabase.getDatabase(application)
        val settings = SettingsManager(application)
        MovieRepository(API.movieService, database.movieDao(), settings)
    }

    suspend fun getFavorites(): List<Movie> = withContext(Dispatchers.IO) {
        repository.getFavorites()
    }

    suspend fun removeFromFavorites(imdbId: String) = withContext(Dispatchers.IO) {
        repository.removeFromFavorites(imdbId)
    }

    suspend fun getSortType() = withContext(Dispatchers.IO) {
        repository.getSortType()
    }

    suspend fun isSortEnabled() = withContext(Dispatchers.IO) {
        repository.isSortEnabled()
    }
}

class FavoritesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            return FavoritesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
