<?php

interface ILocationDao {
    public function insertLocation($locationObj);
    public function updateLocation($locationObj);
    public function deleteLocation($locationObj);
    public function findById($id);
    public function retrieveAllLocations();
}
