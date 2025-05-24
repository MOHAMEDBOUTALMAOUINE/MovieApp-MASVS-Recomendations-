package com.example.movieapp;

import android.util.Log;
import com.example.movieapp.security.SSLConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * ApiClient - Client pour les appels API
 * 
 * Fonctionnalités :
 * - Configuration du client Retrofit
 * - Gestion des appels API
 * - Configuration des intercepteurs
 * 
 * Technologies utilisées :
 * - Retrofit pour les appels API
 * - OkHttp pour le client HTTP
 * - Gson pour la conversion JSON
 */
public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;
    private static final ApiKey apiKey = new ApiKey();

    public static Retrofit getClient() {
        if (retrofit == null) {
            try {
                Log.d(TAG, "Initializing Retrofit client");
                
                // Configuration de base d'OkHttp avec timeouts
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS);

                // Application de la configuration SSL
                httpClient = SSLConfig.configureSSL(httpClient);
                
                // Construction du client OkHttp
                OkHttpClient client = httpClient.build();
                
                Log.d(TAG, "Creating Retrofit instance with base URL: " + BASE_URL);
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                
                Log.d(TAG, "Retrofit instance created successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error creating Retrofit instance", e);
                throw new RuntimeException("Failed to create Retrofit instance", e);
            }
        }
        return retrofit;
    }

    public static String getApiKey() {
        try {
            Log.d(TAG, "Getting API key");
            String key = apiKey.getApiKey();
            if (key == null || key.isEmpty()) {
                Log.e(TAG, "API key is null or empty");
                throw new IllegalStateException("API key is not available");
            }
            Log.d(TAG, "API key retrieved successfully");
            return key;
        } catch (Exception e) {
            Log.e(TAG, "Error getting API key", e);
            throw new RuntimeException("Failed to get API key", e);
        }
    }
} 