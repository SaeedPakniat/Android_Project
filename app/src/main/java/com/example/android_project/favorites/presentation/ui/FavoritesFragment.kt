package com.example.android_project.favorites.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_project.databinding.FragmentFavoritesBinding
import com.example.android_project.detail.presentation.ui.DetailActivity
import com.example.android_project.favorites.presentation.viewModel.FavoritesViewModel
import com.example.android_project.favorites.presentation.viewModel.FavoritesViewModelFactory
import com.example.android_project.shared_component.HorizontalMovieAdapter
import com.example.android_project.home.data.model.Movie
import android.text.Editable
import android.text.TextWatcher
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var adapter: HorizontalMovieAdapter
    private var allMovies: List<Movie> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, FavoritesViewModelFactory(requireActivity().application))[FavoritesViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        setupClearAll()
        refreshData()
    }

    private fun setupClearAll() {
        binding.btnClearAll.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(requireContext(), com.example.android_project.R.style.CustomDialogTheme)
                .setTitle("Clear Favorites")
                .setMessage("Are you sure you want to remove all movies from your favorites?")
                .setPositiveButton("Clear All") { _, _ ->
                    lifecycleScope.launch {
                        viewModel.clearFavorites()
                        refreshData()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
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
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvFavorites.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        adapter = HorizontalMovieAdapter(
            emptyList(),
            com.example.android_project.R.drawable.ic_heart,
            onMovieClick = { movie ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("imdb_id", movie.imdbId)
                startActivity(intent)
            },
            onActionClick = { movie ->
                lifecycleScope.launch {
                    viewModel.removeFromFavorites(movie.imdbId)
                    refreshData()
                }
            }
        )
        binding.rvFavorites.apply {
            adapter = this@FavoritesFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            val movies = viewModel.getFavorites()
            allMovies = movies
            
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                filterMovies(query)
            } else {
                if (movies.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvFavorites.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvFavorites.visibility = View.VISIBLE
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
