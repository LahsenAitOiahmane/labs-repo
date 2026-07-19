<?php

class DatabaseManager {
    private $connection;

    public function __construct() {
        // Paramètres de configuration (peuvent être extraits dans un .env dans un projet réel)
        $host = '127.0.0.1'; // 'localhost' fonctionne souvent, mais 127.0.0.1 est plus sûr pour forcer TCP
        $dbname = 'geotrack_db';
        $user = 'root';
        $pass = '';

        try {
            $dsn = "mysql:host={$host};dbname={$dbname};charset=utf8mb4";
            $options = [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION, // Remonte les erreurs SQL sous forme d'exceptions
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC, // Par défaut on renvoie des tableaux associatifs
                PDO::ATTR_EMULATE_PREPARES => false, // Utilise les vraies requêtes préparées de MySQL
            ];
            
            $this->connection = new PDO($dsn, $user, $pass, $options);
            
        } catch (PDOException $e) {
            // En production, ne pas afficher les détails de l'erreur
            error_log("Connection error: " . $e->getMessage());
            die(json_encode(["status" => "error", "message" => "Database connection failed"]));
        }
    }

    public function getConnection() {
        return $this->connection;
    }
}
