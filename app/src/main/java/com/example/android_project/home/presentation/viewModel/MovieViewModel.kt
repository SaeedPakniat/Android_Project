package com.example.android_project.home.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android_project.home.data.local.AppDatabase
import com.example.android_project.home.data.local.SettingsManager
import com.example.android_project.home.data.model.Movie
import com.example.android_project.home.data.repository.MovieRepository
import com.example.android_project.shared_component.API
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository by lazy {
        val database = AppDatabase.getDatabase(application)
        val settings = SettingsManager(application)
        MovieRepository(API.movieService, database.movieDao(), settings)
    }

    private val _featuredMovie = MutableStateFlow<Movie?>(null)
    val featuredMovie: StateFlow<Movie?> = _featuredMovie.asStateFlow()

    private val _randomPicks = MutableStateFlow<List<Movie>>(emptyList())
    val randomPicks: StateFlow<List<Movie>> = _randomPicks.asStateFlow()

    private val _genreMovies = MutableStateFlow<List<Movie>>(emptyList())
    val genreMovies: StateFlow<List<Movie>> = _genreMovies.asStateFlow()

    private val _recentlyViewed = MutableStateFlow<List<Movie>>(emptyList())
    val recentlyViewed: StateFlow<List<Movie>> = _recentlyViewed.asStateFlow()

    private val _trendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val trendingMovies: StateFlow<List<Movie>> = _trendingMovies.asStateFlow()

    private val _upcomingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val upcomingMovies: StateFlow<List<Movie>> = _upcomingMovies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isFeaturedFavorite = MutableStateFlow(false)
    val isFeaturedFavorite: StateFlow<Boolean> = _isFeaturedFavorite.asStateFlow()

    private val _isFeaturedInWatchlist = MutableStateFlow(false)
    val isFeaturedInWatchlist: StateFlow<Boolean> = _isFeaturedInWatchlist.asStateFlow()

    private var currentGenre: String = "horror"

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // "The Rookie" (2018) series - ID preserved
                _featuredMovie.value = repository.getMovieDetail("tt7587890")

                val keywords = listOf("war", "star", "dark", "love", "man", "blue", "king", "red", "fast")
                val randomKeywords = keywords.shuffled().take(3)
                val jobs = randomKeywords.map { keyword ->
                    async { repository.searchMovies(keyword) }
                }
                val results = jobs.awaitAll().flatten()
                val randomList = results.shuffled().take(10)
                _randomPicks.value = repository.sortMovies(randomList)

                _genreMovies.value = repository.searchMovies(currentGenre).take(9)

                _recentlyViewed.value = repository.getRecentlyViewed()

                _trendingMovies.value = repository.searchMovies("2024").take(10)
                _upcomingMovies.value = repository.searchMovies("2025").take(10)

                updateFeaturedStatus()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectGenre(genre: String) {
        currentGenre = genre
        viewModelScope.launch {
            _genreMovies.value = repository.searchMovies(genre).take(9)
        }
    }

    fun addToRecentlyViewed(movie: Movie) {
        viewModelScope.launch {
            repository.addToRecentlyViewed(movie)
            _recentlyViewed.value = repository.getRecentlyViewed()
        }
    }

    fun clearRecentlyViewed() {
        viewModelScope.launch {
            repository.clearRecentlyViewed()
            _recentlyViewed.value = emptyList()
        }
    }

    fun toggleFeaturedFavorite() {
        viewModelScope.launch {
            val movie = _featuredMovie.value ?: return@launch
            if (_isFeaturedFavorite.value) {
                repository.removeFromFavorites(movie.imdbId)
            } else {
                repository.addToFavorites(movie)
            }
            updateFeaturedStatus()
        }
    }

    fun toggleFeaturedWatchlist() {
        viewModelScope.launch {
            val movie = _featuredMovie.value ?: return@launch
            if (_isFeaturedInWatchlist.value) {
                repository.removeFromWatchlist(movie.imdbId)
            } else {
                repository.addToWatchlist(movie)
            }
            updateFeaturedStatus()
        }
    }

    fun refreshFeaturedStatus() {
        viewModelScope.launch {
            val movie = _featuredMovie.value ?: return@launch
            _isFeaturedFavorite.value = repository.isFavorite(movie.imdbId)
            _isFeaturedInWatchlist.value = repository.isInWatchlist(movie.imdbId)
        }
    }

    private fun updateFeaturedStatus() {
        refreshFeaturedStatus()
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
