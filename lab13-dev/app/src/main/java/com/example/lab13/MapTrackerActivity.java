package com.example.lab13;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapTrackerActivity extends AppCompatActivity {

    private static final String TAG = "MapTrackerActivity";
    private static final String API_FETCH_URL = "http://10.0.2.2/map_project/backend/fetch_locations.php";

    private MapView osmMapView;
    private RequestQueue networkQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osm_prefs", MODE_PRIVATE));

        setContentView(R.layout.activity_map_tracker);

        osmMapView = findViewById(R.id.osmMapView);
        setupMapView();

        networkQueue = Volley.newRequestQueue(this);
        retrieveLocations();
    }

    private void setupMapView() {
        osmMapView.setTileSource(TileSourceFactory.MAPNIK);
        osmMapView.setBuiltInZoomControls(true);
        osmMapView.setMultiTouchControls(true);

        osmMapView.getController().setZoom(15.0);
        // Default center
        osmMapView.getController().setCenter(new GeoPoint(37.272525, -122.12106));
    }

    private void retrieveLocations() {
        Log.d(TAG, "Fetching locations from backend...");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_FETCH_URL, null,
                response -> {
                    try {
                        if (response.has("positions")) {
                            JSONArray positionsArray = response.getJSONArray("positions");
                            Log.d(TAG, "Fetched " + positionsArray.length() + " positions.");

                            for (int i = 0; i < positionsArray.length(); i++) {
                                JSONObject posObj = positionsArray.getJSONObject(i);
                                double lat = posObj.getDouble("latitude");
                                double lng = posObj.getDouble("longitude");

                                placeMarker(lat, lng, "Position " + (i + 1));
                            }
                            osmMapView.invalidate();
                        } else {
                            Log.w(TAG, "No positions key in JSON response.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error: ", e);
                    }
                },
                error -> {
                    Log.e(TAG, "Network error fetching data: ", error);
                    Toast.makeText(this, "Erreur réseau lors de la récupération des positions.", Toast.LENGTH_SHORT).show();
                });

        networkQueue.add(request);
    }

    private void placeMarker(double lat, double lng, String title) {
        Marker mapMarker = new Marker(osmMapView);
        mapMarker.setPosition(new GeoPoint(lat, lng));
        mapMarker.setTitle(title);

        try {
            Drawable customIcon = getResources().getDrawable(R.drawable.custom_marker, null);
            if (customIcon instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) customIcon).getBitmap();
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                mapMarker.setIcon(new BitmapDrawable(getResources(), resized));
            } else {
                mapMarker.setIcon(customIcon);
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not load custom marker icon.", e);
        }

        mapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        osmMapView.getOverlays().add(mapMarker);
    }

    @Override
    public void onResume() {
        super.onResume();
        osmMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        osmMapView.onPause();
    }
}
