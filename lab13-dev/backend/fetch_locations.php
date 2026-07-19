<?php
header('Content-Type: application/json');
require_once 'config.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST' && $_SERVER['REQUEST_METHOD'] !== 'GET') {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
    exit;
}

try {
    // Utilisation d'une requête préparée même sans paramètre d'entrée
    $stmt = $conn->prepare("SELECT latitude, longitude, date FROM positions ORDER BY date DESC LIMIT 100");
    $stmt->execute();
    
    $positions = $stmt->fetchAll(PDO::FETCH_ASSOC);
    
    echo json_encode(["success" => true, "positions" => $positions]);
} catch(PDOException $e) {
    error_log("Select error: " . $e->getMessage());
    echo json_encode(["success" => false, "message" => "Erreur de récupération."]);
}
?>
