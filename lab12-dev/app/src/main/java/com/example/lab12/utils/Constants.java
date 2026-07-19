package com.example.lab12.utils;

public class Constants {
    // Modifier cette IP pour correspondre à l'adresse locale de votre serveur WAMP/XAMPP
    // 10.0.2.2 est l'adresse par défaut pour accéder à localhost depuis l'émulateur Android
    public static final String SERVER_IP = "10.0.2.2";
    
    // Le chemin dépend du nom de votre dossier dans htdocs/www
    public static final String BASE_URL = "http://" + SERVER_IP + "/lab12/backend/";
    
    public static final String API_ADD_LOCATION = BASE_URL + "api_add_location.php";
    public static final String API_GET_LOCATIONS = BASE_URL + "api_get_locations.php";
}
