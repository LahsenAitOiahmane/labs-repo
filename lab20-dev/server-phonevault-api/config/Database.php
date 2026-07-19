<?php
/**
 * PhoneVault API — Database Configuration
 * Gestion de la connexion PDO vers MySQL
 */
class Database {
    private string $host     = "localhost";
    private string $dbName   = "phonevault";
    private string $user     = "root";
    private string $password = "";
    public  ?PDO   $pdo      = null;

    public function connect(): ?PDO {
        if ($this->pdo !== null) {
            return $this->pdo;
        }

        try {
            $dsn = sprintf(
                "mysql:host=%s;dbname=%s;charset=utf8mb4",
                $this->host,
                $this->dbName
            );

            $this->pdo = new PDO($dsn, $this->user, $this->password, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
            ]);
        } catch (PDOException $e) {
            http_response_code(500);
            echo json_encode([
                "success" => false,
                "message" => "Connexion échouée : " . $e->getMessage()
            ]);
            exit;
        }

        return $this->pdo;
    }
}
