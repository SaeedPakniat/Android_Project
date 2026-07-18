package com.example.android_project.shared_component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_project.R
import com.example.android_project.databinding.ItemMovieHorizontalBinding
import com.example.android_project.home.data.model.Movie

class HorizontalMovieAdapter(
    private var movies: List<Movie>,
    private val actionIconRes: Int,
    private val onMovieClick: (Movie) -> Unit,
    private val onActionClick: (Movie) -> Unit
) : RecyclerView.Adapter<HorizontalMovieAdapter.MovieViewHolder>() {

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieHorizontalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class MovieViewHolder(private val binding: ItemMovieHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.tvTitle.text = movie.title
            binding.tvInfo.text = "${movie.year} | ${movie.genre ?: movie.type}"
            binding.tvRating.text = movie.imdbRating ?: "N/A"
            
            ImageLoader.load(movie.poster, binding.ivPoster)

            binding.root.setOnClickListener { onMovieClick(movie) }
            binding.ivAction.setOnClickListener { onActionClick(movie) }
            
            binding.ivAction.setImageResource(actionIconRes)
        }
    }
}
