package com.example.lab13;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int PERMISSION_REQ_CODE = 200;
    private static final String API_RECORD_URL = "http://10.0.2.2/map_project/backend/record_location.php";

    private RequestQueue networkQueue;
    private LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        networkQueue = Volley.newRequestQueue(this);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Button btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(v -> {
            Log.d(TAG, "Opening MapTrackerActivity");
            startActivity(new Intent(HomeActivity.this, MapTrackerActivity.class));
        });

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        String[] requiredPermissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        };

        boolean allGranted = true;
        for (String perm : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            Log.d(TAG, "Requesting permissions");
            ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_REQ_CODE);
        } else {
            Log.d(TAG, "Permissions already granted. Starting location tracking.");
            beginLocationTracking();
        }
    }

    private void beginLocationTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permissions missing during tracking init.");
            return;
        }

        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    Log.d(TAG, "Location changed: Lat=" + lat + ", Lon=" + lon);
                    
                    Toast.makeText(HomeActivity.this, "Nouvelle position: " + lat + ", " + lon, Toast.LENGTH_SHORT).show();
                    transmitLocationToServer(lat, lon);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                    Log.d(TAG, "GPS Provider Enabled");
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                    Log.w(TAG, "GPS Provider Disabled");
                }
            });
            Log.d(TAG, "Location updates requested successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error requesting location updates: ", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (granted) {
                beginLocationTracking();
            } else {
                Toast.makeText(this, "Permissions requises pour le fonctionnement.", Toast.LENGTH_LONG).show();
                Log.w(TAG, "User denied permissions.");
            }
        }
    }

    private void transmitLocationToServer(double latitude, double longitude) {
        StringRequest request = new StringRequest(Request.Method.POST, API_RECORD_URL,
                response -> Log.d(TAG, "Server response: " + response),
                error -> Log.e(TAG, "Network error during transmission: ", error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                params.put("latitude", String.valueOf(latitude));
                params.put("longitude", String.valueOf(longitude));
                params.put("date", sdf.format(new Date()));
                params.put("imei", deviceId); 

                return params;
            }
        };

        networkQueue.add(request);
    }
}
