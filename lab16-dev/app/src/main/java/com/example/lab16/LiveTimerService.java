package com.example.lab16;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LiveTimerService extends Service {

    private static final String TAG = "LiveTimerService";
    private static final String CHANNEL_ID = "live_timer_channel";
    private static final int NOTIFICATION_ID = 2026;
    
    public static final String ACTION_STOP_SERVICE = "com.example.lab16.ACTION_STOP";

    private final IBinder serviceBinder = new TimerBinder();
    private NotificationManager notificationManager;
    private ScheduledExecutorService timerExecutor;
    
    private int elapsedSeconds = 0;
    private boolean isTimerRunning = false;

    // Binder class to return service instance to Activity
    public class TimerBinder extends Binder {
        public LiveTimerService getService() {
            return LiveTimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate called");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        setupNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = (intent != null) ? intent.getAction() : null;
        Log.d(TAG, "onStartCommand received action: " + action);

        if (ACTION_STOP_SERVICE.equals(action)) {
            Log.d(TAG, "Stopping service via intent action");
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!isTimerRunning) {
            isTimerRunning = true;
            startForeground(NOTIFICATION_ID, buildLiveNotification());
            beginCounting();
        }
        
        return START_STICKY;
    }

    private void beginCounting() {
        timerExecutor = Executors.newSingleThreadScheduledExecutor();
        timerExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                elapsedSeconds++;
                refreshNotification();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows active timer duration");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildLiveNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText("Elapsed: " + formatTimeDisplay(elapsedSeconds))
                .setSmallIcon(android.R.drawable.ic_media_play) // Standard icon fallback
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    private void refreshNotification() {
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, buildLiveNotification());
        }
    }

    private String formatTimeDisplay(int totalSecs) {
        int mins = totalSecs / 60;
        int secs = totalSecs % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Client bound to service");
        return serviceBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy called, cleaning up resources");
        isTimerRunning = false;
        if (timerExecutor != null && !timerExecutor.isShutdown()) {
            timerExecutor.shutdownNow();
        }
        stopForeground(true);
        super.onDestroy();
    }
}
