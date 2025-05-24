package com.example.movieapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * DetailActivity - Activité d'affichage des détails d'un film
 * 
 * Fonctionnalités :
 * - Affichage des informations détaillées d'un film
 * - Lecture de la bande-annonce via YouTube
 * - Affichage des films similaires
 * - Gestion des genres du film
 * - Ajout aux favoris
 * 
 * Technologies utilisées :
 * - YouTube Player API pour les vidéos
 * - RecyclerView pour les films similaires
 * - Glide pour les images
 * - Material Design pour l'interface
 */
public class DetailActivity extends AppCompatActivity {
    private TextView titleTextView;
    private TextView overviewTextView;
    private TextView releaseDateTextView;
    private TextView ratingTextView;
    private Button favoriteButton;
    private Button planningButton;
    private Button similarButton;
    private TextView similarMoviesTitle;
    private RecyclerView similarMoviesRecyclerView;
    private SharedPreferences sharedPreferences;
    private Movie currentMovie;
    private boolean isFavorite = false;
    private List<Movie> similarMovies = new ArrayList<>();
    private SimilarMovieAdapter similarMoviesAdapter;
    private boolean similarMoviesLoaded = false;
    private YouTubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        titleTextView = findViewById(R.id.detailTitleTextView);
        overviewTextView = findViewById(R.id.detailOverviewTextView);
        releaseDateTextView = findViewById(R.id.detailReleaseDateTextView);
        ratingTextView = findViewById(R.id.detailRatingTextView);
        favoriteButton = findViewById(R.id.favoriteButton);
        planningButton = findViewById(R.id.planningButton);
        similarButton = findViewById(R.id.similarButton);
        similarMoviesTitle = findViewById(R.id.similarMoviesTitle);
        similarMoviesRecyclerView = findViewById(R.id.similarMoviesRecyclerView);

        similarMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        similarMoviesAdapter = new SimilarMovieAdapter(this, similarMovies);
        similarMoviesRecyclerView.setAdapter(similarMoviesAdapter);

        sharedPreferences = getSharedPreferences("MovieApp", MODE_PRIVATE);

        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId != -1) {
            loadMovieDetails(movieId);
            loadMovieVideos(movieId);
        }

        favoriteButton.setOnClickListener(v -> toggleFavorite());
        planningButton.setOnClickListener(v -> showDateTimePicker());
        similarButton.setOnClickListener(v -> toggleSimilarMovies());
    }

    private void loadMovieVideos(int movieId) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<VideoResponse> call = apiInterface.getMovieVideos(movieId, ApiClient.getApiKey());

        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Video> videos = response.body().getResults();
                    if (videos != null && !videos.isEmpty()) {
                        // Trouver la première bande-annonce
                        for (Video video : videos) {
                            if ("Trailer".equals(video.getType())) {
                                setupYouTubePlayer(video.getKey());
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Erreur de chargement de la bande-annonce", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupYouTubePlayer(String videoId) {
        getLifecycle().addObserver(youtubePlayerView);

        youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
    }

    private void loadMovieDetails(int movieId) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Movie> call = apiInterface.getMovieDetails(movieId, ApiClient.getApiKey());

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentMovie = response.body();
                    displayMovieDetails();
                    checkIfFavorite();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(DetailActivity.this, "Erreur de chargement des détails", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayMovieDetails() {
        titleTextView.setText(currentMovie.getTitle());
        overviewTextView.setText(currentMovie.getOverview());
        releaseDateTextView.setText(currentMovie.getReleaseDate());
        
        double rating = currentMovie.getVoteAverage();
        ratingTextView.setText(String.format(Locale.getDefault(), "%.1f", rating));
    }

    private void checkIfFavorite() {
        String favorites = sharedPreferences.getString("favorites", "");
        isFavorite = favorites.contains(String.valueOf(currentMovie.getId()));
        updateFavoriteButton();
    }

    private void toggleFavorite() {
        String favorites = sharedPreferences.getString("favorites", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isFavorite) {
            favorites = favorites.replace(String.valueOf(currentMovie.getId()) + ",", "");
            editor.putString("favorites", favorites);
            Toast.makeText(this, "Film retiré des favoris", Toast.LENGTH_SHORT).show();
        } else {
            favorites += currentMovie.getId() + ",";
            editor.putString("favorites", favorites);
            Toast.makeText(this, "Film ajouté aux favoris", Toast.LENGTH_SHORT).show();
        }

        editor.apply();
        isFavorite = !isFavorite;
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        favoriteButton.setText(isFavorite ? "Retirer des favoris" : "Ajouter aux favoris");
    }

    private void toggleSimilarMovies() {
        if (!similarMoviesLoaded) {
            loadSimilarMovies();
        } else {
            if (similarMoviesTitle.getVisibility() == View.VISIBLE) {
                similarMoviesTitle.setVisibility(View.GONE);
                similarMoviesRecyclerView.setVisibility(View.GONE);
                similarButton.setText("Films similaires");
            } else {
                similarMoviesTitle.setVisibility(View.VISIBLE);
                similarMoviesRecyclerView.setVisibility(View.VISIBLE);
                similarButton.setText("Masquer les films similaires");
            }
        }
    }

    private void loadSimilarMovies() {
        Toast.makeText(this, "Chargement des films similaires...", Toast.LENGTH_SHORT).show();
        
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiInterface.getSimilarMovies(currentMovie.getId(), ApiClient.getApiKey());

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    similarMovies.clear();
                    similarMovies.addAll(response.body().getResults());
                    similarMoviesAdapter.notifyDataSetChanged();
                    
                    similarMoviesLoaded = true;
                    similarMoviesTitle.setVisibility(View.VISIBLE);
                    similarMoviesRecyclerView.setVisibility(View.VISIBLE);
                    similarButton.setText("Masquer les films similaires");
                    
                    if (similarMovies.isEmpty()) {
                        Toast.makeText(DetailActivity.this, 
                            "Aucun film similaire trouvé", 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this, 
                    "Erreur de chargement des films similaires", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            R.style.CustomDatePickerDialog,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    R.style.CustomTimePickerDialog,
                    (view1, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        addToPlanning(calendar);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                );
                timePickerDialog.show();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void addToPlanning(Calendar dateTime) {
        Intent intent = new Intent(this, PlanningActivity.class);
        Gson gson = new Gson();
        String movieJson = gson.toJson(currentMovie);
        intent.putExtra("movie_json", movieJson);
        intent.putExtra("dateTime", dateTime.getTimeInMillis());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 