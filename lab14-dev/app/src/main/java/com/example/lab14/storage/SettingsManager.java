package com.example.lab14.storage;

import android.content.Context;
import android.content.SharedPreferences;

public final class SettingsManager {

    private static final String PREFERENCES_FILE = "vaultguard_settings";
    private static final String KEY_USER_NAME = "key_user_name";
    private static final String KEY_LANGUAGE = "key_language";
    private static final String KEY_DARK_MODE = "key_dark_mode";

    private SettingsManager() {}

    public static boolean saveSettings(Context context, String userName, String language, boolean isDarkMode, boolean useSync) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit()
                .putString(KEY_USER_NAME, userName)
                .putString(KEY_LANGUAGE, language)
                .putBoolean(KEY_DARK_MODE, isDarkMode);

        if (useSync) {
            // commit() : synchrone
            return editor.commit();
        } else {
            // apply() : asynchrone
            editor.apply();
            return true;
        }
    }

    public static SettingsData loadSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        String userName = prefs.getString(KEY_USER_NAME, "");
        String language = prefs.getString(KEY_LANGUAGE, "fr");
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        return new SettingsData(userName, language, isDarkMode);
    }

    public static void wipeSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static final class SettingsData {
        public final String userName;
        public final String language;
        public final boolean isDarkMode;

        public SettingsData(String userName, String language, boolean isDarkMode) {
            this.userName = userName;
            this.language = language;
            this.isDarkMode = isDarkMode;
        }
    }
}
