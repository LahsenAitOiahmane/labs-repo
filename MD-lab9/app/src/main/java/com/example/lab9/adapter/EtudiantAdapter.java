package com.example.lab9.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab9.R;
import com.example.lab9.beans.Etudiant;

import java.util.List;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {

    private List<Etudiant> etudiants;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Etudiant etudiant);
    }

    public EtudiantAdapter(List<Etudiant> etudiants, OnItemClickListener listener) {
        this.etudiants = etudiants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_etudiant, parent, false);
        return new EtudiantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiants.get(position);
        holder.nomPrenom.setText(etudiant.getNom() + " " + etudiant.getPrenom());
        holder.villeSexe.setText(etudiant.getVille() + " (" + etudiant.getSexe() + ")");
        holder.idEtudiant.setText(String.valueOf(etudiant.getId()));
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(etudiant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return etudiants.size();
    }

    public static class EtudiantViewHolder extends RecyclerView.ViewHolder {
        TextView nomPrenom, villeSexe, idEtudiant;

        public EtudiantViewHolder(@NonNull View itemView) {
            super(itemView);
            nomPrenom = itemView.findViewById(R.id.nomPrenom);
            villeSexe = itemView.findViewById(R.id.villeSexe);
            idEtudiant = itemView.findViewById(R.id.idEtudiant);
        }
    }
}
