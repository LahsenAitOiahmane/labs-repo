<?php
include_once __DIR__ . '/../dao/ILocationDao.php';
include_once __DIR__ . '/../models/DeviceLocation.php';
include_once __DIR__ . '/../config/DatabaseManager.php';

class LocationRepository implements ILocationDao {
    private $dbManager;

    public function __construct() {
        $this->dbManager = new DatabaseManager();
    }

    public function insertLocation($locationObj) {
        $sql = "INSERT INTO device_locations (latitude, longitude, recorded_at, device_id) VALUES (:lat, :lon, :recordedAt, :deviceId)";
        
        $stmt = $this->dbManager->getConnection()->prepare($sql);
        
        // Protection contre les injections
        $lat = htmlspecialchars(strip_tags($locationObj->getLatitude()));
        $lon = htmlspecialchars(strip_tags($locationObj->getLongitude()));
        $date = htmlspecialchars(strip_tags($locationObj->getRecordedAt()));
        $imei = htmlspecialchars(strip_tags($locationObj->getDeviceId()));

        $stmt->bindParam(":lat", $lat);
        $stmt->bindParam(":lon", $lon);
        $stmt->bindParam(":recordedAt", $date);
        $stmt->bindParam(":deviceId", $imei);

        if($stmt->execute()) {
            return true;
        }
        return false;
    }

    public function retrieveAllLocations() {
        $sql = "SELECT id, latitude, longitude, recorded_at AS recordedAt, device_id AS deviceId FROM device_locations ORDER BY recorded_at DESC";
        $stmt = $this->dbManager->getConnection()->prepare($sql);
        $stmt->execute();
        
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    // Méthodes de l'interface non utilisées pour ce LAB mais nécessaires
    public function updateLocation($locationObj) { return false; }
    public function deleteLocation($locationObj) { return false; }
    public function findById($id) { return null; }
}
