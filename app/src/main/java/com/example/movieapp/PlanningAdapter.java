package com.example.movieapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieapp.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * PlanningAdapter - Adaptateur pour l'affichage des films planifiés
 * 
 * Fonctionnalités :
 * - Affichage de la liste des films planifiés
 * - Gestion des dates de visionnage
 * - Suppression des films planifiés
 * - Navigation vers les détails
 * 
 * Technologies utilisées :
 * - RecyclerView.Adapter
 * - ViewHolder pattern
 * - Material Design pour l'interface
 */
public class PlanningAdapter extends RecyclerView.Adapter<PlanningAdapter.PlanningViewHolder> {
    private List<PlanningItem> planningList;
    private PlanningActivity activity;

    public PlanningAdapter(PlanningActivity activity, List<PlanningItem> planningList) {
        this.activity = activity;
        this.planningList = planningList;
    }

    @NonNull
    @Override
    public PlanningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planning, parent, false);
        return new PlanningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanningViewHolder holder, int position) {
        PlanningItem item = planningList.get(position);
        Movie movie = item.getMovie();

        holder.titleTextView.setText(movie.getTitle());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.dateTextView.setText(dateFormat.format(item.getDateTime().getTime()));

        Glide.with(holder.itemView.getContext())
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(holder.posterImageView);

        holder.removeButton.setOnClickListener(v -> {
            activity.removeFromPlanning(item);
        });
    }

    @Override
    public int getItemCount() {
        return planningList.size();
    }

    static class PlanningViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;
        TextView dateTextView;
        ImageButton removeButton;

        PlanningViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
} 