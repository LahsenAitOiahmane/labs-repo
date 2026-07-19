# Application Galerie des Célébrités (Stars Gallery)

## Description du Projet
Ce projet est une application Android "Stars Gallery" développée dans le cadre d'un laboratoire ou d'un défi technique. L'application affiche une liste de célébrités avec leur image, leur nom et une note (rating). 

Plusieurs améliorations ont été apportées à l'application initiale pour la rendre fonctionnelle et moderne :
1. **Correction du Bug de Démarrage (Crash)** : Le dossier `assets` contenant les images des célébrités a été correctement replacé dans `app/src/main/assets` afin d'éviter une exception (`ClassCastException`) liée à l'impossibilité de charger les images via Glide lors du démarrage.
2. **Modernisation de l'Interface Utilisateur (UI)** :
   - Mise en place d'un thème sombre moderne (Dark Theme) avec une nouvelle palette de couleurs fluides et esthétiques.
   - Utilisation de la police personnalisée **Inter** pour une typographie épurée et professionnelle.
   - Refonte des dispositions XML (`activity_list`, `star_item`, `activity_splash`, etc.) en utilisant `MaterialCardView` avec des coins arrondis (20dp) et des marges intérieures optimisées.
3. **Refactoring et Propreté du Code** :
   - Suppression du code obsolète ou dupliqué (telles que les classes `Star` et `StarService`).
   - Renommage des variables pour les rendre claires et lisibles pour les développeurs (par exemple, `mainAdapter` vers `celebrityAdapter`, `mContext` vers `context`).

## Démonstration Vidéo
Vous pouvez consulter la vidéo de démonstration complète montrant le lancement de l'application, l'animation Splash initiale, et l'affichage de la galerie via le lien ci-dessous :

**[demo video link](https://drive.google.com/file/d/1S_ENlhwBqAczJDIAhlRhgU1ojrqQraUG/view?usp=sharing)**

## Instructions d'Installation
1. Cloner ce dépôt ou télécharger le code source.
2. Ouvrir le projet dans **Android Studio**.
3. Rafraîchir/Synchroniser le projet avec Gradle (`Sync Project with Gradle Files`).
4. Lancer l'application sur un émulateur Android ou un appareil physique connecté.
