<?php
// Configuration de la base de données
// TODO(security): En production, ne jamais laisser les identifiants en dur, utiliser des variables d'environnement.
$host = "localhost";
$db_name = "map_project";
$username = "root";
$password = "";

try {
    $conn = new PDO("mysql:host=$host;dbname=$db_name;charset=utf8mb4", $username, $password);
    // Configuration stricte des erreurs pour plus de sécurité
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    // Désactiver les requêtes émulées pour de vraies requêtes préparées (protection supplémentaire contre les injections)
    $conn->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
} catch(PDOException $e) {
    // Ne jamais exposer l'erreur PDO directement en production.
    error_log("Connection failed: " . $e->getMessage());
    echo json_encode(["success" => false, "message" => "Database connection error"]);
    exit;
}
?>
