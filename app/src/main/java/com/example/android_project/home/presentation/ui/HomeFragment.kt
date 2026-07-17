package com.example.android_project.home.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_project.R
import com.example.android_project.databinding.FragmentHomeBinding
import com.example.android_project.detail.presentation.ui.DetailActivity
import com.example.android_project.home.data.model.Movie
import com.example.android_project.home.presentation.viewModel.MovieViewModel
import com.example.android_project.home.presentation.viewModel.MovieViewModelFactory
import com.example.android_project.shared_component.ImageLoader
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MovieViewModel

    private lateinit var randomAdapter: RandomMovieAdapter
    private lateinit var genreAdapter: MovieAdapter
    private lateinit var searchAdapter: MovieAdapter
    
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, MovieViewModelFactory(requireActivity().application))[MovieViewModel::class.java]

        setupRandomPicksRecyclerView()
        setupGenreRecyclerView()
        setupSearchRecyclerView()
        setupSearchBar()
        setupGenreChips()
        setupSorting()
        loadInitialData()
    }

    private fun loadInitialData() {
        lifecycleScope.launch {
            val featured = viewModel.getFeaturedMovie()
            featured?.let { movie ->
                binding.tvFeaturedTitle.text = movie.title
                binding.tvFeaturedInfo.text = "${movie.year} | ${movie.runtime} | ${movie.imdbRating} IMDb"
                ImageLoader.load(movie.poster, binding.ivFeaturedPoster, android.R.drawable.ic_menu_gallery)
            }

            val random = viewModel.getRandomPicks()
            randomAdapter.updateMovies(random)

            // Initial genre: Horror
            val horror = viewModel.getGenreMovies("horror")
            genreAdapter.updateMovies(horror)
        }
    }

    private fun setupRandomPicksRecyclerView() {
        randomAdapter = RandomMovieAdapter(mutableListOf()) { movie ->
            openDetail(movie)
        }
        binding.rvRandomPicks.apply {
            adapter = randomAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupGenreRecyclerView() {
        genreAdapter = MovieAdapter(mutableListOf()) { movie ->
            openDetail(movie)
        }
        binding.rvGenreMovies.apply {
            adapter = genreAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = MovieAdapter(mutableListOf()) { movie ->
            openDetail(movie)
        }
        binding.rvSearchResults.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                searchJob?.cancel()
                if (query.isNotEmpty()) {
                    binding.ivSearchClear.visibility = View.VISIBLE
                    binding.homeContentContainer.visibility = View.GONE
                    binding.searchResultsContainer.visibility = View.VISIBLE
                    binding.sortingContainer.visibility = View.VISIBLE
                    
                    searchJob = lifecycleScope.launch {
                        delay(500) // Debounce
                        val results = viewModel.searchMovies(query)
                        searchAdapter.updateMovies(results)
                    }
                } else {
                    binding.ivSearchClear.visibility = View.GONE
                    binding.homeContentContainer.visibility = View.VISIBLE
                    binding.searchResultsContainer.visibility = View.GONE
                    binding.sortingContainer.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.ivSearchClear.setOnClickListener {
            binding.etSearch.text?.clear()
        }
    }

    private fun setupSorting() {
        binding.btnSortImdb.setOnClickListener { toggleSort("IMDb") }
        binding.btnSortRt.setOnClickListener { toggleSort("Rotten Tomatoes") }
        binding.btnSortMeta.setOnClickListener { toggleSort("Metacritic") }
        
        updateSortingUI()
    }

    private fun toggleSort(type: String) {
        lifecycleScope.launch {
            val currentType = viewModel.getSortType()
            val isEnabled = viewModel.isSortEnabled()
            
            if (isEnabled && currentType == type) {
                viewModel.updateSortSettings(type, false)
            } else {
                viewModel.updateSortSettings(type, true)
            }
            updateSortingUI()
            
            // Re-run search to apply sort
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                val results = viewModel.searchMovies(query)
                searchAdapter.updateMovies(results)
            }
        }
    }

    private fun updateSortingUI() {
        lifecycleScope.launch {
            val type = viewModel.getSortType()
            val isEnabled = viewModel.isSortEnabled()
            
            val selectedColor = ContextCompat.getColor(requireContext(), R.color.accent_yellow)
            val defaultColor = ContextCompat.getColor(requireContext(), R.color.bg_card)
            
            binding.btnSortImdb.setBackgroundResource(if (isEnabled && type == "IMDb") R.drawable.bg_nav_selected else R.drawable.bg_chip_imdb)
            binding.btnSortRt.setBackgroundResource(if (isEnabled && type == "Rotten Tomatoes") R.drawable.bg_nav_selected else R.drawable.bg_chip_imdb)
            binding.btnSortMeta.setBackgroundResource(if (isEnabled && type == "Metacritic") R.drawable.bg_nav_selected else R.drawable.bg_chip_imdb)
        }
    }

    private fun setupGenreChips() {
        binding.chipGroupGenres.setOnCheckedStateChangeListener { _, checkedIds ->
            lifecycleScope.launch {
                when {
                    checkedIds.contains(binding.chipHorror.id) -> {
                        genreAdapter.updateMovies(viewModel.getGenreMovies("horror"))
                    }
                    checkedIds.contains(binding.chipScifi.id) -> {
                        genreAdapter.updateMovies(viewModel.getGenreMovies("sci-fi"))
                    }
                    checkedIds.contains(binding.chipDrama.id) -> {
                        genreAdapter.updateMovies(viewModel.getGenreMovies("drama"))
                    }
                }
            }
        }
    }

    private fun openDetail(movie: Movie) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("imdb_id", movie.imdbId)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
