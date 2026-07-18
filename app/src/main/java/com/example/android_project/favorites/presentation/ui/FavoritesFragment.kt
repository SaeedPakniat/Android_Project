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
import com.example.android_project.databinding.FragmentFavoritesBinding
import com.example.android_project.detail.presentation.ui.DetailActivity
import com.example.android_project.favorites.presentation.viewModel.FavoritesViewModel
import com.example.android_project.favorites.presentation.viewModel.FavoritesViewModelFactory
import com.example.android_project.home.presentation.ui.MovieAdapter
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var adapter: MovieAdapter

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
        refreshData()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            mutableListOf(),
            onDeleteClick = { movie ->
                lifecycleScope.launch {
                    viewModel.removeFromFavorites(movie.imdbId)
                    refreshData()
                }
            },
            onItemClick = { movie ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("imdb_id", movie.imdbId)
                startActivity(intent)
            }
        )
        binding.rvFavorites.apply {
            adapter = this@FavoritesFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            val movies = viewModel.getFavorites()
            val sortType = viewModel.getSortType()
            val isEnabled = viewModel.isSortEnabled()
            adapter.updateSort(sortType, isEnabled)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
