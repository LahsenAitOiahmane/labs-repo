package com.example.lab4;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.button.MaterialButton;

public class DashboardActivity extends AppCompatActivity {

    private MaterialButton navBtnDiscovery, navBtnInsights;
    private final int PRIMARY_COLOR = 0xFF2563EB;
    private final int SECONDARY_COLOR = 0xFF64748B;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeInterface();

        if (savedInstanceState == null) {
            renderContent(new ExplorerFragment(), false);
        }
    }

    private void initializeInterface() {
        navBtnDiscovery = findViewById(R.id.nav_discovery);
        navBtnInsights = findViewById(R.id.nav_insights);

        navBtnDiscovery.setOnClickListener(v -> {
            updateNavState(navBtnDiscovery, navBtnInsights);
            renderContent(new ExplorerFragment(), true);
        });

        navBtnInsights.setOnClickListener(v -> {
            updateNavState(navBtnInsights, navBtnDiscovery);
            renderContent(new AnalyticsFragment(), true);
        });
    }

    private void renderContent(Fragment fragment, boolean shouldCache) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_content_frame, fragment);

        if (shouldCache) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void updateNavState(MaterialButton selected, MaterialButton unselected) {
        selected.setTextColor(PRIMARY_COLOR);
        unselected.setTextColor(SECONDARY_COLOR);
    }
}