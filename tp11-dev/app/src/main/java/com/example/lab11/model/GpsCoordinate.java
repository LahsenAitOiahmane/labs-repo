package com.example.lab11.model;

/**
 * GpsCoordinate — immutable snapshot of a device's geographic position.
 *
 * Holds the four fields that are persisted on the remote server:
 *   latitude, longitude, a formatted timestamp and the device identifier.
 */
public final class GpsCoordinate {

    private final double latitude;
    private final double longitude;
    private final String timestamp;   // formatted as "yyyy-MM-dd HH:mm:ss"
    private final String deviceId;    // IMEI or ANDROID_ID fallback
    private final float  accuracy;    // metres, informational only

    public GpsCoordinate(double latitude,
                         double longitude,
                         String timestamp,
                         String deviceId,
                         float accuracy) {
        this.latitude  = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.deviceId  = deviceId;
        this.accuracy  = accuracy;
    }

    public double getLatitude()  { return latitude;  }
    public double getLongitude() { return longitude; }
    public String getTimestamp() { return timestamp; }
    public String getDeviceId()  { return deviceId;  }
    public float  getAccuracy()  { return accuracy;  }

    @Override
    public String toString() {
        return "GpsCoordinate{lat=" + latitude
                + ", lng=" + longitude
                + ", ts=" + timestamp
                + ", dev=" + deviceId + "}";
    }
}
