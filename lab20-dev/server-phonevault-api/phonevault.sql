-- ============================================================
-- PhoneVault — Script de configuration de la base de données
-- ============================================================
-- Exécuter ce script dans phpMyAdmin ou la console MySQL.

CREATE DATABASE IF NOT EXISTS phonevault
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE phonevault;

CREATE TABLE IF NOT EXISTS phonebook_entry (
    id           INT          AUTO_INCREMENT PRIMARY KEY,
    display_name VARCHAR(200) NOT NULL,
    phone_number VARCHAR(60)  NOT NULL,
    origin       VARCHAR(60)  DEFAULT 'android',
    synced_at    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_phone (phone_number)   -- évite les doublons sur le numéro
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Données de test (optionnel)
INSERT IGNORE INTO phonebook_entry (display_name, phone_number, origin) VALUES
    ('Alice Dupont',    '+33612345678', 'manual'),
    ('Bob Marchand',    '+33698765432', 'manual'),
    ('Carlos Rivera',   '+34690001122', 'manual');
