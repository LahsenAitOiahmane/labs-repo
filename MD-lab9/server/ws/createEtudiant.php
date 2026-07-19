<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../service/EtudiantService.php';
    extract($_POST);
    $es = new EtudiantService();
    // Use dummy id since it's auto_increment
    $es->create(new Etudiant(0, $nom, $prenom, $ville, $sexe));

    header('Content-Type: application/json');
    echo json_encode($es->findAllApi());
}
?>
