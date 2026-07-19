package com.example.lab17;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private FlightStateObserver flightObserver;
    private boolean isObserverActive = false;
    private Button btnToggleFlightState, btnDispatchInternalMsg;
    private TextView tvFlightStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        flightObserver = new FlightStateObserver();
        tvFlightStatus = findViewById(R.id.tvFlightStatus);
        btnToggleFlightState = findViewById(R.id.btnToggleFlightState);
        btnDispatchInternalMsg = findViewById(R.id.btnDispatchInternalMsg);

        btnToggleFlightState.setOnClickListener(v -> toggleFlightObserver());
        btnDispatchInternalMsg.setOnClickListener(v -> dispatchInternalMessage());
        
        Log.d(TAG, "DashboardActivity onCreate completed.");
    }

    private void toggleFlightObserver() {
        if (!isObserverActive) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            registerReceiver(flightObserver, filter);
            isObserverActive = true;
            
            tvFlightStatus.setText(R.string.status_observer_active);
            tvFlightStatus.setTextColor(getColor(R.color.status_active));
            btnToggleFlightState.setText(R.string.btn_disable_observer);
            
            Log.i(TAG, "FlightStateObserver a été ENREGISTRÉ dynamiquement.");
            showSnackbar("Observateur du mode avion activé !");
        } else {
            unregisterReceiver(flightObserver);
            isObserverActive = false;
            
            tvFlightStatus.setText(R.string.status_observer_inactive);
            tvFlightStatus.setTextColor(getColor(R.color.status_inactive));
            btnToggleFlightState.setText(R.string.btn_enable_observer);
            
            Log.i(TAG, "FlightStateObserver a été DÉSENREGISTRÉ.");
            showSnackbar("Observateur du mode avion désactivé.");
        }
    }

    private void dispatchInternalMessage() {
        Intent intent = new Intent("com.example.lab17.ACTION_PING_INTERNAL");
        intent.putExtra("payload", "Message secret envoyé depuis le Dashboard !");
        intent.setPackage(getPackageName()); // Sécurisation et compatibilité Android 8+
        
        sendBroadcast(intent);
        Log.d(TAG, "Custom Broadcast (Ping Internal) dispatché.");
        showSnackbar("Message interne expédié !");
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (isObserverActive) {
            unregisterReceiver(flightObserver);
            Log.d(TAG, "Nettoyage : désenregistrement de l'observateur.");
        }
        super.onDestroy();
    }
}
