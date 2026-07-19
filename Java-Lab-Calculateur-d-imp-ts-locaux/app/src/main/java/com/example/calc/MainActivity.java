package com.example.calc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText surfaceInput;
    private EditText piecesInput;
    private CheckBox piscineCheckbox;
    private TextView impotBaseResult;
    private TextView impotSupplementResult;
    private TextView impotTotalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceInput = findViewById(R.id.surfaceInput);
        piecesInput = findViewById(R.id.piecesInput);
        piscineCheckbox = findViewById(R.id.piscineCheckbox);
        impotBaseResult = findViewById(R.id.impotBaseResult);
        impotSupplementResult = findViewById(R.id.impotSupplementResult);
        impotTotalResult = findViewById(R.id.impotTotalResult);
        Button calculButton = findViewById(R.id.calculButton);

        calculButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculerImpots();
            }
        });
    }

    private void calculerImpots() {
        String surfaceStr = surfaceInput.getText().toString();
        String piecesStr = piecesInput.getText().toString();

        if (surfaceStr.isEmpty() || piecesStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Lecture des valeurs saisies
            double surface = Double.parseDouble(surfaceStr);
            int pieces = Integer.parseInt(piecesStr);
            boolean piscine = piscineCheckbox.isChecked();

            // Calcul des impôts
            double impotBase = surface * 2;
            double supplement = pieces * 50 + (piscine ? 100 : 0);
            double total = impotBase + supplement;

            // Affichage des résultats
            impotBaseResult.setText("Impôt de base : " + String.format("%.1f", impotBase));
            impotSupplementResult.setText("impôt supplémentaire : " + String.format("%.1f", supplement));
            impotTotalResult.setText("impôt Total : " + String.format("%.1f", total));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valeurs invalides", Toast.LENGTH_SHORT).show();
        }
    }
}
