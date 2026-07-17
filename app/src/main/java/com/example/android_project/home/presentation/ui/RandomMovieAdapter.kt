package com.example.android_project.home.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_project.databinding.ItemMovieRandomBinding
import com.example.android_project.home.data.model.Movie
import com.example.android_project.shared_component.ImageLoader

class RandomMovieAdapter(
    private val items: MutableList<Movie>,
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

    fun updateMovies(newItems: List<Movie>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemMovieRandomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Movie) {
            binding.tvMovieTitle.text = item.title
            binding.tvMovieInfo.text = "${item.year} | ${item.type}"

            ImageLoader.load(item.poster, binding.ivMoviePoster, android.R.drawable.ic_menu_gallery)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}
