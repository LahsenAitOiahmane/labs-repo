package com.example.lab20;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * PhoneVault — singleton Retrofit configuré pour l'API distante.
 *
 * ⚠️  Remplacez BASE_URL par l'adresse IP locale de votre machine XAMPP.
 *     Exemple : "http://192.168.1.42/phonevault-api/api/"
 *     (ne pas utiliser "localhost" sur Android car ça pointe vers l'émulateur)
 */
public class ApiClient {

    // ─────────────────────────────────────────────────────────────
    //  Modifier cette URL selon votre configuration réseau locale
    // ─────────────────────────────────────────────────────────────
    private static final String BASE_URL = "http://192.168.137.1/phonevault-api/api/";
    //   192.168.137.1 = adresse de l'hôte depuis l'émulateur Android Studio
    //  Sur appareil réel : utiliser l'IP locale (ex: 192.168.x.x)

    private static Retrofit instance;

    /** Retourne l'instance Retrofit (singleton). */
    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

    /** Raccourci pour créer directement une implémentation de VaultApi. */
    public static VaultApi getVaultApi() {
        return getInstance().create(VaultApi.class);
    }
}
