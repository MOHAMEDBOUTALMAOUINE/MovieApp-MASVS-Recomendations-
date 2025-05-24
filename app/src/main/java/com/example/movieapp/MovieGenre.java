package com.example.movieapp;

/**
 * MovieGenre - Classe modèle représentant un genre de film
 * 
 * Fonctionnalités :
 * - Stockage des informations d'un genre
 * - Sérialisation/désérialisation JSON
 * - Gestion des données du genre
 * 
 * Technologies utilisées :
 * - Gson pour la sérialisation
 * - Annotations pour le mapping JSON
 */
public class MovieGenre {
    private int id;
    private String name;

    public MovieGenre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static final MovieGenre[] GENRES = {
        new MovieGenre(28, "Action"),
        new MovieGenre(12, "Aventure"),
        new MovieGenre(16, "Animation"),
        new MovieGenre(35, "Comédie"),
        new MovieGenre(80, "Crime"),
        new MovieGenre(99, "Documentaire"),
        new MovieGenre(18, "Drame"),
        new MovieGenre(10751, "Familial"),
        new MovieGenre(14, "Fantastique"),
        new MovieGenre(36, "Histoire"),
        new MovieGenre(27, "Horreur"),
        new MovieGenre(10402, "Musique"),
        new MovieGenre(9648, "Mystère"),
        new MovieGenre(10749, "Romance"),
        new MovieGenre(878, "Science-Fiction"),
        new MovieGenre(10770, "Téléfilm"),
        new MovieGenre(53, "Thriller"),
        new MovieGenre(10752, "Guerre"),
        new MovieGenre(37, "Western")
    };
} 