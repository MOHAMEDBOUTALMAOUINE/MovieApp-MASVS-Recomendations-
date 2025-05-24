package com.example.movieapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

/**
 * GenreAdapter - Adaptateur pour l'affichage des genres de films
 * 
 * Fonctionnalités :
 * - Affichage de la liste des genres
 * - Mise en forme des genres
 * - Gestion du style des genres
 * 
 * Technologies utilisées :
 * - RecyclerView.Adapter
 * - ViewHolder pattern
 * - Material Design pour le style
 */
public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private List<MovieGenre> genres;
    private OnGenreClickListener listener;
    private int selectedPosition = -1;

    public interface OnGenreClickListener {
        void onGenreClick(MovieGenre genre);
    }

    public GenreAdapter(OnGenreClickListener listener) {
        this.genres = Arrays.asList(MovieGenre.GENRES);
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        MovieGenre genre = genres.get(position);
        holder.textView.setText(genre.getName());
        
        // Mise en forme du texte
        holder.textView.setTextSize(16);
        holder.textView.setPadding(32, 16, 32, 16);
        
        // Gestion de la sélection
        holder.itemView.setBackgroundResource(position == selectedPosition ? 
            android.R.color.holo_blue_light : android.R.color.transparent);
        
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onGenreClick(genre);
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
} 