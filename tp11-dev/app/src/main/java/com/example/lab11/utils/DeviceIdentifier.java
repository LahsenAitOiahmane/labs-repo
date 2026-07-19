package com.example.lab11.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

/**
 * DeviceIdentifier — resolves the best available unique device identifier.
 *
 * Strategy (descending priority):
 *   1. IMEI via TelephonyManager (requires READ_PHONE_STATE; only on API ≤ 29)
 *   2. ANDROID_ID — stable per app-install, privacy-safe fallback
 *   3. Manufacturer + Model string — last resort
 */
public final class DeviceIdentifier {

    private static final String TAG = "DeviceIdentifier";

    private DeviceIdentifier() { /* utility class */ }

    /**
     * Returns the best available device ID string.
     * Never returns null; always returns at least the build fingerprint.
     */
    @SuppressLint("HardwareIds")
    public static String resolve(Context context) {

        // ── Attempt 1: IMEI (API 28 and below, or with granted permission) ──
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    TelephonyManager tm =
                            (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    if (tm != null) {
                        String imei;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            imei = tm.getImei();
                        } else {
                            imei = tm.getDeviceId();
                        }

                        if (imei != null && !imei.isEmpty()) {
                            Log.d(TAG, "Using IMEI as device ID");
                            return imei;
                        }
                    }
                } catch (SecurityException se) {
                    Log.w(TAG, "IMEI read denied at runtime: " + se.getMessage());
                } catch (Exception e) {
                    Log.w(TAG, "IMEI read failed: " + e.getMessage());
                }
            }
        }

        // ── Attempt 2: ANDROID_ID ──
        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (androidId != null && !androidId.isEmpty()
                && !"9774d56d682e549c".equals(androidId)) {  // known-bad value
            Log.d(TAG, "Using ANDROID_ID as device ID");
            return "ANDROID_" + androidId;
        }

        // ── Fallback: Build info ──
        String fallback = "BUILD_" + Build.MANUFACTURER + "_" + Build.MODEL;
        Log.d(TAG, "Using build fallback: " + fallback);
        return fallback;
    }
}
