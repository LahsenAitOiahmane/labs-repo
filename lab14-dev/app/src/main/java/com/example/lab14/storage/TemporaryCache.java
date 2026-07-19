package com.example.lab14.storage;

import android.content.Context;

import java.io.File;
import java.nio.charset.StandardCharsets;

public final class TemporaryCache {

    private TemporaryCache() {}

    public static void writeToCache(Context context, String fileName, String content) throws Exception {
        File cacheFile = new File(context.getCacheDir(), fileName);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            java.nio.file.Files.write(cacheFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } else {
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(cacheFile)) {
                fos.write(content.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    public static String readFromCache(Context context, String fileName) throws Exception {
        File cacheFile = new File(context.getCacheDir(), fileName);
        if (!cacheFile.exists()) return null;
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return new String(java.nio.file.Files.readAllBytes(cacheFile.toPath()), StandardCharsets.UTF_8);
        } else {
            try (java.io.FileInputStream fis = new java.io.FileInputStream(cacheFile)) {
                byte[] bytes = new byte[fis.available()];
                int bytesRead = fis.read(bytes);
                return bytesRead != -1 ? new String(bytes, 0, bytesRead, StandardCharsets.UTF_8) : "";
            }
        }
    }

    public static int clearCache(Context context) {
        File[] cachedFiles = context.getCacheDir().listFiles();
        if (cachedFiles == null) return 0;
        int deleteCount = 0;
        for (File f : cachedFiles) {
            if (f.delete()) {
                deleteCount++;
            }
        }
        return deleteCount;
    }
}
