package com.example.movieapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Movie - Classe modèle représentant un film
 * 
 * Fonctionnalités :
 * - Stockage des informations d'un film
 * - Sérialisation/désérialisation JSON
 * - Gestion des données du film
 * 
 * Technologies utilisées :
 * - Gson pour la sérialisation
 * - Annotations pour le mapping JSON
 */
public class Movie {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("videos")
    private VideoResponse videos;

    public Movie(int id, String title, String overview, String posterPath, String releaseDate, double voteAverage) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getReleaseDate() { return releaseDate; }
    public double getVoteAverage() { return voteAverage; }
    public VideoResponse getVideos() { return videos; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setOverview(String overview) { this.overview = overview; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public void setVoteAverage(double voteAverage) { this.voteAverage = voteAverage; }
    public void setVideos(VideoResponse videos) { this.videos = videos; }
}

class VideoResponse {
    @SerializedName("results")
    private List<Video> results;

    public List<Video> getResults() { return results; }
}

class Video {
    @SerializedName("key")
    private String key;

    @SerializedName("type")
    private String type;

    public String getKey() { return key; }
    public String getType() { return type; }
} 