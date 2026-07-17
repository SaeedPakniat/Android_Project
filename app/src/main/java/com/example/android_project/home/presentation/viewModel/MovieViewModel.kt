package com.example.android_project.home.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android_project.home.data.local.AppDatabase
import com.example.android_project.home.data.local.SettingsManager
import com.example.android_project.home.data.model.Movie
import com.example.android_project.home.data.repository.MovieRepository
import com.example.android_project.shared_component.API

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository by lazy {
        val database = AppDatabase.getDatabase(application)
        val settings = SettingsManager(application)
        MovieRepository(API.movieService, database.movieDao(), settings)
    }

    suspend fun getFeaturedMovie(): Movie? {
        // "The Rookie" (2018) series
        return repository.getMovieDetail("tt7587890")
    }

    suspend fun getRandomPicks(): List<Movie> {
        // Since random picks don't need ratings for sorting, we just use local or some generic search
        // For simplicity, let's just get whatever is in DB or search for 'popular'
        val results = repository.searchMovies("action")
        return results.shuffled().take(10)
    }

    suspend fun getGenreMovies(genre: String): List<Movie> {
        return repository.searchMovies(genre).take(9)
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return repository.searchMovies(query)
    }

    suspend fun updateSortSettings(type: String, enabled: Boolean) {
        repository.setSortSettings(type, enabled)
    }

    suspend fun getSortType() = repository.getSortType()
    suspend fun isSortEnabled() = repository.isSortEnabled()
}

class MovieViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return MovieViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
