package com.example.movieapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * FavoritesActivity - Activité de gestion des films favoris
 * 
 * Fonctionnalités :
 * - Affichage de la liste des films favoris
 * - Suppression des films des favoris
 * - Navigation vers les détails d'un film favori
 * - Persistance des favoris
 * 
 * Technologies utilisées :
 * - RecyclerView pour l'affichage de la liste
 * - SharedPreferences pour le stockage local
 * - Material Design pour l'interface
 */
public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> favoriteMovies;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mes Favoris");

        recyclerView = findViewById(R.id.favoritesRecyclerView);
        favoriteMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, favoriteMovies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(movieAdapter);

        sharedPreferences = getSharedPreferences("MovieApp", MODE_PRIVATE);
        loadFavoriteMovies();
    }

    private void loadFavoriteMovies() {
        String favorites = sharedPreferences.getString("favorites", "");
        if (favorites.isEmpty()) {
            Toast.makeText(this, "Aucun film en favoris", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] favoriteIds = favorites.split(",");
        for (String id : favoriteIds) {
            if (!id.isEmpty()) {
                loadMovieDetails(Integer.parseInt(id));
            }
        }
    }

    private void loadMovieDetails(int movieId) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Movie> call = apiInterface.getMovieDetails(movieId, ApiClient.getApiKey());

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteMovies.add(response.body());
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(FavoritesActivity.this, "Erreur de chargement des films favoris", Toast.LENGTH_SHORT).show();
            }
        });
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