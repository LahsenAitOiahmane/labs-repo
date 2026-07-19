package com.example.lab12.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lab12.R;
import com.example.lab12.network.VolleySingleton;
import com.example.lab12.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackerMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "TrackerMapActivity";
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;
        fetchAndDisplayLocations();
    }

    private void fetchAndDisplayLocations() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, // Modification pour être compatible avec l'API PHP refactorisée
                Constants.API_GET_LOCATIONS,
                null,
                response -> {
                    try {
                        if (response.has("positions")) {
                            JSONArray positionsArray = response.getJSONArray("positions");
                            LatLng lastKnownLoc = null;

                            for (int i = 0; i < positionsArray.length(); i++) {
                                JSONObject posObj = positionsArray.getJSONObject(i);
                                double lat = posObj.getDouble("latitude");
                                double lon = posObj.getDouble("longitude");

                                lastKnownLoc = new LatLng(lat, lon);
                                googleMap.addMarker(new MarkerOptions()
                                        .position(lastKnownLoc)
                                        .title("Position Enregistrée"));
                            }

                            // Centrer la caméra sur le dernier point connu
                            if (lastKnownLoc != null) {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLoc, 12.0f));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error", e);
                    }
                },
                error -> {
                    Log.e(TAG, "API call failed: " + error.getMessage());
                    Toast.makeText(this, "Impossible de charger les positions depuis le serveur", Toast.LENGTH_LONG).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
