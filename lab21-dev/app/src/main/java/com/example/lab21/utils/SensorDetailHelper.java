package com.example.lab21.utils;

import android.hardware.Sensor;

public class SensorDetailHelper {

    public static String getDetailedInfo(Sensor sensor) {
        StringBuilder builder = new StringBuilder();
        builder.append("Identification : ").append(sensor.getId()).append("\n")
               .append("Modèle : ").append(sensor.getName()).append("\n")
               .append("Constructeur : ").append(sensor.getVendor()).append("\n")
               .append("Version : ").append(sensor.getVersion()).append("\n")
               .append("Type (Chaîne) : ").append(sensor.getStringType()).append("\n")
               .append("Type (Entier) : ").append(sensor.getType()).append("\n")
               .append("Résolution : ").append(sensor.getResolution()).append("\n")
               .append("Consommation : ").append(sensor.getPower()).append(" mA\n")
               .append("Plage Max : ").append(sensor.getMaximumRange()).append("\n")
               .append("Délai Min : ").append(sensor.getMinDelay()).append(" µs");
        return builder.toString();
    }
}
