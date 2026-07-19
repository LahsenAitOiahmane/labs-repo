<?php
/**
 * PhoneVault API — GET /api/list.php
 * Retourne tous les contacts de la base au format JSON.
 */
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

require_once __DIR__ . '/../repository/EntryRepository.php';

$repo   = new EntryRepository();
$entries = $repo->findAll();

echo json_encode($entries, JSON_UNESCAPED_UNICODE);
