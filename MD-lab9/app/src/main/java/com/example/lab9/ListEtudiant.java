package com.example.lab9;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lab9.adapter.EtudiantAdapter;
import com.example.lab9.beans.Etudiant;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListEtudiant extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EtudiantAdapter adapter;
    private List<Etudiant> etudiantList;
    private RequestQueue requestQueue;
    private static final String loadUrl = "http://192.168.1.175/projet/ws/loadEtudiant.php";
    private static final String deleteUrl = "http://192.168.1.175/projet/ws/deleteEtudiant.php";

    @Override
    protected void onResume() {
        super.onResume();
        if (requestQueue != null) {
            loadEtudiants();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etudiant);

        recyclerView = findViewById(R.id.recyclerView);
        etudiantList = new ArrayList<>();
        
        adapter = new EtudiantAdapter(etudiantList, etudiant -> {
            showOptionsDialog(etudiant);
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);
        loadEtudiants();
    }

    private void showOptionsDialog(Etudiant etudiant) {
        String[] options = {"Modifier", "Supprimer", "Annuler"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options pour " + etudiant.getNom());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) { // Modifier
                android.content.Intent intent = new android.content.Intent(ListEtudiant.this, UpdateEtudiant.class);
                intent.putExtra("id", etudiant.getId());
                intent.putExtra("nom", etudiant.getNom());
                intent.putExtra("prenom", etudiant.getPrenom());
                intent.putExtra("ville", etudiant.getVille());
                intent.putExtra("sexe", etudiant.getSexe());
                startActivity(intent);
            } else if (which == 1) { // Supprimer
                confirmDelete(etudiant);
            }
        });
        builder.show();
    }

    private void confirmDelete(Etudiant etudiant) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment supprimer cet étudiant ?")
                .setPositiveButton("Oui", (dialog, which) -> deleteEtudiant(etudiant))
                .setNegativeButton("Non", null)
                .show();
    }

    private void deleteEtudiant(Etudiant etudiant) {
        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    Toast.makeText(this, "Supprimé avec succès", Toast.LENGTH_SHORT).show();
                    loadEtudiants(); // Recharger la liste
                },
                error -> Toast.makeText(this, "Erreur suppression", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiant.getId()));
                return params;
            }
        };
        requestQueue.add(request);
    }

    private void loadEtudiants() {
        StringRequest request = new StringRequest(Request.Method.GET, loadUrl,
                response -> {
                    Log.d("RESPONSE", response);
                    Type type = new TypeToken<Collection<Etudiant>>(){}.getType();
                    Collection<Etudiant> etudiants = new Gson().fromJson(response, type);
                    etudiantList.clear();
                    etudiantList.addAll(etudiants);
                    adapter.notifyDataSetChanged();
                },
                error -> Log.e("VOLLEY", "Erreur : " + error.getMessage()));
        requestQueue.add(request);
    }
}
