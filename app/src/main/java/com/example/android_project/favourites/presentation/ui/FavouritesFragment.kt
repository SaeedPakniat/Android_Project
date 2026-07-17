package com.example.android_project.favourites.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android_project.databinding.FragmentFavouritesBinding
import com.example.android_project.detail.presentation.ui.DetailActivity
import com.example.android_project.favourites.presentation.viewModel.FavouritesViewModel
import com.example.android_project.favourites.presentation.viewModel.FavouritesViewModelFactory
import com.example.android_project.home.presentation.ui.MovieAdapter
import kotlinx.coroutines.launch

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavouritesViewModel
    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, FavouritesViewModelFactory(requireActivity().application))[FavouritesViewModel::class.java]

        setupRecyclerView()
        refreshData()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            mutableListOf(),
            onDeleteClick = { movie ->
                lifecycleScope.launch {
                    viewModel.removeFromFavourites(movie.imdbId)
                    refreshData()
                }
            },
            onItemClick = { movie ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("imdb_id", movie.imdbId)
                startActivity(intent)
            }
        )
        binding.rvFavourites.apply {
            adapter = this@FavouritesFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun refreshData() {
        lifecycleScope.launch {
            val movies = viewModel.getFavourites()
            if (movies.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.rvFavourites.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.rvFavourites.visibility = View.VISIBLE
                adapter.updateMovies(movies)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
