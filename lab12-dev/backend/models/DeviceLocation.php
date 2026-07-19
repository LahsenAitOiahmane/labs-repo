<?php

class DeviceLocation {
    private $id;
    private $latitude;
    private $longitude;
    private $recordedAt;
    private $deviceId;

    public function __construct($id, $latitude, $longitude, $recordedAt, $deviceId) {
        $this->id = $id;
        $this->latitude = $latitude;
        $this->longitude = $longitude;
        $this->recordedAt = $recordedAt;
        $this->deviceId = $deviceId;
    }

    // Getters
    public function getId() { return $this->id; }
    public function getLatitude() { return $this->latitude; }
    public function getLongitude() { return $this->longitude; }
    public function getRecordedAt() { return $this->recordedAt; }
    public function getDeviceId() { return $this->deviceId; }

    // Setters
    public function setId($id) { $this->id = $id; }
    public function setLatitude($latitude) { $this->latitude = $latitude; }
    public function setLongitude($longitude) { $this->longitude = $longitude; }
    public function setRecordedAt($recordedAt) { $this->recordedAt = $recordedAt; }
    public function setDeviceId($deviceId) { $this->deviceId = $deviceId; }
    
    // Utile pour la conversion rapide en JSON
    public function toArray() {
        return [
            'id' => $this->id,
            'latitude' => $this->latitude,
            'longitude' => $this->longitude,
            'recordedAt' => $this->recordedAt,
            'deviceId' => $this->deviceId
        ];
    }
}
