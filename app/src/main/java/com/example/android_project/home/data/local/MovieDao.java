package com.example.android_project.home.data.local;

import androidx.room.*;
import com.example.android_project.home.data.model.Movie;
import com.example.android_project.home.data.model.FavoriteMovie;
import com.example.android_project.home.data.model.WatchlistMovie;
import com.example.android_project.home.data.model.RecentlyViewedMovie;
import java.util.List;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<Movie> movies);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Query("SELECT * FROM movies")
    List<Movie> getAllMovies();

    @Query("SELECT * FROM movies WHERE imdbId = :imdbId")
    Movie getMovieById(String imdbId);

    @Query("SELECT * FROM movies WHERE genre LIKE '%' || :genre || '%'")
    List<Movie> getMoviesByGenre(String genre);

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%'")
    List<Movie> searchMovies(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavorite(FavoriteMovie favorite);

    @Query("DELETE FROM favorite_movies WHERE imdbId = :imdbId")
    void deleteFavorite(String imdbId);

    @Query("SELECT * FROM favorite_movies")
    List<FavoriteMovie> getAllFavorites();

    @Query("DELETE FROM favorite_movies")
    void clearFavorites();

    @Query("SELECT movies.* FROM movies INNER JOIN favorite_movies ON movies.imdbId = favorite_movies.imdbId")
    List<Movie> getFavoriteMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWatchlist(WatchlistMovie watchlist);

    @Query("DELETE FROM watchlist_movies WHERE imdbId = :imdbId")
    void deleteWatchlist(String imdbId);

    @Query("DELETE FROM watchlist_movies")
    void clearWatchlist();

    @Query("SELECT * FROM watchlist_movies")
    List<WatchlistMovie> getAllWatchlist();

    @Query("SELECT movies.* FROM movies INNER JOIN watchlist_movies ON movies.imdbId = watchlist_movies.imdbId")
    List<Movie> getWatchlistMovies();
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE imdbId = :imdbId)")
    boolean isFavorite(String imdbId);

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE imdbId = :imdbId)")
    boolean isInWatchlist(String imdbId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecentlyViewed(RecentlyViewedMovie recent);

    @Query("SELECT movies.* FROM movies INNER JOIN recently_viewed ON movies.imdbId = recently_viewed.imdbId ORDER BY recently_viewed.timestamp DESC LIMIT 10")
    List<Movie> getRecentlyViewedMovies();

    @Query("DELETE FROM recently_viewed")
    void clearRecentlyViewed();
}
