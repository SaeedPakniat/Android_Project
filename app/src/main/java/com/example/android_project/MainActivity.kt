package com.example.android_project

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android_project.databinding.ActivityMainBinding
import com.example.android_project.favorites.presentation.ui.FavoritesFragment
import com.example.android_project.home.presentation.ui.HomeFragment
import com.example.android_project.watchlist.presentation.ui.WatchlistFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            updateBottomNav(R.id.btn_nav_home)
        }

        binding.btnNavHome.setOnClickListener {
            loadFragment(HomeFragment())
            updateBottomNav(R.id.btn_nav_home)
        }

        binding.btnNavFavorites.setOnClickListener {
            loadFragment(FavoritesFragment())
            updateBottomNav(R.id.btn_nav_favorites)
        }

        binding.btnNavWatchlist.setOnClickListener {
            loadFragment(WatchlistFragment())
            updateBottomNav(R.id.btn_nav_watchlist)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateBottomNav(selectedId: Int) {
        val items = listOf(
            Triple(R.id.btn_nav_home, R.id.iv_nav_home, R.id.tv_nav_home),
            Triple(R.id.btn_nav_favorites, R.id.iv_nav_favorites, R.id.tv_nav_favorites),
            Triple(R.id.btn_nav_watchlist, R.id.iv_nav_watchlist, R.id.tv_nav_watchlist)
        )

        for ((containerId, iconId, textId) in items) {
            val icon = findViewById<ImageView>(iconId)
            val text = findViewById<TextView>(textId)

            if (containerId == selectedId) {
                icon.setColorFilter(ContextCompat.getColor(this, R.color.accent_yellow))
                icon.setBackgroundResource(R.drawable.bg_nav_selected)
                text.setTextColor(ContextCompat.getColor(this, R.color.accent_yellow))
            } else {
                icon.setColorFilter(ContextCompat.getColor(this, R.color.text_gray))
                icon.setBackgroundResource(0)
                text.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
            }
        }
    }
}
