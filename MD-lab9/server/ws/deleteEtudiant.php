<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../service/EtudiantService.php';
    extract($_POST);
    $es = new EtudiantService();
    $etudiant = $es->findById($id);
    if ($etudiant) {
        $es->delete($etudiant);
    }
    header('Content-Type: application/json');
    echo json_encode(["status" => "success"]);
}
?>
