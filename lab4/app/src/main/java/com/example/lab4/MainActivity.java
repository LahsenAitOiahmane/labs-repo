package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btn1, btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = findViewById(R.id.btnFragment1);
        btn2 = findViewById(R.id.btnFragment2);

        // Afficher le premier fragment au démarrage
        if (savedInstanceState == null) {
            replaceFragment(new FragmentOne(), false);
        }

        // Clic sur bouton 1
        btn1.setOnClickListener(v -> replaceFragment(new FragmentOne(), true));
        // Clic sur bouton 2
        btn2.setOnClickListener(v -> replaceFragment(new FragmentTwo(), true));
    }

    // Méthode générique de remplacement de fragment
    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            ft.addToBackStack(null); // permet de revenir en arrière avec le bouton back
        }

        ft.commit(); // exécute la transaction
    }
}