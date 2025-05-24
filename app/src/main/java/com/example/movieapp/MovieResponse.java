package com.example.movieapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * MovieResponse - Classe de réponse de l'API pour les films
 * 
 * Fonctionnalités :
 * - Stockage de la réponse de l'API
 * - Gestion de la pagination
 * - Sérialisation/désérialisation JSON
 * 
 * Technologies utilisées :
 * - Gson pour la sérialisation
 * - Annotations pour le mapping JSON
 */
public class MovieResponse {
    @SerializedName("page")
    private int page;

    @SerializedName("results")
    private List<Movie> results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public int getPage() {
        return page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
} 