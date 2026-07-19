package com.example.lab21;

import android.hardware.Sensor;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lab21.fragments.ActionDetectorFragment;
import com.example.lab21.fragments.KinematicsFragment;
import com.example.lab21.fragments.LiveChartFragment;
import com.example.lab21.fragments.NavigationCompassFragment;
import com.example.lab21.fragments.OverviewFragment;
import com.example.lab21.fragments.PedometerFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OverviewFragment()).commit();
            navView.setCheckedItem(R.id.nav_sensors);
            setTitle("Capteurs Disponibles");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_sensors) {
            selectedFragment = new OverviewFragment();
            title = "Capteurs Disponibles";
        } else if (id == R.id.nav_temperature) {
            selectedFragment = LiveChartFragment.newInstance(Sensor.TYPE_AMBIENT_TEMPERATURE, "Température", "FIRST_VALUE");
            title = "Température";
        } else if (id == R.id.nav_humidity) {
            selectedFragment = LiveChartFragment.newInstance(Sensor.TYPE_RELATIVE_HUMIDITY, "Humidité", "FIRST_VALUE");
            title = "Humidité";
        } else if (id == R.id.nav_proximity) {
            selectedFragment = LiveChartFragment.newInstance(Sensor.TYPE_PROXIMITY, "Proximité", "FIRST_VALUE");
            title = "Proximité";
        } else if (id == R.id.nav_magnetic) {
            selectedFragment = LiveChartFragment.newInstance(Sensor.TYPE_MAGNETIC_FIELD, "Champ Magnétique", "MAGNITUDE");
            title = "Magnétomètre";
        } else if (id == R.id.nav_accelerometer) {
            selectedFragment = KinematicsFragment.newInstance(Sensor.TYPE_ACCELEROMETER, "Accéléromètre");
            title = "Accéléromètre";
        } else if (id == R.id.nav_gravity) {
            selectedFragment = KinematicsFragment.newInstance(Sensor.TYPE_GRAVITY, "Gravité");
            title = "Gravité";
        } else if (id == R.id.nav_gyroscope) {
            selectedFragment = KinematicsFragment.newInstance(Sensor.TYPE_GYROSCOPE, "Gyroscope");
            title = "Gyroscope";
        } else if (id == R.id.nav_steps) {
            selectedFragment = new PedometerFragment();
            title = "Compteur de pas";
        } else if (id == R.id.nav_compass) {
            selectedFragment = new NavigationCompassFragment();
            title = "Boussole";
        } else if (id == R.id.nav_activity) {
            selectedFragment = new ActionDetectorFragment();
            title = "Activité";
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            setTitle(title);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}