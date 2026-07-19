<?php
include_once '../service/EtudiantService.php';
$es = new EtudiantService();
header('Content-Type: application/json');
echo json_encode($es->findAllApi());
?>
