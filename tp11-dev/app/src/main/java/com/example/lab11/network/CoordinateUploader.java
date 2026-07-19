package com.example.lab11.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lab11.model.GpsCoordinate;

import java.util.HashMap;
import java.util.Map;

/**
 * CoordinateUploader — handles HTTP POST to the PHP endpoint using Volley.
 *
 * Keeps a singleton RequestQueue to avoid creating a new queue on every send.
 */
public final class CoordinateUploader {

    private static final String TAG     = "CoordinateUploader";
    private static final int    TIMEOUT = 12_000; // ms

    private static volatile RequestQueue sQueue;

    private CoordinateUploader() {}

    /** Obtain or create the singleton Volley queue. */
    private static RequestQueue getQueue(Context context) {
        if (sQueue == null) {
            synchronized (CoordinateUploader.class) {
                if (sQueue == null) {
                    sQueue = Volley.newRequestQueue(context.getApplicationContext());
                }
            }
        }
        return sQueue;
    }

    /** Callback interface for upload result. */
    public interface UploadCallback {
        void onSuccess(String serverResponse);
        void onFailure(String errorMessage);
    }

    /**
     * Sends a {@link GpsCoordinate} to the given endpoint as an HTTP POST request.
     *
     * @param context   application context
     * @param endpoint  full URL, e.g. "http://192.168.137.1:80/localisation/createPosition.php"
     * @param coord     the coordinate snapshot to upload
     * @param callback  result callback (called on main thread)
     */
    public static void send(Context context,
                            String endpoint,
                            GpsCoordinate coord,
                            UploadCallback callback) {

        Log.d(TAG, "Uploading coordinate: " + coord);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                endpoint,

                // ── Success ──
                response -> {
                    Log.i(TAG, "Server response: " + response);
                    if (callback != null) callback.onSuccess(response.trim());
                },

                // ── Error ──
                error -> {
                    String msg;
                    if (error.networkResponse != null) {
                        msg = "HTTP " + error.networkResponse.statusCode
                                + " — " + new String(error.networkResponse.data);
                    } else if (error.getCause() != null) {
                        msg = error.getCause().getMessage();
                    } else {
                        msg = error.getMessage() != null ? error.getMessage() : "Erreur inconnue";
                    }
                    Log.e(TAG, "Upload failed: " + msg);
                    if (callback != null) callback.onFailure(msg);
                }

        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("latitude",      String.valueOf(coord.getLatitude()));
                params.put("longitude",     String.valueOf(coord.getLongitude()));
                params.put("date_position", coord.getTimestamp());
                params.put("imei",          coord.getDeviceId());
                return params;
            }
        };

        // Retry policy: 1 attempt, 12 s timeout
        request.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        getQueue(context).add(request);
    }
}
