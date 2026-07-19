<?php
/**
 * DbConnexion — gère la connexion PDO à la base de données MySQL.
 *
 * Singleton léger : une seule connexion PDO par requête HTTP.
 */
class DbConnexion
{
    private PDO $pdo;

    // Paramètres de connexion — à adapter selon l'environnement
    private const DB_HOST    = 'localhost';
    private const DB_NAME    = 'localisation';
    private const DB_CHARSET = 'utf8mb4';
    private const DB_USER    = 'root';
    private const DB_PASS    = '';

    public function __construct()
    {
        $dsn = sprintf(
            'mysql:host=%s;dbname=%s;charset=%s',
            self::DB_HOST,
            self::DB_NAME,
            self::DB_CHARSET
        );

        try {
            $this->pdo = new PDO($dsn, self::DB_USER, self::DB_PASS, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
            ]);
        } catch (PDOException $e) {
            // Ne pas exposer les détails de la connexion dans la réponse
            error_log('[GeoTracker] Erreur connexion BDD : ' . $e->getMessage());
            http_response_code(500);
            die(json_encode(['erreur' => 'Connexion à la base de données impossible']));
        }
    }

    public function getPdo(): PDO
    {
        return $this->pdo;
    }
}
