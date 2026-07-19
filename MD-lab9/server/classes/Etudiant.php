<?php
class Etudiant {
    private $id, $nom, $prenom, $ville, $sexe;

    function __construct($id, $nom, $prenom, $ville, $sexe) {
        $this->id = $id;
        $this->nom = $nom;
        $this->prenom = $prenom;
        $this->ville = $ville;
        $this->sexe = $sexe;
    }
    // Getters
    function getId() { return $this->id; }
    function getNom() { return $this->nom; }
    function getPrenom() { return $this->prenom; }
    function getVille() { return $this->ville; }
    function getSexe() { return $this->sexe; }
}
?>
