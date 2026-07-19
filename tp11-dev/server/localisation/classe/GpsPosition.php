<?php
/**
 * GpsPosition — représente un enregistrement de position GPS.
 *
 * Contient les quatre données envoyées par l'application mobile :
 * latitude, longitude, horodatage et identifiant de l'appareil.
 */
class GpsPosition
{
    private ?int    $id;
    private float   $latitude;
    private float   $longitude;
    private string  $datePosition;  // format "YYYY-MM-DD HH:MM:SS"
    private string  $imei;

    public function __construct(
        ?int   $id,
        float  $latitude,
        float  $longitude,
        string $datePosition,
        string $imei
    ) {
        $this->id           = $id;
        $this->latitude     = $latitude;
        $this->longitude    = $longitude;
        $this->datePosition = $datePosition;
        $this->imei         = $imei;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public function getId(): ?int         { return $this->id;           }
    public function getLatitude(): float  { return $this->latitude;     }
    public function getLongitude(): float { return $this->longitude;    }
    public function getDatePosition(): string { return $this->datePosition; }
    public function getImei(): string     { return $this->imei;         }

    // ── Setters ──────────────────────────────────────────────────────────────

    public function setId(?int $id): void               { $this->id           = $id;           }
    public function setLatitude(float $lat): void       { $this->latitude     = $lat;          }
    public function setLongitude(float $lng): void      { $this->longitude    = $lng;          }
    public function setDatePosition(string $d): void    { $this->datePosition = $d;            }
    public function setImei(string $imei): void         { $this->imei         = $imei;         }

    // ── Debug ─────────────────────────────────────────────────────────────────

    public function toArray(): array
    {
        return [
            'id'            => $this->id,
            'latitude'      => $this->latitude,
            'longitude'     => $this->longitude,
            'date_position' => $this->datePosition,
            'imei'          => $this->imei,
        ];
    }
}
