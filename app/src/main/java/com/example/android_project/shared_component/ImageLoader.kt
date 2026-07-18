package com.example.android_project.shared_component

import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import com.example.android_project.R

object ImageLoader {

    fun load(url: String?, imageView: ImageView, placeholder: Int = R.drawable.bg_chip) {
        if (url.isNullOrEmpty() || url == "N/A") {
            imageView.setImageResource(placeholder)
            return
        }
        
        imageView.load(url) {
            crossfade(true)
            placeholder(placeholder)
            error(placeholder)
            // Coil handles disk and memory caching automatically.
            // These policies ensure we use cache when offline.
            diskCachePolicy(CachePolicy.ENABLED)
            memoryCachePolicy(CachePolicy.ENABLED)
        }
    }
}
