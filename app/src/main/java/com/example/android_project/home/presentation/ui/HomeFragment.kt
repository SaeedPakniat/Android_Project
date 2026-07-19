package com.example.android_project.home.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MovieViewModel

    private lateinit var randomAdapter: RandomMovieAdapter
    private lateinit var recentAdapter: RandomMovieAdapter
    private lateinit var trendingAdapter: MovieAdapter
    private lateinit var upcomingAdapter: MovieAdapter
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
        viewModel = ViewModelProvider(requireActivity(), MovieViewModelFactory(requireActivity().application))[MovieViewModel::class.java]

        setupRandomPicksRecyclerView()
        setupRecentlyViewedRecyclerView()
        setupTrendingRecyclerView()
        setupUpcomingRecyclerView()
        setupGenreRecyclerView()
        setupSearchRecyclerView()
        setupSearchBar()
        setupGenreChips()
        setupSorting()
        setupRefreshLayout()
        setupFeaturedClick()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInitialData()
    }

    private fun setupFeaturedClick() {
        binding.featuredContainer.setOnClickListener {
            viewModel.featuredMovie.value?.let { movie ->
                viewModel.addToRecentlyViewed(movie)
                openDetail(movie)
            }
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadInitialData()
        }
        // Optional: customize colors
        binding.swipeRefresh.setColorSchemeResources(R.color.accent_yellow)
        binding.swipeRefresh.setProgressBackgroundColorSchemeResource(R.color.bg_card)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    viewModel.featuredMovie.collectLatest { movie ->
                        movie?.let {
                            binding.tvFeaturedTitle.text = it.title
                            binding.tvFeaturedInfo.text = "${it.year} | ${it.runtime} | ${it.imdbRating} IMDb"
                            ImageLoader.load(it.poster, binding.ivFeaturedPoster, android.R.drawable.ic_menu_gallery)
                        }
                    }
                }
                launch {
                    viewModel.randomPicks.collectLatest { movies ->
                        randomAdapter.updateMovies(movies)
                        updateAdaptersSort()
                    }
                }
                launch {
                    viewModel.genreMovies.collectLatest { movies ->
                        genreAdapter.updateMovies(movies)
                        updateAdaptersSort()
                    }
                }
                launch {
                    viewModel.trendingMovies.collectLatest { movies ->
                        trendingAdapter.updateMovies(movies)
                        updateAdaptersSort()
                    }
                }
                launch {
                    viewModel.upcomingMovies.collectLatest { movies ->
                        upcomingAdapter.updateMovies(movies)
                        updateAdaptersSort()
                    }
                }
                launch {
                    viewModel.recentlyViewed.collectLatest { movies ->
                        if (movies.isEmpty()) {
                            binding.recentlyViewedContainer.visibility = View.GONE
                        } else {
                            binding.recentlyViewedContainer.visibility = View.VISIBLE
                            recentAdapter.updateMovies(movies)
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        binding.swipeRefresh.isRefreshing = isLoading
                    }
                }
            }
        }
    }



    private fun setupRandomPicksRecyclerView() {
        randomAdapter = RandomMovieAdapter(emptyList()) { movie ->
            viewModel.addToRecentlyViewed(movie)
            openDetail(movie)
        }
        binding.rvRandomPicks.apply {
            adapter = randomAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupRecentlyViewedRecyclerView() {
        recentAdapter = RandomMovieAdapter(emptyList()) { movie ->
            viewModel.addToRecentlyViewed(movie)
            openDetail(movie)
        }
        binding.rvRecentlyViewed.apply {
            adapter = recentAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        binding.btnClearRecent.setOnClickListener {
            viewModel.clearRecentlyViewed()
        }
    }

    private fun setupTrendingRecyclerView() {
        trendingAdapter = MovieAdapter(emptyList()) { movie ->
            viewModel.addToRecentlyViewed(movie)
            openDetail(movie)
        }
        binding.rvTrending.apply {
            adapter = trendingAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setupUpcomingRecyclerView() {
        upcomingAdapter = MovieAdapter(emptyList()) { movie ->
            viewModel.addToRecentlyViewed(movie)
            openDetail(movie)
        }
        binding.rvUpcoming.apply {
            adapter = upcomingAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setupGenreRecyclerView() {
        genreAdapter = MovieAdapter(emptyList()) { movie ->
            viewModel.addToRecentlyViewed(movie)
            openDetail(movie)
        }
        binding.rvGenreMovies.apply {
            adapter = genreAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = MovieAdapter(emptyList()) { movie ->
            viewModel.addToRecentlyViewed(movie)
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
                    
                    searchJob = lifecycleScope.launch {
                        delay(500) // Debounce
                        val results = viewModel.searchMovies(query)
                        searchAdapter.updateMovies(results)
                    }
                } else {
                    binding.ivSearchClear.visibility = View.GONE
                    binding.homeContentContainer.visibility = View.VISIBLE
                    binding.searchResultsContainer.visibility = View.GONE
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
            updateAdaptersSort()
            
            // Re-run search if active to apply sort order
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
            
            val imdbSelected = isEnabled && type == "IMDb"
            val rtSelected = isEnabled && type == "Rotten Tomatoes"
            val metaSelected = isEnabled && type == "Metacritic"

            binding.btnSortImdb.isSelected = imdbSelected
            binding.tvSortImdb.setTextColor(if (imdbSelected) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            // Only tint IMDb logo black when selected so it's visible on yellow background
            binding.ivSortImdb.imageTintList = if (imdbSelected) android.content.res.ColorStateList.valueOf(android.graphics.Color.BLACK) else null

            binding.btnSortRt.isSelected = rtSelected
            binding.tvSortRt.setTextColor(if (rtSelected) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            binding.ivSortRt.imageTintList = null // Keep original brand colors

            binding.btnSortMeta.isSelected = metaSelected
            binding.tvSortMeta.setTextColor(if (metaSelected) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            binding.ivSortMeta.imageTintList = null // Keep original brand colors
        }
    }

    private fun updateAdaptersSort() {
        lifecycleScope.launch {
            val type = viewModel.getSortType()
            val enabled = viewModel.isSortEnabled()
            
            randomAdapter.updateSort(type, enabled)
            recentAdapter.updateSort(type, enabled)
            trendingAdapter.updateSort(type, enabled)
            upcomingAdapter.updateSort(type, enabled)
            genreAdapter.updateSort(type, enabled)
            searchAdapter.updateSort(type, enabled)

            // Re-sort current lists to ensure they match the selected criteria
            randomAdapter.updateMovies(viewModel.sortMovies(randomAdapter.getMovies()))
            recentAdapter.updateMovies(viewModel.sortMovies(recentAdapter.getMovies()))
            trendingAdapter.updateMovies(viewModel.sortMovies(trendingAdapter.getMovies()))
            upcomingAdapter.updateMovies(viewModel.sortMovies(upcomingAdapter.getMovies()))
            genreAdapter.updateMovies(viewModel.sortMovies(genreAdapter.getMovies()))
            searchAdapter.updateMovies(viewModel.sortMovies(searchAdapter.getMovies()))
        }
    }

    private fun setupGenreChips() {
        binding.chipGroupGenres.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(binding.chipHorror.id) -> {
                    viewModel.selectGenre("horror")
                }
                checkedIds.contains(binding.chipScifi.id) -> {
                    viewModel.selectGenre("sci-fi")
                }
                checkedIds.contains(binding.chipDrama.id) -> {
                    viewModel.selectGenre("drama")
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
