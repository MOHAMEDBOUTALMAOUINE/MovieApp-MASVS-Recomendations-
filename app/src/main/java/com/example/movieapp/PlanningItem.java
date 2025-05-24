package com.example.movieapp;

import java.util.Calendar;

/**
 * PlanningItem - Classe modèle représentant un film planifié
 * 
 * Fonctionnalités :
 * - Stockage des informations d'un film planifié
 * - Gestion de la date de visionnage
 * - Sérialisation/désérialisation JSON
 * 
 * Technologies utilisées :
 * - Gson pour la sérialisation
 * - Annotations pour le mapping JSON
 */
public class PlanningItem {
    private Movie movie;
    private Calendar dateTime;

    public PlanningItem(Movie movie, Calendar dateTime) {
        this.movie = movie;
        this.dateTime = dateTime;
    }

    public Movie getMovie() {
        return movie;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }
} 