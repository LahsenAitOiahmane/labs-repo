package com.example.lab12.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.lab12.R;
import com.example.lab12.network.VolleySingleton;
import com.example.lab12.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_LOC = 101;
    private static final String TAG = "DashboardActivity";

    private TextView txtLatitude, txtLongitude;
    private LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        txtLatitude = findViewById(R.id.tvLatitudeVal);
        txtLongitude = findViewById(R.id.tvLongitudeVal);
        Button btnOpenMap = findViewById(R.id.btnOpenMap);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, TrackerMapActivity.class);
            startActivity(intent);
        });

        checkPermissionsAndStartTracking();
    }

    private void checkPermissionsAndStartTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_REQUEST_LOC);
        } else {
            initializeLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationUpdates() {
        // Paramètres de l'énoncé: 60000ms et 150m
        long minTimeMs = 60000;
        float minDistanceM = 150f;

        locManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTimeMs,
                minDistanceM,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        updateUI(lat, lon);
                        transmitLocationToServer(lat, lon);

                        String feedback = String.format(Locale.getDefault(),
                                getString(R.string.new_location),
                                lat, lon, location.getAccuracy());
                        
                        Snackbar.make(findViewById(android.R.id.content), "Localisation mise à jour", Snackbar.LENGTH_SHORT).show();
                        Log.d(TAG, feedback);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        String statusStr = status == LocationProvider.AVAILABLE ? "AVAILABLE" : 
                                         (status == LocationProvider.OUT_OF_SERVICE ? "OUT_OF_SERVICE" : "TEMPORARILY_UNAVAILABLE");
                        Log.d(TAG, "Provider " + provider + " status: " + statusStr);
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        Log.d(TAG, "Provider enabled: " + provider);
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        Log.d(TAG, "Provider disabled: " + provider);
                        Toast.makeText(DashboardActivity.this, "Veuillez activer le GPS", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void updateUI(double latitude, double longitude) {
        txtLatitude.setText(String.valueOf(latitude));
        txtLongitude.setText(String.valueOf(longitude));
    }

    private void transmitLocationToServer(final double lat, final double lon) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                Constants.API_ADD_LOCATION,
                response -> Log.i(TAG, "Sync successful: " + response),
                error -> {
                    Log.e(TAG, "Sync failed: " + error.getMessage());
                    Snackbar.make(findViewById(android.R.id.content), "Erreur de synchronisation réseau", Snackbar.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lon));
                params.put("date", dateFormat.format(new Date()));
                params.put("imei", fetchDeviceIdentifier());

                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private String fetchDeviceIdentifier() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceId != null && !deviceId.trim().isEmpty()) {
            return deviceId;
        }

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null && tm.getDeviceId() != null) {
                    return tm.getDeviceId();
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Impossible de récupérer l'IMEI, utilisation de l'ID par défaut", e);
        }

        return "UNKNOWN_DEVICE_" + System.currentTimeMillis();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLocationUpdates();
            } else {
                Toast.makeText(this, "Permission GPS requise pour le fonctionnement de l'application", Toast.LENGTH_LONG).show();
            }
        }
    }
}
