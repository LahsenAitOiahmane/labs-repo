<?php
/**
 * PhoneVault API — GET /api/search.php?q=...
 * Recherche un contact par nom ou numéro de téléphone.
 */
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

require_once __DIR__ . '/../repository/EntryRepository.php';

$keyword = trim($_GET['q'] ?? '');

if ($keyword === '') {
    echo json_encode([], JSON_UNESCAPED_UNICODE);
    exit;
}

$repo    = new EntryRepository();
$results = $repo->findByKeyword($keyword);

echo json_encode($results, JSON_UNESCAPED_UNICODE);
