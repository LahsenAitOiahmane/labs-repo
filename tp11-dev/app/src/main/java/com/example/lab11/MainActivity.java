package com.example.lab11;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.lab11.model.GpsCoordinate;
import com.example.lab11.network.CoordinateUploader;
import com.example.lab11.utils.DeviceIdentifier;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * MainActivity — GPS Tracker screen.
 *
 * Responsibilities:
 *  - Request runtime permissions for location and phone state
 *  - Listen for GPS fixes via LocationManager
 *  - Display live coordinates in the UI
 *  - Send positions to the PHP backend via Volley (manual + automatic every 30 s)
 *  - Maintain an on-screen activity log
 */
public class MainActivity extends AppCompatActivity implements LocationListener {

    // ─── Constants ──────────────────────────────────────────────────────────

    private static final String TAG = "GeoTracker";

    /** PHP endpoint — update this IP to your host machine's address. */
    private static final String SERVER_ENDPOINT =
            "http://192.168.137.1:80/localisation/createPosition.php";

    /** Auto-send interval in milliseconds (30 seconds). */
    private static final long AUTO_SEND_INTERVAL_MS = 30_000L;

    /** GPS minimum update interval. */
    private static final long GPS_MIN_TIME_MS   = 5_000L;
    private static final float GPS_MIN_DIST_M   = 5.0f;

    private static final SimpleDateFormat TIMESTAMP_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    // ─── Permissions ────────────────────────────────────────────────────────

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    this::onPermissionResult);

    // ─── UI Views ────────────────────────────────────────────────────────────

    private TextView   tvLatitude;
    private TextView   tvLongitude;
    private TextView   tvTimestamp;
    private TextView   tvAccuracy;
    private TextView   tvDeviceId;
    private TextView   tvServerUrl;
    private TextView   tvLastSent;
    private TextView   tvSentCount;
    private TextView   tvStatusMessage;
    private TextView   tvActivityLog;
    private ScrollView logScrollView;

    private Button btnStartTracking;
    private Button btnStopTracking;
    private Button btnSendNow;

    private View pulseRing2;
    private View pulseRing3;

    // ─── State ───────────────────────────────────────────────────────────────

    private LocationManager locationManager;
    private GpsCoordinate   lastCoordinate;
    private String          deviceId;
    private boolean         isTracking     = false;
    private int             sentCount      = 0;

    private Animation pulseAnimation;

    private final Handler autoSendHandler = new Handler(Looper.getMainLooper());
    private final Runnable autoSendRunnable = this::performAutoSend;

    // ─── Lifecycle ───────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        setupClickListeners();
        setupPulseAnimation();

        // Resolve device ID once
        deviceId = DeviceIdentifier.resolve(this);
        tvDeviceId.setText(deviceId);
        tvServerUrl.setText(SERVER_ENDPOINT);

        appendLog("Application GeoTracker démarrée", LogLevel.INFO);

        // Request permissions
        if (!allPermissionsGranted()) {
            permissionLauncher.launch(REQUIRED_PERMISSIONS);
        } else {
            onPermissionsReady();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Keep GPS alive only when tracking (battery concern)
        if (!isTracking) {
            stopGpsUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        stopGpsUpdates();
        autoSendHandler.removeCallbacks(autoSendRunnable);
        super.onDestroy();
    }

    // ─── View binding ────────────────────────────────────────────────────────

    private void bindViews() {
        tvLatitude      = findViewById(R.id.tvLatitude);
        tvLongitude     = findViewById(R.id.tvLongitude);
        tvTimestamp     = findViewById(R.id.tvTimestamp);
        tvAccuracy      = findViewById(R.id.tvAccuracy);
        tvDeviceId      = findViewById(R.id.tvDeviceId);
        tvServerUrl     = findViewById(R.id.tvServerUrl);
        tvLastSent      = findViewById(R.id.tvLastSent);
        tvSentCount     = findViewById(R.id.tvSentCount);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        tvActivityLog   = findViewById(R.id.tvActivityLog);
        logScrollView   = findViewById(R.id.logScrollView);

        btnStartTracking = findViewById(R.id.btnStartTracking);
        btnStopTracking  = findViewById(R.id.btnStopTracking);
        btnSendNow       = findViewById(R.id.btnSendNow);

        pulseRing2 = findViewById(R.id.pulseRing2);
        pulseRing3 = findViewById(R.id.pulseRing3);
    }

    private void setupClickListeners() {
        btnStartTracking.setOnClickListener(v -> startTracking());
        btnStopTracking.setOnClickListener(v  -> stopTracking());
        btnSendNow.setOnClickListener(v       -> sendCurrentPosition());
        findViewById(R.id.btnClearLog).setOnClickListener(v -> {
            tvActivityLog.setText("");
            appendLog("Journal effacé", LogLevel.INFO);
        });
    }

    // ─── Animation ───────────────────────────────────────────────────────────

    private void setupPulseAnimation() {
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_ring);
    }

    private void startPulse() {
        pulseRing2.startAnimation(pulseAnimation);
        pulseRing3.startAnimation(pulseAnimation);
        pulseRing2.setVisibility(View.VISIBLE);
        pulseRing3.setVisibility(View.VISIBLE);
    }

    private void stopPulse() {
        pulseRing2.clearAnimation();
        pulseRing3.clearAnimation();
        pulseRing2.setVisibility(View.INVISIBLE);
        pulseRing3.setVisibility(View.INVISIBLE);
    }

    // ─── Permissions ────────────────────────────────────────────────────────

    private boolean allPermissionsGranted() {
        for (String perm : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void onPermissionResult(Map<String, Boolean> results) {
        boolean locationGranted =
                Boolean.TRUE.equals(results.get(Manifest.permission.ACCESS_FINE_LOCATION))
                || Boolean.TRUE.equals(results.get(Manifest.permission.ACCESS_COARSE_LOCATION));

        if (!locationGranted) {
            updateStatus("Permission de localisation refusée", StatusColor.ERROR);
            appendLog("Permission GPS refusée — impossible de démarrer le suivi", LogLevel.ERROR);
            return;
        }

        // Refresh device ID after phone-state permission might be granted
        deviceId = DeviceIdentifier.resolve(this);
        tvDeviceId.setText(deviceId);

        onPermissionsReady();
    }

    private void onPermissionsReady() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        updateStatus("Prêt — appuyez sur Démarrer", StatusColor.INFO);
        appendLog("Permissions accordées — GPS disponible", LogLevel.SUCCESS);
    }

    // ─── GPS Tracking ────────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    private void startTracking() {
        if (isTracking) return;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            updateStatus("GPS désactivé sur l'appareil", StatusColor.ERROR);
            appendLog("GPS désactivé — activez la localisation dans les paramètres", LogLevel.ERROR);
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                GPS_MIN_TIME_MS,
                GPS_MIN_DIST_M,
                this);

        isTracking = true;
        startPulse();

        btnStartTracking.setEnabled(false);
        btnStopTracking.setEnabled(true);
        btnSendNow.setEnabled(false);   // will enable on first fix

        updateStatus("Recherche du signal GPS…", StatusColor.INFO);
        appendLog("Suivi GPS démarré — en attente de fix satellite", LogLevel.INFO);

        // Schedule periodic auto-send
        autoSendHandler.postDelayed(autoSendRunnable, AUTO_SEND_INTERVAL_MS);
    }

    private void stopTracking() {
        stopGpsUpdates();
        autoSendHandler.removeCallbacks(autoSendRunnable);
        isTracking = false;
        stopPulse();

        btnStartTracking.setEnabled(true);
        btnStopTracking.setEnabled(false);
        btnSendNow.setEnabled(lastCoordinate != null);

        updateStatus("Suivi arrêté", StatusColor.INFO);
        appendLog("Suivi GPS arrêté", LogLevel.INFO);
    }

    private void stopGpsUpdates() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException ignored) { /* permissions lost */ }
        }
    }

    // ─── LocationListener ────────────────────────────────────────────────────

    @Override
    public void onLocationChanged(@NonNull Location location) {
        String ts = TIMESTAMP_FMT.format(new Date(location.getTime()));

        lastCoordinate = new GpsCoordinate(
                location.getLatitude(),
                location.getLongitude(),
                ts,
                deviceId,
                location.getAccuracy());

        refreshCoordinateUi(lastCoordinate);
        btnSendNow.setEnabled(true);

        appendLog(String.format(Locale.getDefault(),
                "Fix GPS → %.6f, %.6f (±%.0f m)",
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy()), LogLevel.SUCCESS);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        updateStatus("GPS activé", StatusColor.SUCCESS);
        appendLog("Fournisseur GPS activé : " + provider, LogLevel.INFO);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        updateStatus("GPS désactivé", StatusColor.ERROR);
        appendLog("Fournisseur GPS désactivé : " + provider, LogLevel.ERROR);
    }

    // ─── UI refresh ──────────────────────────────────────────────────────────

    @SuppressLint("DefaultLocale")
    private void refreshCoordinateUi(GpsCoordinate coord) {
        tvLatitude.setText(String.format("%.6f°", coord.getLatitude()));
        tvLongitude.setText(String.format("%.6f°", coord.getLongitude()));
        tvTimestamp.setText(coord.getTimestamp());
        tvAccuracy.setText(String.format("± %.0f m", coord.getAccuracy()));

        updateStatus("Signal GPS acquis", StatusColor.SUCCESS);
        tvSentCount.setText(String.valueOf(sentCount));
    }

    // ─── Sending ─────────────────────────────────────────────────────────────

    private void sendCurrentPosition() {
        if (lastCoordinate == null) {
            appendLog("Pas de position GPS disponible", LogLevel.ERROR);
            return;
        }
        dispatchUpload(lastCoordinate);
    }

    private void performAutoSend() {
        if (!isTracking) return;
        if (lastCoordinate != null) {
            appendLog("Envoi automatique programmé…", LogLevel.INFO);
            dispatchUpload(lastCoordinate);
        }
        autoSendHandler.postDelayed(autoSendRunnable, AUTO_SEND_INTERVAL_MS);
    }

    private void dispatchUpload(GpsCoordinate coord) {
        updateStatus("Envoi en cours…", StatusColor.INFO);
        appendLog("Connexion à " + SERVER_ENDPOINT, LogLevel.INFO);

        CoordinateUploader.send(this, SERVER_ENDPOINT, coord, new CoordinateUploader.UploadCallback() {
            @Override
            public void onSuccess(String response) {
                sentCount++;
                tvSentCount.setText(String.valueOf(sentCount));

                String now = TIMESTAMP_FMT.format(new Date());
                tvLastSent.setText(now);

                updateStatus("Position enregistrée ✓", StatusColor.SUCCESS);
                appendLog("Serveur : " + response, LogLevel.SUCCESS);
            }

            @Override
            public void onFailure(String error) {
                updateStatus("Erreur réseau", StatusColor.ERROR);
                appendLog("Échec de l'envoi : " + error, LogLevel.ERROR);
            }
        });
    }

    // ─── Status helpers ──────────────────────────────────────────────────────

    private enum StatusColor { INFO, SUCCESS, ERROR }

    private void updateStatus(String message, StatusColor color) {
        tvStatusMessage.setText(message);
        int textColor;
        switch (color) {
            case SUCCESS: textColor = Color.parseColor("#00C9A7"); break;
            case ERROR:   textColor = Color.parseColor("#FF6B6B"); break;
            default:      textColor = Color.parseColor("#7DB5CC"); break;
        }
        tvStatusMessage.setTextColor(textColor);
    }

    // ─── Activity log ────────────────────────────────────────────────────────

    private enum LogLevel { INFO, SUCCESS, ERROR }

    private void appendLog(String message, LogLevel level) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(new Date());
        String prefix;
        int color;
        switch (level) {
            case SUCCESS: prefix = "✓";  color = Color.parseColor("#00C9A7"); break;
            case ERROR:   prefix = "✗";  color = Color.parseColor("#FF6B6B"); break;
            default:      prefix = "→";  color = Color.parseColor("#7DB5CC"); break;
        }

        SpannableStringBuilder sb = new SpannableStringBuilder();

        // Append existing text
        CharSequence existing = tvActivityLog.getText();
        if (existing.length() > 0) {
            sb.append(existing).append("\n");
        }

        // Timestamp in dim colour
        int tsStart = sb.length();
        sb.append("[").append(time).append("] ");
        sb.setSpan(new ForegroundColorSpan(Color.parseColor("#3D6A80")),
                tsStart, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Prefix icon
        int prefixStart = sb.length();
        sb.append(prefix).append(" ");
        sb.setSpan(new ForegroundColorSpan(color),
                prefixStart, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Message
        int msgStart = sb.length();
        sb.append(message);
        sb.setSpan(new ForegroundColorSpan(color),
                msgStart, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvActivityLog.setText(sb);

        // Auto-scroll to bottom
        logScrollView.post(() -> logScrollView.fullScroll(ScrollView.FOCUS_DOWN));

        Log.d(TAG, "[" + level + "] " + message);
    }
}