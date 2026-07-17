package com.example.android_project.detail.presentation.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.android_project.databinding.ActivityDetailBinding
import com.example.android_project.detail.presentation.viewModel.DetailViewModel
import com.example.android_project.detail.presentation.viewModel.DetailViewModelFactory
import com.example.android_project.home.data.model.Movie
import com.example.android_project.shared_component.ImageLoader
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private var currentMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, DetailViewModelFactory(application))[DetailViewModel::class.java]

        binding.ivBack.setOnClickListener { finish() }

        val imdbId = intent.getStringExtra("imdb_id") ?: return
        loadMovieDetail(imdbId)
    }

    private fun loadMovieDetail(imdbId: String) {
        lifecycleScope.launch {
            val movie = viewModel.getMovieDetail(imdbId)
            if (movie != null) {
                currentMovie = movie
                displayMovie(movie)
                updateButtonStates(imdbId)
            } else {
                Toast.makeText(this@DetailActivity, "Error loading movie details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayMovie(movie: Movie) {
        binding.tvDetailTitle.text = movie.title
        binding.tvDetailYear.text = movie.year
        binding.tvDetailRuntime.text = movie.runtime
        binding.tvDetailRating.text = movie.imdbRating
        binding.tvDetailGenre.text = movie.genre
        binding.tvDetailPlot.text = movie.plot
        binding.tvDetailDirector.text = movie.director
        binding.tvDetailActors.text = movie.actors
        binding.tvDetailReleased.text = movie.released

        ImageLoader.load(movie.poster, binding.ivDetailPoster, android.R.drawable.ic_menu_gallery)
    }

    private fun updateButtonStates(imdbId: String) {
        lifecycleScope.launch {
            val isFav = viewModel.isFavourite(imdbId)
            val isInWatchlist = viewModel.isInWatchlist(imdbId)

            binding.btnFavourite.text = if (isFav) "Remove Favourite" else "Add Favourite"
            binding.btnFavourite.setOnClickListener {
                lifecycleScope.launch {
                    if (isFav) {
                        viewModel.removeFromFavourites(imdbId)
                    } else {
                        currentMovie?.let { viewModel.addToFavourites(it) }
                    }
                    updateButtonStates(imdbId)
                }
            }

            binding.btnWatchlist.text = if (isInWatchlist) "Remove Watchlist" else "Add Watchlist"
            binding.btnWatchlist.setOnClickListener {
                lifecycleScope.launch {
                    if (isInWatchlist) {
                        viewModel.removeFromWatchlist(imdbId)
                    } else {
                        currentMovie?.let { viewModel.addToWatchlist(it) }
                    }
                    updateButtonStates(imdbId)
                }
            }
        }
    }
}
