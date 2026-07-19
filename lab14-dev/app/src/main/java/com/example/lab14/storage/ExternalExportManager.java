package com.example.lab14.storage;

import android.content.Context;

import java.io.File;
import java.nio.charset.StandardCharsets;

public final class ExternalExportManager {

    private ExternalExportManager() {}

    public static String exportData(Context context, String fileName, String content) throws Exception {
        File externalDir = context.getExternalFilesDir(null);
        if (externalDir == null) return null;

        File exportFile = new File(externalDir, fileName);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            java.nio.file.Files.write(exportFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } else {
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(exportFile)) {
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }
        }
        return exportFile.getAbsolutePath();
    }

    public static String readExportedData(Context context, String fileName) throws Exception {
        File externalDir = context.getExternalFilesDir(null);
        if (externalDir == null) return null;

        File exportFile = new File(externalDir, fileName);
        if (!exportFile.exists()) return null;
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return new String(java.nio.file.Files.readAllBytes(exportFile.toPath()), StandardCharsets.UTF_8);
        } else {
            try (java.io.FileInputStream fis = new java.io.FileInputStream(exportFile)) {
                byte[] bytes = new byte[fis.available()];
                int bytesRead = fis.read(bytes);
                return bytesRead != -1 ? new String(bytes, 0, bytesRead, StandardCharsets.UTF_8) : "";
            }
        }
    }

    public static boolean deleteExportedData(Context context, String fileName) {
        File externalDir = context.getExternalFilesDir(null);
        if (externalDir == null) return false;

        File exportFile = new File(externalDir, fileName);
        return exportFile.delete();
    }
}
