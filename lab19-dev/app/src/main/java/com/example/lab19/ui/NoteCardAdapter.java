package com.example.lab19.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab19.R;
import com.example.lab19.data.local.NoteEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * NoteCardAdapter — adapte la liste de NoteEntity pour l'affichage dans un RecyclerView.
 *
 * Fonctionnalités :
 *  - Affichage de chaque note dans une carte stylisée
 *  - Animation d'apparition (slide-in) à chaque nouvelle carte rendue
 *  - Clic simple : affichage du détail (géré par l'Activity)
 *  - Clic long : suppression (géré par l'Activity)
 *  - Alternance de couleur d'accent selon la position de la note
 */
public class NoteCardAdapter extends RecyclerView.Adapter<NoteCardAdapter.NoteViewHolder> {

    // Couleurs d'accent pour les indicateurs de position (hex, alternées)
    private static final int[] ACCENT_COLORS = {
            0xFF58A6FF, // cyan
            0xFFA371F7, // violet
            0xFFFF7B7B, // rose
            0xFFF0B429, // amber
            0xFF3FB950  // emerald
    };

    private List<NoteEntity> noteList = new ArrayList<>();

    /** Callback pour clic simple */
    private NoteClickListener clickListener;

    /** Callback pour clic long */
    private NoteLongClickListener longClickListener;

    // --- Interfaces pour les callbacks ---

    public interface NoteClickListener {
        void onNoteClicked(NoteEntity note);
    }

    public interface NoteLongClickListener {
        void onNoteLongClicked(NoteEntity note);
    }

    // --- Setters des callbacks ---

    public void setOnNoteClickListener(NoteClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnNoteLongClickListener(NoteLongClickListener listener) {
        this.longClickListener = listener;
    }

    /**
     * Met à jour la liste des notes et demande un rafraîchissement de la vue.
     */
    public void refreshNotes(List<NoteEntity> updatedList) {
        this.noteList = (updatedList != null) ? updatedList : new ArrayList<>();
        notifyDataSetChanged();
    }

    // --- Méthodes RecyclerView.Adapter ---

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_card, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteEntity currentNote = noteList.get(position);

        holder.tvTitle.setText(currentNote.getNoteTitle());
        holder.tvDescription.setText(currentNote.getNoteContent());

        // Alternance de la couleur de l'indicateur selon la position
        int accentColor = ACCENT_COLORS[position % ACCENT_COLORS.length];
        holder.viewAccentDot.setBackgroundColor(accentColor);

        // Animation slide-in pour chaque carte
        Context ctx = holder.itemView.getContext();
        Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_up);
        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // --- ViewHolder ---

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvDescription;
        private final View viewAccentDot;
        private final CardView cardNote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvDescription = itemView.findViewById(R.id.tv_note_description);
            viewAccentDot = itemView.findViewById(R.id.view_accent_dot);
            cardNote = itemView.findViewById(R.id.card_note);

            // Clic simple → notifier l'Activity
            cardNote.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (clickListener != null && pos != RecyclerView.NO_POSITION) {
                    clickListener.onNoteClicked(noteList.get(pos));
                }
            });

            // Clic long → déclencher la suppression dans l'Activity
            cardNote.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (longClickListener != null && pos != RecyclerView.NO_POSITION) {
                    longClickListener.onNoteLongClicked(noteList.get(pos));
                    return true;
                }
                return false;
            });
        }
    }
}
