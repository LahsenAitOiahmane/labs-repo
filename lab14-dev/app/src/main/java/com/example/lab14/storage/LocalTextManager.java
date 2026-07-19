package com.example.lab14.storage;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public final class LocalTextManager {

    private LocalTextManager() {}

    public static void writeTextFile(Context context, String fileName, String content) throws Exception {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String readTextFile(Context context, String fileName) throws Exception {
        try (FileInputStream fis = context.openFileInput(fileName)) {
            byte[] bytes = new byte[fis.available()];
            int bytesRead = fis.read(bytes);
            if (bytesRead != -1) {
                return new String(bytes, 0, bytesRead, StandardCharsets.UTF_8);
            }
            return "";
        }
    }

    public static boolean deleteTextFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }
}
