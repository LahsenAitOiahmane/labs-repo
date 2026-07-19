package com.example.lab20;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * PhoneVault — interface Retrofit décrivant les endpoints du backend.
 *
 * Tous les chemins sont relatifs à la BASE_URL définie dans {@link ApiClient}.
 */
public interface VaultApi {

    /**
     * Envoie un contact vers le serveur (POST JSON).
     * Endpoint : POST /api/push.php
     */
    @POST("push.php")
    Call<ServerResponse> uploadEntry(@Body PhoneEntry entry);

    /**
     * Récupère tous les contacts depuis la base distante.
     * Endpoint : GET /api/list.php
     */
    @GET("list.php")
    Call<List<PhoneEntry>> fetchAll();

    /**
     * Recherche des contacts par nom ou numéro.
     * Endpoint : GET /api/search.php?q=...
     */
    @GET("search.php")
    Call<List<PhoneEntry>> lookupByKeyword(@Query("q") String keyword);
}
