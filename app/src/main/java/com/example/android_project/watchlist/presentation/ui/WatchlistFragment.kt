package com.example.android_project.watchlist.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_project.databinding.FragmentWatchlistBinding
import com.example.android_project.detail.presentation.ui.DetailActivity
import com.example.android_project.shared_component.HorizontalMovieAdapter
import com.example.android_project.home.data.model.Movie
import com.example.android_project.watchlist.presentation.viewModel.WatchlistViewModel
import com.example.android_project.watchlist.presentation.viewModel.WatchlistViewModelFactory
import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.launch

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WatchlistViewModel
    private lateinit var adapter: HorizontalMovieAdapter
    private var allMovies: List<Movie> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, WatchlistViewModelFactory(requireActivity().application))[WatchlistViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        refreshData()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMovies(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterMovies(query: String) {
        val filtered = if (query.isEmpty()) {
            allMovies
        } else {
            allMovies.filter { it.title?.contains(query, ignoreCase = true) == true }
        }
        adapter.updateMovies(filtered)
        
        if (filtered.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvWatchlist.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvWatchlist.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = HorizontalMovieAdapter(
            emptyList(),
            com.example.android_project.R.drawable.ic_bookmark,
            onMovieClick = { movie ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("imdb_id", movie.imdbId)
                startActivity(intent)
            },
            onActionClick = { movie ->
                showRemoveDialog(movie)
            }
        )
        binding.rvWatchlist.apply {
            adapter = this@WatchlistFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showRemoveDialog(movie: Movie) {
        lifecycleScope.launch {
            val isFav = viewModel.isFavorite(movie.imdbId)
            if (!isFav) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Remove from Watchlist")
                    .setMessage("Add to Favorites before removing?")
                    .setPositiveButton("Yes") { _, _ ->
                        lifecycleScope.launch {
                            viewModel.addToFavorites(movie)
                            viewModel.removeFromWatchlist(movie.imdbId)
                            refreshData()
                        }
                    }
                    .setNegativeButton("No") { _, _ ->
                        lifecycleScope.launch {
                            viewModel.removeFromWatchlist(movie.imdbId)
                            refreshData()
                        }
                    }
                    .show()
            } else {
                viewModel.removeFromWatchlist(movie.imdbId)
                refreshData()
            }
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            val movies = viewModel.getWatchlist()
            allMovies = movies
            
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                filterMovies(query)
            } else {
                if (movies.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvWatchlist.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvWatchlist.visibility = View.VISIBLE
                    adapter.updateMovies(movies)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
