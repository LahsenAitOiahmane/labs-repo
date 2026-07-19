<?php
/**
 * PhoneVault API — EntryRepository
 * Gère toutes les opérations CRUD sur la table `phonebook_entry`.
 */
require_once __DIR__ . '/../config/Database.php';

class EntryRepository {
    private PDO    $db;
    private string $table = "phonebook_entry";

    public function __construct() {
        $database  = new Database();
        $this->db  = $database->connect();
    }

    /**
     * Insère un nouveau contact. Ignore si le numéro existe déjà (ON DUPLICATE KEY).
     */
    public function save(string $displayName, string $phoneNumber, string $origin = "android"): bool {
        $cleanPhone = preg_replace('/\s+/', '', $phoneNumber); // supprime les espaces

        $sql  = "INSERT INTO {$this->table} (display_name, phone_number, origin)
                 VALUES (:display_name, :phone_number, :origin)
                 ON DUPLICATE KEY UPDATE display_name = VALUES(display_name), origin = VALUES(origin)";
        $stmt = $this->db->prepare($sql);

        return $stmt->execute([
            ':display_name'  => $displayName,
            ':phone_number'  => $cleanPhone,
            ':origin'        => $origin,
        ]);
    }

    /**
     * Retourne tous les contacts, triés alphabétiquement.
     */
    public function findAll(): array {
        $sql  = "SELECT * FROM {$this->table} ORDER BY display_name ASC";
        $stmt = $this->db->prepare($sql);
        $stmt->execute();
        return $stmt->fetchAll();
    }

    /**
     * Recherche par nom ou numéro (insensible à la casse).
     */
    public function findByKeyword(string $keyword): array {
        $pattern = '%' . $keyword . '%';
        $sql  = "SELECT * FROM {$this->table}
                 WHERE display_name LIKE :kw OR phone_number LIKE :kw
                 ORDER BY display_name ASC";
        $stmt = $this->db->prepare($sql);
        $stmt->execute([':kw' => $pattern]);
        return $stmt->fetchAll();
    }

    /**
     * Supprime un contact par son id.
     */
    public function remove(int $id): bool {
        $sql  = "DELETE FROM {$this->table} WHERE id = :id";
        $stmt = $this->db->prepare($sql);
        return $stmt->execute([':id' => $id]);
    }
}
