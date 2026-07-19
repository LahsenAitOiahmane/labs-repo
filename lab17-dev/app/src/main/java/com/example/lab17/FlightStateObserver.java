package com.example.lab17;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class FlightStateObserver extends BroadcastReceiver {

    private static final String TAG = "FlightStateObserver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            boolean isFlightModeActive = intent.getBooleanExtra("state", false);
            
            String logMsg = isFlightModeActive 
                ? "ALERTE: Mode Avion détecté (Actif)." 
                : "INFO: Mode Avion désactivé (Réseau restauré).";
                
            Log.w(TAG, logMsg);
            Toast.makeText(context, logMsg, Toast.LENGTH_LONG).show();
        }
    }
}
