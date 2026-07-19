package com.example.lab16;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TimerDashboardActivity extends AppCompatActivity {

    private static final String TAG = "TimerDashboardActivity";

    private TextView tvLiveTime;
    private Button btnStart, btnStop;
    
    private LiveTimerService activeService;
    private boolean isServiceBound = false;

    // ServiceConnection for interacting with the Bound Service
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "Successfully connected to LiveTimerService");
            LiveTimerService.TimerBinder binder = (LiveTimerService.TimerBinder) service;
            activeService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "Disconnected from LiveTimerService unexpectedly");
            activeService = null;
            isServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_dashboard);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        tvLiveTime = findViewById(R.id.tvLiveTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
    }

    private void setupClickListeners() {
        btnStart.setOnClickListener(view -> launchTimerService());
        btnStop.setOnClickListener(view -> haltTimerService());
    }

    private void launchTimerService() {
        Log.d(TAG, "User requested to start the timer");
        Intent serviceIntent = new Intent(this, LiveTimerService.class);
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            Toast.makeText(this, "Timer Started", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch service", e);
            Toast.makeText(this, "Error starting service", Toast.LENGTH_SHORT).show();
        }
    }

    private void haltTimerService() {
        Log.d(TAG, "User requested to stop the timer");
        Intent serviceIntent = new Intent(this, LiveTimerService.class);
        serviceIntent.setAction(LiveTimerService.ACTION_STOP_SERVICE);
        
        try {
            startService(serviceIntent); // Sends the STOP action to the service

            if (isServiceBound) {
                unbindService(serviceConnection);
                isServiceBound = false;
                Log.d(TAG, "Service unbound successfully");
            }
            
            tvLiveTime.setText(getString(R.string.default_time));
            Toast.makeText(this, "Timer Stopped", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Failed to halt service cleanly", e);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Activity onDestroy called");
        if (isServiceBound) {
            unbindService(serviceConnection);
            isServiceBound = false;
        }
        super.onDestroy();
    }
}
