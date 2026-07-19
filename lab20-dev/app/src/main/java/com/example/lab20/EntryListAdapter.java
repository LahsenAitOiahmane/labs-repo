package com.example.lab20;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * PhoneVault — Adapter RecyclerView pour la liste des contacts.
 *
 * Chaque carte affiche :
 *  - un avatar circulaire coloré avec l'initiale du nom ;
 *  - le nom complet du contact ;
 *  - le numéro de téléphone.
 */
public class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.EntryViewHolder> {

    /** Palette de couleurs utilisée en cycle pour les avatars */
    private static final int[] AVATAR_COLORS = {
            R.color.pv_avatar_1,
            R.color.pv_avatar_2,
            R.color.pv_avatar_3,
            R.color.pv_avatar_4,
            R.color.pv_avatar_5
    };

    private List<PhoneEntry> dataset;
    private final Context    context;

    public EntryListAdapter(Context context) {
        this.context = context;
        this.dataset = new ArrayList<>();
    }

    // ─── Cycle de vie du RecyclerView ───────────────────────────

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_card, parent, false);
        return new EntryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        PhoneEntry entry = dataset.get(position);

        // Nom & numéro
        holder.tvName.setText(entry.getDisplayName());
        holder.tvPhone.setText(entry.getPhoneNumber());

        // Avatar : initiale + couleur cyclique
        holder.tvAvatar.setText(entry.getInitial());
        int colorRes = AVATAR_COLORS[position % AVATAR_COLORS.length];
        setAvatarColor(holder.tvAvatar, ContextCompat.getColor(context, colorRes));

        // Badge d'origine (visible seulement si origin est défini)
        if (entry.getOrigin() != null && !entry.getOrigin().isEmpty()) {
            holder.tvOrigin.setText(entry.getOrigin());
            holder.tvOrigin.setVisibility(View.VISIBLE);
        } else {
            holder.tvOrigin.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.size();
    }

    // ─── Méthode publique pour rafraîchir les données ───────────

    /**
     * Remplace les données actuelles et notifie le RecyclerView.
     */
    public void refreshData(List<PhoneEntry> freshData) {
        this.dataset = (freshData != null) ? freshData : new ArrayList<>();
        notifyDataSetChanged();
    }

    /** Retourne le nombre réel d'entrées affichées. */
    public int getCount() {
        return dataset.size();
    }

    // ─── Utilitaire pour colorier l'avatar ──────────────────────

    private void setAvatarColor(TextView tv, int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(color);
        tv.setBackground(bg);
    }

    // ─── ViewHolder ─────────────────────────────────────────────

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        final TextView tvAvatar;
        final TextView tvName;
        final TextView tvPhone;
        final TextView tvOrigin;

        EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar_initial);
            tvName   = itemView.findViewById(R.id.tv_entry_name);
            tvPhone  = itemView.findViewById(R.id.tv_entry_phone);
            tvOrigin = itemView.findViewById(R.id.tv_entry_origin);
        }
    }
}
