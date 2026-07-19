<?php
// Configuration des en-têtes CORS
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

// Autoriser GET et POST pour plus de flexibilité lors des tests (contrairement à l'énoncé qui force POST)
if ($_SERVER["REQUEST_METHOD"] !== "POST" && $_SERVER["REQUEST_METHOD"] !== "GET") {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Method Not Allowed."]);
    exit;
}

include_once __DIR__ . '/services/LocationRepository.php';

function fetchAllLocations() {
    try {
        $repository = new LocationRepository();
        $locations = $repository->retrieveAllLocations();
        
        if($locations && count($locations) > 0) {
            http_response_code(200);
            echo json_encode([
                "status" => "success",
                "count" => count($locations),
                "positions" => $locations // on garde la clé 'positions' pour faciliter le traitement Android (même si on l'a renommée dans le json)
            ]);
        } else {
            http_response_code(404);
            echo json_encode([
                "status" => "info",
                "message" => "Aucune position trouvée.",
                "positions" => []
            ]);
        }
    } catch(Exception $e) {
        http_response_code(500);
        echo json_encode([
            "status" => "error", 
            "message" => "Erreur interne du serveur: " . $e->getMessage()
        ]);
    }
}

fetchAllLocations();
