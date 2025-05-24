package com.example.movieapp.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SecureFavoritesManager {
    private static final String TAG = "SecureFavoritesManager";
    private static final String PREF_NAME = "secure_favorites";
    private static final String KEY_FAVORITES = "encrypted_favorites";

    private final SharedPreferences preferences;
    private final EncryptionManager encryptionManager;
    private final Gson gson;

    public SecureFavoritesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        encryptionManager = new EncryptionManager();
        gson = new Gson();
    }

    public void saveFavorites(List<Integer> favoriteIds) {
        try {
            String json = gson.toJson(favoriteIds);
            String encrypted = encryptionManager.encrypt(json);
            preferences.edit().putString(KEY_FAVORITES, encrypted).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving favorites", e);
        }
    }

    public List<Integer> getFavorites() {
        try {
            String encrypted = preferences.getString(KEY_FAVORITES, null);
            if (encrypted == null) {
                return new ArrayList<>();
            }

            String decrypted = encryptionManager.decrypt(encrypted);
            Type type = new TypeToken<List<Integer>>(){}.getType();
            return gson.fromJson(decrypted, type);
        } catch (Exception e) {
            Log.e(TAG, "Error getting favorites", e);
            return new ArrayList<>();
        }
    }

    public void addFavorite(int movieId) {
        List<Integer> favorites = getFavorites();
        if (!favorites.contains(movieId)) {
            favorites.add(movieId);
            saveFavorites(favorites);
        }
    }

    public void removeFavorite(int movieId) {
        List<Integer> favorites = getFavorites();
        if (favorites.remove(Integer.valueOf(movieId))) {
            saveFavorites(favorites);
        }
    }

    public boolean isFavorite(int movieId) {
        return getFavorites().contains(movieId);
    }
} 