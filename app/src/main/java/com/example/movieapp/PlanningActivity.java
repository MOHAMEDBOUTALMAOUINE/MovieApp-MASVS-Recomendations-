package com.example.movieapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * PlanningActivity - Activité de planification des films à regarder
 * 
 * Fonctionnalités :
 * - Planification des films à regarder
 * - Gestion du calendrier de visionnage
 * - Notifications de rappel
 * - Organisation des films par date
 * 
 * Technologies utilisées :
 * - AlarmManager pour les notifications
 * - RecyclerView pour l'affichage de la liste
 * - Material Design pour l'interface
 * - Calendar pour la gestion des dates
 */
public class PlanningActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlanningAdapter planningAdapter;
    private List<PlanningItem> planningList;
    private static final String PLANNING_FILE = "planning.json";
    private static final String CHANNEL_ID = "movie_planning_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mon Planning");

        recyclerView = findViewById(R.id.planningRecyclerView);
        planningList = loadPlanning();
        planningAdapter = new PlanningAdapter(this, planningList);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(planningAdapter);
        
        createNotificationChannel();

        // Vérifier si nous recevons un nouveau film à ajouter
        if (getIntent().hasExtra("movie_json") && getIntent().hasExtra("dateTime")) {
            String movieJson = getIntent().getStringExtra("movie_json");
            long dateTimeMillis = getIntent().getLongExtra("dateTime", 0);
            
            Gson gson = new Gson();
            Movie movie = gson.fromJson(movieJson, Movie.class);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateTimeMillis);
            
            addToPlanning(movie, calendar);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Planning de visionnage";
            String description = "Notifications pour les films planifiés";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void addToPlanning(Movie movie, Calendar dateTime) {
        PlanningItem item = new PlanningItem(movie, dateTime);
        planningList.add(item);
        savePlanning();
        planningAdapter.notifyDataSetChanged();
        scheduleNotification(item);
    }

    private void scheduleNotification(PlanningItem item) {
        Intent intent = new Intent(this, PlanningNotificationReceiver.class);
        intent.putExtra("movie_title", item.getMovie().getTitle());
        intent.putExtra("movie_id", item.getMovie().getId());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this,
            item.getMovie().getId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        
        // Vérifier si la date est dans le futur
        if (item.getDateTime().getTimeInMillis() > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 
                    item.getDateTime().getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, 
                    item.getDateTime().getTimeInMillis(), pendingIntent);
            }
        }
    }

    private void savePlanning() {
        Gson gson = new Gson();
        String json = gson.toJson(planningList);
        
        try (FileWriter writer = new FileWriter(new File(getFilesDir(), PLANNING_FILE))) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<PlanningItem> loadPlanning() {
        File file = new File(getFilesDir(), PLANNING_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<PlanningItem>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void removeFromPlanning(PlanningItem item) {
        planningList.remove(item);
        savePlanning();
        planningAdapter.notifyDataSetChanged();
        
        // Annuler la notification
        Intent intent = new Intent(this, PlanningNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this,
            item.getMovie().getId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
} 