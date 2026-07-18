package com.example.android_project.home.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android_project.home.data.local.AppDatabase
import com.example.android_project.home.data.local.SettingsManager
import com.example.android_project.home.data.model.Movie
import com.example.android_project.home.data.repository.MovieRepository
import com.example.android_project.shared_component.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository by lazy {
        val database = AppDatabase.getDatabase(application)
        val settings = SettingsManager(application)
        MovieRepository(API.movieService, database.movieDao(), settings)
    }

    suspend fun getFeaturedMovie(): Movie? = withContext(Dispatchers.IO) {
        // "The Rookie" (2018) series - ID preserved
        repository.getMovieDetail("tt7587890")
    }

    suspend fun getRandomPicks(): List<Movie> = withContext(Dispatchers.IO) {
        val keywords = listOf("war", "star", "dark", "love", "man", "blue", "king", "red", "fast")
        val randomKeywords = keywords.shuffled().take(3)
        val jobs = randomKeywords.map { keyword ->
            async { repository.searchMovies(keyword) }
        }
        val results = jobs.awaitAll().flatten()
        
        val randomList = results.shuffled().take(10)
        repository.sortMovies(randomList)
    }

    suspend fun getGenreMovies(genre: String): List<Movie> = withContext(Dispatchers.IO) {
        repository.searchMovies(genre).take(9)
    }

    suspend fun searchMovies(query: String): List<Movie> = withContext(Dispatchers.IO) {
        repository.searchMovies(query)
    }

    suspend fun sortMovies(movies: List<Movie>): List<Movie> = withContext(Dispatchers.IO) {
        repository.sortMovies(movies)
    }

    suspend fun updateSortSettings(type: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        repository.setSortSettings(type, enabled)
    }

    suspend fun getSortType() = withContext(Dispatchers.IO) {
        repository.getSortType()
    }

    suspend fun isSortEnabled() = withContext(Dispatchers.IO) {
        repository.isSortEnabled()
    }
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
