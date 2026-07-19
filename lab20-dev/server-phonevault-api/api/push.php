<?php
/**
 * PhoneVault API — POST /api/push.php
 * Reçoit un contact JSON depuis l'app Android et l'insère en base.
 *
 * Corps attendu (JSON) :
 * {
 *   "display_name": "Alice Martin",
 *   "phone_number":  "+212601020304",
 *   "origin":        "android"          // optionnel
 * }
 */
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once __DIR__ . '/../repository/EntryRepository.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["success" => false, "message" => "Méthode non autorisée"]);
    exit;
}

$raw  = file_get_contents("php://input");
$body = json_decode($raw, true);

if (!isset($body['display_name'], $body['phone_number'])) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Champs requis manquants : display_name, phone_number"]);
    exit;
}

$displayName = trim($body['display_name']);
$phoneNumber = trim($body['phone_number']);
$origin      = trim($body['origin'] ?? 'android');

if ($displayName === '' || $phoneNumber === '') {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Les champs ne peuvent pas être vides"]);
    exit;
}

$repo = new EntryRepository();
$ok   = $repo->save($displayName, $phoneNumber, $origin);

http_response_code($ok ? 200 : 500);
echo json_encode([
    "success" => $ok,
    "message" => $ok ? "Contact enregistré avec succès" : "Erreur lors de l'enregistrement"
]);
