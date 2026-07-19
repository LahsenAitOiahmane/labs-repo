package com.example.lab20;

/**
 * PhoneVault — modèle de réponse API générique.
 * Représente la structure JSON retournée lors d'une opération d'écriture.
 *
 * Exemple de réponse :
 * { "success": true, "message": "Contact enregistré avec succès" }
 */
public class ServerResponse {
    private boolean success;
    private String  message;

    public boolean isSuccess() { return success; }
    public String  getMessage() { return message; }
}
