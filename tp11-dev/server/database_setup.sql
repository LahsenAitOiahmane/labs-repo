-- ============================================================
-- GeoTracker — initialisation de la base de données MySQL
-- Exécuter dans phpMyAdmin ou via la CLI MySQL :
--   mysql -u root -p < database_setup.sql
-- ============================================================

-- Création de la base
CREATE DATABASE IF NOT EXISTS localisation
    CHARACTER SET  utf8mb4
    COLLATE        utf8mb4_unicode_ci;

USE localisation;

-- Table principale de stockage des positions GPS
CREATE TABLE IF NOT EXISTS position (
    id             INT           AUTO_INCREMENT PRIMARY KEY,
    latitude       DOUBLE        NOT NULL,
    longitude      DOUBLE        NOT NULL,
    date_position  DATETIME      NOT NULL,
    imei           VARCHAR(50)   NOT NULL,

    -- Métadonnées d'audit
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    -- Index pour accélérer les recherches par appareil et par date
    INDEX idx_imei      (imei),
    INDEX idx_date      (date_position),
    INDEX idx_imei_date (imei, date_position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Jeu de données de test (optionnel, peut être supprimé en production)
INSERT INTO position (latitude, longitude, date_position, imei) VALUES
    (48.8566,   2.3522,  '2025-01-01 10:00:00', 'TEST_DEVICE_001'),
    (48.8534,   2.3488,  '2025-01-01 10:00:30', 'TEST_DEVICE_001'),
    (43.2965,   5.3698,  '2025-01-01 12:00:00', 'TEST_DEVICE_002');

-- Vérification
SELECT 'Base de données initialisée avec succès' AS statut;
SELECT COUNT(*) AS nb_positions FROM position;
