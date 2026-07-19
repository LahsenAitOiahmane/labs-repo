<?php
// Configuration des en-têtes CORS et du type de contenu
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

if($_SERVER["REQUEST_METHOD"] !== "POST"){
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method Not Allowed. Use POST."]);
    exit;
}

include_once __DIR__ . '/services/LocationRepository.php';
include_once __DIR__ . '/models/DeviceLocation.php';

// Récupération des données POST
$latitude = isset($_POST['latitude']) ? $_POST['latitude'] : null;
$longitude = isset($_POST['longitude']) ? $_POST['longitude'] : null;
$recordedAt = isset($_POST['date']) ? $_POST['date'] : null;
$deviceId = isset($_POST['imei']) ? $_POST['imei'] : null;

// IP du client (téléphone)
$clientIp = $_SERVER['REMOTE_ADDR'];

if ($latitude === null || $longitude === null || $recordedAt === null || $deviceId === null) {
    http_response_code(400);
    echo json_encode([
        "status" => "error", 
        "message" => "Données manquantes ou invalides.", 
        "client_ip" => $clientIp
    ]);
    exit;
}

try {
    $repository = new LocationRepository();
    $newLocation = new DeviceLocation(null, $latitude, $longitude, $recordedAt, $deviceId);
    
    if($repository->insertLocation($newLocation)) {
        http_response_code(201); // 201 Created
        echo json_encode([
            "status" => "success", 
            "message" => "Position ajoutée avec succès.",
            "client_ip" => $clientIp
        ]);
    } else {
        http_response_code(503); // 503 Service Unavailable
        echo json_encode([
            "status" => "error", 
            "message" => "Impossible d'ajouter la position."
        ]);
    }

} catch(Exception $e) {
    http_response_code(500); // 500 Internal Server Error
    echo json_encode([
        "status" => "error", 
        "message" => "Erreur interne du serveur.", 
        "debug_info" => $e->getMessage(),
        "client_ip" => $clientIp
    ]);
}
