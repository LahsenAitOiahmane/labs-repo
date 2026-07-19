<?php
/**
 * PhoneVault API — Modèle Entry
 * Représente un contact synchronisé depuis l'application mobile.
 */
class Entry {
    public int    $id;
    public string $display_name;
    public string $phone_number;
    public string $origin;
    public string $synced_at;
}
