package com.example.movieapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

/**
 * PlanningNotificationReceiver - Gestionnaire des notifications de planification
 * 
 * Fonctionnalités :
 * - Réception des alarmes de planification
 * - Création et affichage des notifications
 * - Gestion des rappels de films
 * 
 * Technologies utilisées :
 * - BroadcastReceiver pour la réception des alarmes
 * - NotificationManager pour l'affichage des notifications
 * - AlarmManager pour la planification
 */
public class PlanningNotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "movie_planning_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String movieTitle = intent.getStringExtra("movie_title");
        int movieId = intent.getIntExtra("movie_id", 0);

        Intent movieIntent = new Intent(context, DetailActivity.class);
        movieIntent.putExtra("movie_id", movieId);
        movieIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            movieId,
            movieIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle("C'est l'heure de regarder un film !")
            .setContentText("Il est temps de regarder : " + movieTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(movieId, notification);
    }
} 