package com.example.lab20;

import com.google.gson.annotations.SerializedName;

/**
 * PhoneVault — modèle de données pour un contact synchronisé.
 * Les noms des champs correspondent aux clés JSON renvoyées par le backend.
 */
public class PhoneEntry {

    @SerializedName("id")
    private int entryId;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("origin")
    private String origin;

    @SerializedName("synced_at")
    private String syncedAt;

    /** Constructeur vide requis par Gson pour la désérialisation */
    public PhoneEntry() {}

    /** Constructeur pratique pour créer un contact à partir du téléphone */
    public PhoneEntry(String displayName, String phoneNumber) {
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        this.origin = "android";
    }

    // ─── Getters ────────────────────────────────────────────────

    public int getEntryId()        { return entryId;     }
    public String getDisplayName() { return displayName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getOrigin()      { return origin;      }
    public String getSyncedAt()    { return syncedAt;    }

    // ─── Setters ────────────────────────────────────────────────

    public void setEntryId(int entryId)          { this.entryId = entryId;         }
    public void setDisplayName(String name)       { this.displayName = name;        }
    public void setPhoneNumber(String phone)      { this.phoneNumber = phone;       }
    public void setOrigin(String origin)          { this.origin = origin;           }
    public void setSyncedAt(String syncedAt)      { this.syncedAt = syncedAt;       }

    /** Retourne l'initiale du nom pour l'avatar (ex : "Alice" → "A") */
    public String getInitial() {
        if (displayName == null || displayName.isEmpty()) return "?";
        return String.valueOf(displayName.charAt(0)).toUpperCase();
    }
}
