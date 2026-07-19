package com.example.labb11;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class GpsTrackerManager implements LocationListener {
    private static final String TAG = "NaviTrackerSys";
    private final Context context;
    private final LocationManager locationManager;
    private final LocationUpdateListener listener;

    public interface LocationUpdateListener {
        void onPositionUpdated(Location location);
    }

    public GpsTrackerManager(Context context, LocationUpdateListener listener) {
        this.context = context;
        this.listener = listener;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Log.i(TAG, "GpsTrackerManager initialized");
    }

    public boolean isGpsEnabled() {
        boolean enabled = locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Log.w(TAG, "GPS Provider is currently disabled by the user.");
        }
        return enabled;
    }

    public void startTracking() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Cannot start tracking: FINE_LOCATION permission missing.");
            return;
        }

        try {
            if (locationManager != null) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.d(TAG, "Requesting updates from GPS_PROVIDER");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 3f, this);
                }
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.d(TAG, "Requesting updates from NETWORK_PROVIDER");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 3f, this);
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException while requesting location updates", e);
        }
    }

    public void stopTracking() {
        if (locationManager != null) {
            Log.i(TAG, "Stopping location updates");
            locationManager.removeUpdates(this);
        }
    }

    public Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location loc = null;
        if (locationManager != null) {
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) {
                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        
        if (loc != null) {
             Log.d(TAG, "Retrieved last known location: Lat=" + loc.getLatitude() + ", Lng=" + loc.getLongitude());
        } else {
             Log.w(TAG, "Last known location is null");
        }
        
        return loc;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, "Location changed callback triggered");
        if (listener != null) {
            listener.onPositionUpdated(location);
        }
    }
}
