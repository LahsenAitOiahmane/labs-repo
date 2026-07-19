package com.example.lab23;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public native boolean isDebugDetected();
    public native String helloFromJNI();
    public native int factorial(int n);
    public native String reverseString(String s);
    public native int sumArray(int[] values);

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvHello = findViewById(R.id.tvHello);
        TextView tvFact = findViewById(R.id.tvFact);
        TextView tvReverse = findViewById(R.id.tvReverse);
        TextView tvArray = findViewById(R.id.tvArray);

        boolean suspicious = isDebugDetected();

        if (suspicious) {
            tvStatus.setText("⚠ Environnement suspect détecté");
            tvStatus.setTextColor(Color.parseColor("#EF4444"));

            tvHello.setText("Fonction native sensible désactivée");
            tvFact.setText("Calcul natif bloqué");
            tvReverse.setText("Inversion bloquée");
            tvArray.setText("Somme bloquée");
        } else {
            tvStatus.setText("✓ État sécurité : OK");
            tvStatus.setTextColor(Color.parseColor("#10B981"));

            tvHello.setText(helloFromJNI());

            int result = factorial(10);
            if (result >= 0) {
                tvFact.setText("Factoriel de 10 = " + result);
            } else {
                tvFact.setText("Erreur factoriel, code = " + result);
            }

            String reversed = reverseString("JNI is powerful!");
            tvReverse.setText("Texte inversé : " + reversed);

            int[] numbers = {10, 20, 30, 40, 50};
            int sum = sumArray(numbers);
            tvArray.setText("Somme du tableau = " + sum);
        }
    }
}
