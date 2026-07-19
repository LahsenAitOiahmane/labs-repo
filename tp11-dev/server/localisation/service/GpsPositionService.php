<?php
require_once __DIR__ . '/../dao/IRepository.php';
require_once __DIR__ . '/../classe/GpsPosition.php';
require_once __DIR__ . '/../connexion/DbConnexion.php';

/**
 * GpsPositionService — implémentation du repository pour la table `position`.
 *
 * Toutes les requêtes SQL utilisent des paramètres liés (prepared statements)
 * pour prévenir les injections SQL.
 */
class GpsPositionService implements IRepository
{
    private PDO $pdo;

    public function __construct()
    {
        $db        = new DbConnexion();
        $this->pdo = $db->getPdo();
    }

    // ── Création ─────────────────────────────────────────────────────────────

    public function create($position): bool
    {
        $sql = '
            INSERT INTO position (latitude, longitude, date_position, imei)
            VALUES (:latitude, :longitude, :date_position, :imei)
        ';

        $stmt = $this->pdo->prepare($sql);

        return $stmt->execute([
            ':latitude'      => $position->getLatitude(),
            ':longitude'     => $position->getLongitude(),
            ':date_position' => $position->getDatePosition(),
            ':imei'          => $position->getImei(),
        ]);
    }

    // ── Mise à jour ───────────────────────────────────────────────────────────

    public function update($position): bool
    {
        $sql = '
            UPDATE position
               SET latitude      = :latitude,
                   longitude     = :longitude,
                   date_position = :date_position,
                   imei          = :imei
             WHERE id = :id
        ';

        $stmt = $this->pdo->prepare($sql);

        return $stmt->execute([
            ':latitude'      => $position->getLatitude(),
            ':longitude'     => $position->getLongitude(),
            ':date_position' => $position->getDatePosition(),
            ':imei'          => $position->getImei(),
            ':id'            => $position->getId(),
        ]);
    }

    // ── Suppression ───────────────────────────────────────────────────────────

    public function delete($position): bool
    {
        $stmt = $this->pdo->prepare('DELETE FROM position WHERE id = :id');
        return $stmt->execute([':id' => $position->getId()]);
    }

    // ── Recherche par ID ──────────────────────────────────────────────────────

    public function findById(int $id): ?GpsPosition
    {
        $stmt = $this->pdo->prepare(
            'SELECT * FROM position WHERE id = :id LIMIT 1'
        );
        $stmt->execute([':id' => $id]);
        $row = $stmt->fetch();

        if (!$row) return null;

        return new GpsPosition(
            (int) $row['id'],
            (float) $row['latitude'],
            (float) $row['longitude'],
            $row['date_position'],
            $row['imei']
        );
    }

    // ── Toutes les positions ──────────────────────────────────────────────────

    public function findAll(): array
    {
        $stmt = $this->pdo->query(
            'SELECT * FROM position ORDER BY date_position DESC'
        );
        $results = [];

        while ($row = $stmt->fetch()) {
            $results[] = new GpsPosition(
                (int) $row['id'],
                (float) $row['latitude'],
                (float) $row['longitude'],
                $row['date_position'],
                $row['imei']
            );
        }

        return $results;
    }
}
