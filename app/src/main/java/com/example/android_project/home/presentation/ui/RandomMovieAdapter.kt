package com.example.android_project.home.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.android_project.R
import com.example.android_project.databinding.ItemMovieRandomBinding
import com.example.android_project.home.data.model.Movie
import com.example.android_project.shared_component.ImageLoader

class RandomMovieAdapter(
    private var items: List<Movie>,
    private var sortType: String = "IMDb",
    private var isSortEnabled: Boolean = false,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<RandomMovieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieRandomBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun getMovies() = items

    fun updateMovies(newItems: List<Movie>) {
        if (items == newItems) return
        items = newItems
        notifyDataSetChanged()
    }

    fun updateSort(newSortType: String, enabled: Boolean) {
        if (sortType == newSortType && isSortEnabled == enabled) return
        sortType = newSortType
        isSortEnabled = enabled
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemMovieRandomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Movie) {
            binding.tvMovieTitle.text = item.title
            binding.tvMovieInfo.text = "${item.year} | ${item.type}"

            val rating = when (sortType) {
                "Rotten Tomatoes" -> {
                    binding.ivRatingIcon.setImageResource(R.drawable.ic_rt)
                    binding.ivRatingIcon.setColorFilter(0)
                    item.rottenTomatoesRating
                }
                "Metacritic" -> {
                    binding.ivRatingIcon.setImageResource(R.drawable.ic_meta)
                    binding.ivRatingIcon.setColorFilter(ContextCompat.getColor(itemView.context, R.color.white))
                    item.metacriticRating
                }
                else -> {
                    binding.ivRatingIcon.setImageResource(R.drawable.ic_imdb)
                    binding.ivRatingIcon.setColorFilter(0)
                    item.imdbRating
                }
            }
            binding.tvImdbRating.text = rating

            ImageLoader.load(item.poster, binding.ivMoviePoster, android.R.drawable.ic_menu_gallery)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}
