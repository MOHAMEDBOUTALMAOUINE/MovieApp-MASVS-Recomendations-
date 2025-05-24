package com.example.movieapp;

import android.util.Log;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * ApiInterface - Interface de définition des appels API
 * 
 * Fonctionnalités :
 * - Définition des endpoints de l'API
 * - Gestion des requêtes HTTP
 * - Conversion des réponses JSON
 * 
 * Technologies utilisées :
 * - Retrofit pour les appels API
 * - Gson pour la conversion JSON
 * - Annotations Retrofit
 */
public interface ApiInterface {
    String TAG = "ApiInterface";

    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("discover/movie")
    Call<MovieResponse> getMoviesByGenre(@Query("api_key") String apiKey, @Query("with_genres") int genreId);

    @GET("movie/{movie_id}/similar")
    Call<MovieResponse> getSimilarMovies(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideos(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MovieResponse> searchMovies(@Query("api_key") String apiKey, @Query("query") String query);
} 