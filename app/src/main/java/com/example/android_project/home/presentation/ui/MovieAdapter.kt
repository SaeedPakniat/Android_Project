package com.example.android_project.home.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android_project.R
import com.example.android_project.databinding.ItemMovieGridBinding
import com.example.android_project.home.data.model.Movie
import com.example.android_project.shared_component.ImageLoader

class MovieAdapter(
    private val items: MutableList<Movie>,
    private val sortType: String = "IMDb",
    private val onDeleteClick: ((Movie) -> Unit)? = null,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateMovies(newItems: List<Movie>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemMovieGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Movie) {
            binding.tvMovieTitle.text = item.title
            
            val rating = when (sortType) {
                "Rotten Tomatoes" -> {
                    binding.ivRatingSource.setImageResource(R.drawable.ic_rt)
                    binding.ivRatingSource.setColorFilter(0) // No tint for RT icon usually if it has colors
                    item.rottenTomatoesRating
                }
                "Metacritic" -> {
                    binding.ivRatingSource.setImageResource(R.drawable.ic_meta)
                    binding.ivRatingSource.setColorFilter(ContextCompat.getColor(itemView.context, R.color.white))
                    item.metacriticRating
                }
                else -> {
                    binding.ivRatingSource.setImageResource(R.drawable.ic_imdb)
                    binding.ivRatingSource.setColorFilter(ContextCompat.getColor(itemView.context, R.color.accent_yellow))
                    item.imdbRating
                }
            }

            binding.tvMovieInfo.text = "${item.year} | $rating"

            ImageLoader.load(item.poster, binding.ivMoviePoster, android.R.drawable.ic_menu_gallery)

            if (onDeleteClick != null) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.setOnClickListener { onDeleteClick.invoke(item) }
            } else {
                binding.btnDelete.visibility = View.GONE
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}
