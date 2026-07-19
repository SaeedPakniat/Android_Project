package com.example.android_project.detail.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_project.databinding.ActivityDetailBinding
import com.example.android_project.detail.presentation.viewModel.DetailViewModel
import com.example.android_project.detail.presentation.viewModel.DetailViewModelFactory
import com.example.android_project.home.data.model.Movie
import com.example.android_project.home.presentation.ui.RandomMovieAdapter
import com.example.android_project.shared_component.ImageLoader
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var recommendationsAdapter: RandomMovieAdapter
    private var currentMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, DetailViewModelFactory(application))[DetailViewModel::class.java]

        binding.ivBack.setOnClickListener { finish() }

        binding.ivShare.setOnClickListener { shareMovie() }

        binding.btnTrailer.setOnClickListener { watchTrailer() }

        binding.containerImdb.setOnClickListener { openExternalLink("https://www.imdb.com/title/${currentMovie?.imdbId}") }
        binding.containerRt.setOnClickListener { openExternalLink("https://www.rottentomatoes.com/search?search=${currentMovie?.title}") }
        binding.containerMeta.setOnClickListener { openExternalLink("https://www.metacritic.com/search/movie/${currentMovie?.title}/results") }

        setupRecommendationsRecyclerView()

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
                viewModel.addToRecentlyViewed(movie)
                loadRecommendations(movie.genre ?: "")
            } else {
                Toast.makeText(this@DetailActivity, "Error loading movie details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecommendationsRecyclerView() {
        recommendationsAdapter = RandomMovieAdapter(emptyList()) { movie ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("imdb_id", movie.imdbId)
            startActivity(intent)
        }
        binding.rvRecommendations.apply {
            adapter = recommendationsAdapter
            layoutManager = LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun loadRecommendations(genre: String) {
        lifecycleScope.launch {
            val recommendations = viewModel.getRecommendations(genre)
                .filter { it.imdbId != currentMovie?.imdbId } // Don't show current movie
            
            if (recommendations.isNotEmpty()) {
                binding.tvRecommendationsTitle.visibility = View.VISIBLE
                binding.rvRecommendations.visibility = View.VISIBLE
                recommendationsAdapter.updateMovies(recommendations)
            }
        }
    }

    private fun displayMovie(movie: Movie) {
        binding.tvDetailTitle.text = movie.title
        binding.tvDetailYear.text = movie.year
        binding.tvDetailRuntime.text = movie.runtime
        binding.tvDetailRatingImdb.text = movie.imdbRating
        binding.tvDetailRatingRt.text = movie.rottenTomatoesRating
        binding.tvDetailRatingMeta.text = movie.metacriticRating

        binding.tvDetailGenre.text = movie.genre
        binding.tvDetailPlot.text = movie.plot
        binding.tvDetailDirector.text = movie.director
        binding.tvDetailActors.text = movie.actors
        binding.tvDetailReleased.text = movie.released

        binding.ratingBar.rating = movie.userRating
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                lifecycleScope.launch {
                    viewModel.updateUserRating(movie.imdbId, rating)
                    Toast.makeText(this@DetailActivity, "Rating saved: $rating stars", Toast.LENGTH_SHORT).show()
                }
            }
        }

        ImageLoader.load(movie.poster, binding.ivDetailPoster, android.R.drawable.ic_menu_gallery)
    }

    private fun updateButtonStates(imdbId: String) {
        lifecycleScope.launch {
            val isFav = viewModel.isFavorite(imdbId)
            val isInWatchlist = viewModel.isInWatchlist(imdbId)

            binding.btnDetailFavorite.imageTintList = android.content.res.ColorStateList.valueOf(
                if (isFav) android.graphics.Color.RED else android.graphics.Color.WHITE
            )
            binding.btnDetailFavorite.setOnClickListener {
                lifecycleScope.launch {
                    if (isFav) {
                        viewModel.removeFromFavorites(imdbId)
                        Toast.makeText(this@DetailActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        currentMovie?.let { 
                            viewModel.addToFavorites(it)
                            Toast.makeText(this@DetailActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()
                        }
                    }
                    updateButtonStates(imdbId)
                }
            }

            binding.btnDetailWatchlist.imageTintList = android.content.res.ColorStateList.valueOf(
                if (isInWatchlist) androidx.core.content.ContextCompat.getColor(this@DetailActivity, com.example.android_project.R.color.accent_yellow) else android.graphics.Color.WHITE
            )
            binding.btnDetailWatchlist.setOnClickListener {
                lifecycleScope.launch {
                    if (isInWatchlist) {
                        viewModel.removeFromWatchlist(imdbId)
                        Toast.makeText(this@DetailActivity, "Removed from Watchlist", Toast.LENGTH_SHORT).show()
                    } else {
                        currentMovie?.let { 
                            viewModel.addToWatchlist(it) 
                            Toast.makeText(this@DetailActivity, "Added to Watchlist", Toast.LENGTH_SHORT).show()
                        }
                    }
                    updateButtonStates(imdbId)
                }
            }
        }
    }

    private fun shareMovie() {
        val movie = currentMovie ?: return
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this movie!")
            val text = "Check out ${movie.title} (${movie.year}) on IMDb: https://www.imdb.com/title/${movie.imdbId}\n\nPlot: ${movie.plot}"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(shareIntent, "Share movie via"))
    }

    private fun openExternalLink(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun watchTrailer() {
        val movie = currentMovie ?: return
        val query = "${movie.title} ${movie.year} official trailer"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "YouTube not found, opening in browser", Toast.LENGTH_SHORT).show()
            openExternalLink("https://www.youtube.com/results?search_query=${Uri.encode(query)}")
        }
    }
}
