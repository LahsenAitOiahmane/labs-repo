package com.example.lab14.security;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public final class TokenVault {

    private static final String SECURE_PREFS_FILE = "vaultguard_secure_prefs";
    private static final String KEY_SECRET_TOKEN = "key_secret_token";

    private TokenVault() {}

    private static SharedPreferences getSecurePrefs(Context context) throws Exception {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                SECURE_PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public static void saveSecureToken(Context context, String token) throws Exception {
        // Enregistrement sécurisé du token, aucun log ici.
        getSecurePrefs(context).edit().putString(KEY_SECRET_TOKEN, token).apply();
    }

    public static String loadSecureToken(Context context) throws Exception {
        return getSecurePrefs(context).getString(KEY_SECRET_TOKEN, "");
    }

    public static void wipeSecureToken(Context context) throws Exception {
        getSecurePrefs(context).edit().clear().apply();
    }
}
