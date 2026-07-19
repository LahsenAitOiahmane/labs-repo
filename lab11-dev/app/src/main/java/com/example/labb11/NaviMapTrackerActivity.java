package com.example.labb11;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class NaviMapTrackerActivity extends AppCompatActivity implements OnMapReadyCallback, GpsTrackerManager.LocationUpdateListener {

    private static final String TAG = "NaviTrackerSys";
    private static final int REQ_CODE_PERMS = 2002;

    private GoogleMap dashboardMap;
    private GpsTrackerManager trackerManager;
    private Marker activeLocationMarker;
    private TextView lblCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_map_tracker);
        
        Log.i(TAG, "NaviMapTrackerActivity created");

        lblCoordinates = findViewById(R.id.lbl_coordinates);
        ExtendedFloatingActionButton btnFindMe = findViewById(R.id.btn_find_me);

        trackerManager = new GpsTrackerManager(this, this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnFindMe.setOnClickListener(v -> panToLatestLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        Log.i(TAG, "Google Map is ready");
        dashboardMap = map;
        
        // Optional: you can inject a custom map style here using map.setMapStyle(...) if you have a raw resource
        dashboardMap.getUiSettings().setCompassEnabled(false);
        dashboardMap.getUiSettings().setMapToolbarEnabled(false);
        
        evaluatePermissionsAndInit();
    }

    private void evaluatePermissionsAndInit() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Requesting location permissions");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_CODE_PERMS);
        } else {
            activateLocationServices();
        }
    }

    private void activateLocationServices() {
        if (!trackerManager.isGpsEnabled()) {
            promptUserToEnableGps();
        }

        trackerManager.startTracking();
        panToLatestLocation();
    }

    private void panToLatestLocation() {
        Location latestLoc = trackerManager.getLastKnownLocation();
        if (latestLoc != null) {
            updateDashboardUi(latestLoc);
        } else {
            Log.w(TAG, "No cached location available to pan to.");
        }
    }

    private void promptUserToEnableGps() {
        Log.i(TAG, "Displaying GPS activation prompt");
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.gps_disabled_title)
                .setMessage(R.string.gps_disabled_message)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_gps, (dialog, id) -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    dialog.cancel();
                    Toast.makeText(this, "Sans GPS, la carte sera imprécise.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onPositionUpdated(Location location) {
        updateDashboardUi(location);
    }

    private void updateDashboardUi(Location location) {
        if (dashboardMap == null) return;

        LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
        
        String displayText = String.format("LAT: %.4f \nLNG: %.4f", location.getLatitude(), location.getLongitude());
        lblCoordinates.setText(displayText);

        if (activeLocationMarker == null) {
            Log.d(TAG, "Creating new marker on the map");
            MarkerOptions options = new MarkerOptions()
                    .position(coordinates)
                    .title("Ma Position Actuelle")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            activeLocationMarker = dashboardMap.addMarker(options);
            dashboardMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16f));
        } else {
            Log.v(TAG, "Animating existing marker to new coordinates");
            activeLocationMarker.setPosition(coordinates);
            dashboardMap.animateCamera(CameraUpdateFactory.newLatLng(coordinates));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_PERMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Location permission granted by user");
                activateLocationServices();
            } else {
                Log.w(TAG, "Location permission denied by user");
                Toast.makeText(this, R.string.location_permission_required, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Activity destroyed. Cleaning up trackers.");
        if (trackerManager != null) {
            trackerManager.stopTracking();
        }
    }
}
