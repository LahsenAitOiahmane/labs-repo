<?php
/**
 * createPosition.php — point d'entrée HTTP POST pour enregistrer une position GPS.
 *
 * Paramètres POST attendus :
 *   - latitude      (float)
 *   - longitude     (float)
 *   - date_position (string, format "YYYY-MM-DD HH:MM:SS")
 *   - imei          (string, identifiant de l'appareil)
 *
 * Réponse :
 *   - 200 + JSON {"status":"ok","message":"..."} en cas de succès
 *   - 4xx/5xx + JSON {"status":"error","message":"..."} en cas d'erreur
 */

// ── En-têtes ──────────────────────────────────────────────────────────────────
header('Content-Type: application/json; charset=utf-8');

// Autoriser les requêtes cross-origin depuis l'émulateur (développement uniquement)
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Répondre immédiatement aux pré-vols CORS
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

// ── Vérification de la méthode HTTP ───────────────────────────────────────────
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Méthode non autorisée. Utilisez POST.']);
    exit;
}

// ── Chargement des dépendances ────────────────────────────────────────────────
require_once __DIR__ . '/service/GpsPositionService.php';

// ── Validation des paramètres ─────────────────────────────────────────────────
$requiredFields = ['latitude', 'longitude', 'date_position', 'imei'];
$missing = [];

foreach ($requiredFields as $field) {
    if (!isset($_POST[$field]) || trim($_POST[$field]) === '') {
        $missing[] = $field;
    }
}

if (!empty($missing)) {
    http_response_code(400);
    echo json_encode([
        'status'  => 'error',
        'message' => 'Paramètres manquants : ' . implode(', ', $missing),
    ]);
    exit;
}

// ── Nettoyage et validation des types ─────────────────────────────────────────
$rawLat  = filter_var(trim($_POST['latitude']),  FILTER_VALIDATE_FLOAT);
$rawLng  = filter_var(trim($_POST['longitude']), FILTER_VALIDATE_FLOAT);
$rawDate = trim($_POST['date_position']);
$rawImei = trim($_POST['imei']);

if ($rawLat === false || $rawLat < -90 || $rawLat > 90) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Latitude invalide.']);
    exit;
}

if ($rawLng === false || $rawLng < -180 || $rawLng > 180) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Longitude invalide.']);
    exit;
}

// Validation du format de date (YYYY-MM-DD HH:MM:SS)
$dateObj = DateTime::createFromFormat('Y-m-d H:i:s', $rawDate);
if (!$dateObj || $dateObj->format('Y-m-d H:i:s') !== $rawDate) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Format de date invalide. Attendu : YYYY-MM-DD HH:MM:SS']);
    exit;
}

if (strlen($rawImei) > 50) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Identifiant appareil trop long (max 50 car.).']);
    exit;
}

// ── Enregistrement en base ────────────────────────────────────────────────────
try {
    $service  = new GpsPositionService();
    $position = new GpsPosition(null, $rawLat, $rawLng, $rawDate, $rawImei);
    $inserted = $service->create($position);

    if ($inserted) {
        http_response_code(200);
        echo json_encode([
            'status'  => 'ok',
            'message' => 'Position enregistrée avec succès',
            'data'    => $position->toArray(),
        ]);
    } else {
        http_response_code(500);
        echo json_encode(['status' => 'error', 'message' => "L'insertion a échoué."]); 
    }

} catch (PDOException $e) {
    error_log('[GeoTracker] PDO exception : ' . $e->getMessage());
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Erreur interne du serveur.']);
}
