CREATE DATABASE IF NOT EXISTS `geotrack_db`;
USE `geotrack_db`;

CREATE TABLE `device_locations` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `recorded_at` DATETIME NOT NULL,
  `device_id` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
