package com.example.twoscreens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Screen2Activity extends AppCompatActivity {

    private TextView recap;
    private Button btnRetour, btnPartager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen2); // relie au XML du récap

        recap = findViewById(R.id.recap);
        btnRetour = findViewById(R.id.btnRetour);
        btnPartager = findViewById(R.id.btnPartager);

        // 1) Récupérer l'Intent qui a lancé cet écran
        Intent intent = getIntent();

        // 2) Extraire les données envoyées depuis MainActivity
        String nom     = intent.getStringExtra("nom");
        String email   = intent.getStringExtra("email");
        String phone   = intent.getStringExtra("phone");
        String adresse = intent.getStringExtra("adresse");
        String ville   = intent.getStringExtra("ville");

        // 3) Construire un texte formaté (affichage multi-lignes)
        String resume = "Nom : " + safe(nom) +
                        "\nEmail : " + safe(email) +
                        "\nPhone : " + safe(phone) +
                        "\nAdresse : " + safe(adresse) +
                        "\nVille : " + safe(ville);

        // 4) Afficher le récapitulatif
        recap.setText(resume);

        // 5) Bouton Retour : fermer cet écran et revenir au précédent
        btnRetour.setOnClickListener(v -> finish());

        // Bonus : Partager le récap
        btnPartager.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Récapitulatif de " + nom);
            shareIntent.putExtra(Intent.EXTRA_TEXT, resume);
            startActivity(Intent.createChooser(shareIntent, "Partager via"));
        });
    }

    // Petite aide : si une valeur est null/vides, retourner "—"
    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "—" : s.trim();
    }
}
