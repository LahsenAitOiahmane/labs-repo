package com.example.twoscreens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 1) Références vers les champs de saisie
    private EditText nom, email, phone, adresse, ville;

    // 2) Référence vers le bouton
    private Button btnEnvoyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 3) Cycle de vie : onCreate est appelé à la création de l'écran
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 4) Lie l'UI XML à cette Activity

        // 5) Récupérer les vues par leurs IDs (définis dans XML)
        nom     = findViewById(R.id.nom);
        email   = findViewById(R.id.email);
        phone   = findViewById(R.id.phone);
        adresse = findViewById(R.id.adresse);
        ville   = findViewById(R.id.ville);
        btnEnvoyer = findViewById(R.id.btnEnvoyer);

        // 6) Écouter le clic sur "Envoyer"
        btnEnvoyer.setOnClickListener(v -> {
            // 6.1) Lire le texte des champs
            String sNom     = nom.getText().toString().trim();
            String sEmail   = email.getText().toString().trim();
            String sPhone   = phone.getText().toString().trim();
            String sAdresse = adresse.getText().toString().trim();
            String sVille   = ville.getText().toString().trim();

            // 6.2) Validation ultra-simple : champs obligatoires
            if (sNom.isEmpty() || sEmail.isEmpty()) {
                Toast.makeText(this, "Nom et Email sont obligatoires.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bonus : Validation Email
            if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                Toast.makeText(this, "Format d'email invalide.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 6.3) Construire un Intent explicite vers l'écran 2
            Intent i = new Intent(MainActivity.this, Screen2Activity.class);

            // 6.4) Passer les données avec des « extras » (clé/valeur)
            i.putExtra("nom", sNom);
            i.putExtra("email", sEmail);
            i.putExtra("phone", sPhone);
            i.putExtra("adresse", sAdresse);
            i.putExtra("ville", sVille);

            // 6.5) Démarrer l'activité de récapitulatif
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bonus : Réinitialiser le formulaire au retour
        nom.setText("");
        email.setText("");
        phone.setText("");
        adresse.setText("");
        ville.setText("");
        nom.requestFocus(); // Remet le focus sur le premier champ
    }
}
