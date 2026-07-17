package com.example.android_project.shared_component

import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object ImageLoader {

    private val client = OkHttpClient()

    fun load(url: String?, imageView: ImageView, placeholder: Int = 0) {
        if (url.isNullOrEmpty() || url == "N/A") {
            if (placeholder != 0) imageView.setImageResource(placeholder)
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        response.body?.byteStream()?.use {
                            BitmapFactory.decodeStream(it)
                        }
                    } else null
                } catch (_: Exception) {
                    null
                }
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            } else if (placeholder != 0) {
                imageView.setImageResource(placeholder)
            }
        }
    }
}
