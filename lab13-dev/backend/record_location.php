<?php
header('Content-Type: application/json');
require_once 'config.php';

// Sécurisation basique : on vérifie que la requête est POST.
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit;
}

$latitude = $_POST['latitude'] ?? null;
$longitude = $_POST['longitude'] ?? null;
$date = $_POST['date'] ?? null;
$imei = $_POST['imei'] ?? null; // C'est en fait l'ANDROID_ID

// Validation stricte des types de données
if ($latitude === null || $longitude === null || $date === null || $imei === null) {
    echo json_encode(["success" => false, "message" => "Données manquantes"]);
    exit;
}

if (!is_numeric($latitude) || !is_numeric($longitude)) {
    echo json_encode(["success" => false, "message" => "Format de coordonnées invalide"]);
    exit;
}

try {
    // Utilisation stricte des requêtes préparées pour éviter toute injection SQL
    $stmt = $conn->prepare("INSERT INTO positions (latitude, longitude, date, imei) VALUES (:lat, :lon, :dt, :im)");
    
    // Binding avec les bons types
    $stmt->bindParam(':lat', $latitude);
    $stmt->bindParam(':lon', $longitude);
    $stmt->bindParam(':dt', $date);
    $stmt->bindParam(':im', $imei);
    
    $stmt->execute();
    
    echo json_encode(["success" => true, "message" => "Position enregistrée de façon sécurisée."]);
} catch(PDOException $e) {
    error_log("Insert error: " . $e->getMessage());
    echo json_encode(["success" => false, "message" => "Erreur lors de l'enregistrement."]);
}
?>
