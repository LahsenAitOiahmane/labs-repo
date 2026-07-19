package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.appbar.MaterialToolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabsHeader;
    private ViewPager2 mainPager;
    private ViewPagerAdapter sectionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.custom_app_bar);
        setSupportActionBar(toolbar);

        tabsHeader = findViewById(R.id.main_tabs_header);
        mainPager = findViewById(R.id.content_pager);

        sectionsAdapter = new ViewPagerAdapter(this);
        mainPager.setAdapter(sectionsAdapter);

        new TabLayoutMediator(tabsHeader, mainPager,
                (tab, pos) -> {
                    if (pos == 0) tab.setText("Température");
                    else tab.setText("Distance");
                }
        ).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_exit_app) {
            triggerExitSequence();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        triggerExitSequence();
    }

    private void triggerExitSequence() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Souhaitez-vous fermer l'application ?")
                .setPositiveButton("Quitter", (dialog, which) -> finish())
                .setNegativeButton("Annuler", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}