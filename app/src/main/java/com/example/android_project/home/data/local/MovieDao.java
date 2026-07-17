package com.example.android_project.home.data.local;

import androidx.room.*;
import com.example.android_project.home.data.model.Movie;
import com.example.android_project.home.data.model.FavouriteMovie;
import com.example.android_project.home.data.model.WatchlistMovie;
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
    void insertFavourite(FavouriteMovie favourite);

    @Query("DELETE FROM favourite_movies WHERE imdbId = :imdbId")
    void deleteFavourite(String imdbId);

    @Query("SELECT * FROM favourite_movies")
    List<FavouriteMovie> getAllFavourites();

    @Query("SELECT movies.* FROM movies INNER JOIN favourite_movies ON movies.imdbId = favourite_movies.imdbId")
    List<Movie> getFavouriteMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWatchlist(WatchlistMovie watchlist);

    @Query("DELETE FROM watchlist_movies WHERE imdbId = :imdbId")
    void deleteWatchlist(String imdbId);

    @Query("SELECT * FROM watchlist_movies")
    List<WatchlistMovie> getAllWatchlist();

    @Query("SELECT movies.* FROM movies INNER JOIN watchlist_movies ON movies.imdbId = watchlist_movies.imdbId")
    List<Movie> getWatchlistMovies();
    
    @Query("SELECT EXISTS(SELECT 1 FROM favourite_movies WHERE imdbId = :imdbId)")
    boolean isFavourite(String imdbId);

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_movies WHERE imdbId = :imdbId)")
    boolean isInWatchlist(String imdbId);
}
