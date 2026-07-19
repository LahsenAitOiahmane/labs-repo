package com.example.lab9;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class UpdateEtudiant extends AppCompatActivity {

    private EditText nom, prenom;
    private Spinner ville;
    private RadioButton m, f;
    private Button update;
    private RequestQueue requestQueue;
    private int etudiantId;

    private static final String updateUrl = "http://192.168.1.175/projet/ws/updateEtudiant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_etudiant);

        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        ville = findViewById(R.id.ville);
        m = findViewById(R.id.m);
        f = findViewById(R.id.f);
        update = findViewById(R.id.update);

        requestQueue = Volley.newRequestQueue(this);

        etudiantId = getIntent().getIntExtra("id", -1);
        nom.setText(getIntent().getStringExtra("nom"));
        prenom.setText(getIntent().getStringExtra("prenom"));
        
        String sexeIntent = getIntent().getStringExtra("sexe");
        if ("femme".equalsIgnoreCase(sexeIntent)) f.setChecked(true);
        else m.setChecked(true);

        String villeIntent = getIntent().getStringExtra("ville");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.villes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ville.setAdapter(adapter);
        if (villeIntent != null) {
            int spinnerPosition = adapter.getPosition(villeIntent);
            ville.setSelection(spinnerPosition);
        }

        update.setOnClickListener(v -> updateEtudiant());
    }

    private void updateEtudiant() {
        StringRequest request = new StringRequest(Request.Method.POST, updateUrl,
                response -> {
                    Toast.makeText(UpdateEtudiant.this, "Étudiant modifié avec succès", Toast.LENGTH_SHORT).show();
                    finish(); 
                },
                error -> Log.e("VOLLEY", "Erreur : " + (error.getMessage() != null ? error.getMessage() : "Unknown error"))) {
            @Override
            protected Map<String, String> getParams() {
                String sexe = m.isChecked() ? "homme" : "femme";
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(etudiantId));
                params.put("nom", nom.getText().toString());
                params.put("prenom", prenom.getText().toString());
                params.put("ville", ville.getSelectedItem().toString());
                params.put("sexe", sexe);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
