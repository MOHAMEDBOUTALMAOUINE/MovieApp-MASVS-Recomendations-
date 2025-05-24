package com.example.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.pm.ApplicationInfo;
import java.io.File;
import com.example.movieapp.security.DebugProtection;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.security.SecurityManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity - Activité principale de l'application
 * 
 * Fonctionnalités :
 * - Affichage de la liste des films populaires
 * - Recherche de films
 * - Navigation vers les détails d'un film
 * - Gestion des favoris
 * 
 * Technologies utilisées :
 * - RecyclerView pour l'affichage de la liste
 * - Retrofit pour les appels API
 * - Glide pour le chargement des images
 * - Material Design pour l'interface
 * - Navigation entre activités
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private SharedPreferences sharedPreferences;
    private SearchView searchView;
    private ChipGroup filterChipGroup;
    private int currentGenreId = -1;
    private static final String THEME_PREF = "theme_preference";
    private static final String DARK_THEME = "dark_theme";
    private MaterialCardView searchCard;
    private boolean isSearchVisible = false;
    private static final String TAG = "MainActivity";

    private boolean isProductionEnvironment() {
        return (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0;
    }

    private void showProductionEnvironmentError() {
        new AlertDialog.Builder(this)
            .setTitle("Environnement Non Autorisé")
            .setMessage("Cette application ne peut être exécutée qu'en environnement de développement. Veuillez utiliser la version de développement.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK", (dialog, which) -> {
                finishAffinity(); // Force la fermeture de l'application
                System.exit(0); // Termine le processus
            })
            .setCancelable(false)
            .show();
    }

    private boolean isDeviceRooted() {
        // Vérifier les chemins communs pour les fichiers root
        String[] rootPaths = {
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        };

        for (String path : rootPaths) {
            if (new File(path).exists()) {
                return true;
            }
        }

        // Vérifier si on peut exécuter la commande 'su'
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"which", "su"});
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void showRootedDeviceError() {
        new AlertDialog.Builder(this)
            .setTitle("Appareil Rooté Détecté")
            .setMessage("Cette application ne peut pas être exécutée sur un appareil rooté pour des raisons de sécurité.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("OK", (dialog, which) -> {
                finishAffinity();
                System.exit(0);
            })
            .setCancelable(false)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isProductionEnvironment()) {
            showProductionEnvironmentError();
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Vérifier le debugging
        DebugProtection.checkDebugging();
        
        // Vérifier si l'appareil est rooté
        if (isDeviceRooted()) {
            showRootedDeviceError();
            return;
        }
        
        // Vérifier la sécurité de l'appareil
        if (!SecurityManager.isDeviceSecure(this)) {
            Log.w(TAG, "Device security check failed");
            // Afficher un message à l'utilisateur
            Toast.makeText(this, "Attention: L'appareil pourrait ne pas être sécurisé", Toast.LENGTH_LONG).show();
        }
        
        // Appliquer le thème sauvegardé
        sharedPreferences = getSharedPreferences("MovieApp", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean(DARK_THEME, false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        filterChipGroup = findViewById(R.id.filterChipGroup);
        searchCard = findViewById(R.id.searchCard);
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList);
        
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(movieAdapter);
        
        setupSearchView();
        setupFilterChips();
        
        // Restaurer le dernier genre sélectionné si nécessaire
        currentGenreId = sharedPreferences.getInt("last_genre_id", -1);
        if (currentGenreId != -1) {
            loadMoviesByGenre(currentGenreId);
        } else {
            loadPopularMovies();
        }
    }

    private void setupFilterChips() {
        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Ignorer les changements de sélection si l'activité est en train d'être recréée
            if (isFinishing() || isDestroyed()) {
                return;
            }

            if (checkedId == R.id.chipPopular) {
                currentGenreId = -1;
                loadPopularMovies();
            } else if (checkedId == R.id.chipTopRated) {
                currentGenreId = -1;
                loadTopRatedMovies();
            } else if (checkedId == R.id.chipNew) {
                currentGenreId = -1;
                loadNowPlayingMovies();
            } else if (checkedId == R.id.chipGenres) {
                showGenresDialog();
                // Réinitialiser la sélection du chip Genres immédiatement
                group.clearCheck();
            }
        });

        // Sélectionner le chip "Populaires" par défaut
        Chip popularChip = findViewById(R.id.chipPopular);
        if (popularChip != null) {
            popularChip.setChecked(true);
        }
    }

    private void showGenresDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_genres, null);
        RecyclerView genresRecyclerView = dialogView.findViewById(R.id.genresRecyclerView);
        genresRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        GenreAdapter genreAdapter = new GenreAdapter(genre -> {
            currentGenreId = genre.getId();
            loadMoviesByGenre(currentGenreId);
            dialog.dismiss();
            
            // Mettre à jour le texte du chip Genres
            Chip genresChip = findViewById(R.id.chipGenres);
            if (genresChip != null) {
                genresChip.setText(genre.getName());
            }
        });

        genresRecyclerView.setAdapter(genreAdapter);
        dialog.show();
    }

    private void loadTopRatedMovies() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiInterface.getTopRatedMovies(ApiClient.getApiKey());
        
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur de chargement des films", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNowPlayingMovies() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiInterface.getNowPlayingMovies(ApiClient.getApiKey());
        
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur de chargement des films", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoviesByGenre(int genreId) {
        // Afficher un indicateur de chargement
        Toast.makeText(this, "Chargement des films...", Toast.LENGTH_SHORT).show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiInterface.getMoviesByGenre(ApiClient.getApiKey(), genreId);
        
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                    
                    if (movieList.isEmpty()) {
                        Toast.makeText(MainActivity.this, 
                            "Aucun film trouvé dans cette catégorie", 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, 
                    "Erreur de chargement des films", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMovies(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    if (currentGenreId != -1) {
                        loadMoviesByGenre(currentGenreId);
                    } else {
                        loadPopularMovies();
                    }
                }
                return true;
            }
        });
    }

    private void searchMovies(String query) {
        if (query.isEmpty()) {
            if (currentGenreId != -1) {
                loadMoviesByGenre(currentGenreId);
            } else {
                loadPopularMovies();
            }
            return;
        }

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiInterface.searchMovies(ApiClient.getApiKey(), query);
        
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movieList.clear();
                    movieList.addAll(response.body().getResults());
                    movieAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur de recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPopularMovies() {
        Log.d(TAG, "Starting to load popular movies");
        try {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            String apiKey = ApiClient.getApiKey();
            Log.d(TAG, "API Key retrieved, length: " + (apiKey != null ? apiKey.length() : "null"));
            
            Call<MovieResponse> call = apiInterface.getPopularMovies(apiKey);
            
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    Log.d(TAG, "Received response from API. Success: " + response.isSuccessful());
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Number of movies received: " + response.body().getResults().size());
                        movieList.clear();
                        movieList.addAll(response.body().getResults());
                        movieAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Movies loaded successfully");
                    } else {
                        String errorMessage = "Erreur de chargement des films: " + response.code();
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += " - " + response.errorBody().string();
                            } catch (IOException e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                        }
                        Log.e(TAG, errorMessage);
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    String errorMessage = "Erreur de connexion: " + t.getMessage();
                    Log.e(TAG, errorMessage, t);
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            String errorMessage = "Erreur lors de l'initialisation: " + e.getMessage();
            Log.e(TAG, errorMessage, e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_toggle_theme) {
            toggleTheme();
            return true;
        } else if (id == R.id.action_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        } else if (id == R.id.action_search) {
            toggleSearchVisibility();
            return true;
        } else if (id == R.id.action_planning) {
            startActivity(new Intent(this, PlanningActivity.class));
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        // Sauvegarder l'état actuel avant le changement de thème
        int currentGenreIdTemp = currentGenreId;
        
        boolean isDarkTheme = sharedPreferences.getBoolean(DARK_THEME, false);
        isDarkTheme = !isDarkTheme;
        sharedPreferences.edit().putBoolean(DARK_THEME, isDarkTheme).apply();
        
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        
        // Réinitialiser la sélection des genres
        Chip popularChip = findViewById(R.id.chipPopular);
        if (popularChip != null) {
            popularChip.setChecked(true);
        }
        
        // Sauvegarder l'état pour le restaurer après la recréation
        sharedPreferences.edit().putInt("last_genre_id", currentGenreIdTemp).apply();
        
        recreate();
    }

    private void toggleSearchVisibility() {
        isSearchVisible = !isSearchVisible;
        searchCard.setVisibility(isSearchVisible ? View.VISIBLE : View.GONE);
        if (isSearchVisible) {
            searchView.requestFocus();
        } else {
            searchView.setQuery("", false);
            if (currentGenreId != -1) {
                loadMoviesByGenre(currentGenreId);
            } else {
                loadPopularMovies();
            }
        }
    }
} 