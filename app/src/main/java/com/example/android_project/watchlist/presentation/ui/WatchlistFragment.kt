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
import com.example.android_project.databinding.FragmentWatchlistBinding
import com.example.android_project.detail.presentation.ui.DetailActivity
import com.example.android_project.home.presentation.ui.MovieAdapter
import com.example.android_project.watchlist.presentation.viewModel.WatchlistViewModel
import com.example.android_project.watchlist.presentation.viewModel.WatchlistViewModelFactory
import kotlinx.coroutines.launch

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WatchlistViewModel
    private lateinit var adapter: MovieAdapter

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
        refreshData()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            mutableListOf(),
            onDeleteClick = { movie ->
                lifecycleScope.launch {
                    val isFav = viewModel.isFavourite(movie.imdbId)
                    if (!isFav) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Remove from Watchlist")
                            .setMessage("Add to Favourites before removing?")
                            .setPositiveButton("Yes") { _, _ ->
                                lifecycleScope.launch {
                                    viewModel.addToFavourites(movie)
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
            },
            onItemClick = { movie ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("imdb_id", movie.imdbId)
                startActivity(intent)
            }
        )
        binding.rvWatchlist.apply {
            adapter = this@WatchlistFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            val movies = viewModel.getWatchlist()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
