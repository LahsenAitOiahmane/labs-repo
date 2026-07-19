# Rapport de TP : Comprendre le Rooting et ses Impacts (LAB-2)

## 📋 Fiche Périmètre
- **Application cible :** Application de test (`app-debug.apk`)
- **Environnement (Support) :** Émulateur Android (AVD) — API récente
- **Objectif :** Comprendre le processus de rooting, ses impacts sur la sécurité et le système, et identifier les mécanismes de protection (Verity, Verified Boot).
- **Données :** Fictives uniquement (Environnement de test).
- **Réseau :** Isolé pour ce test (Réseau de test).

---

## 📝 Concepts Clés Assimilés

- **Le Rooting :** C'est l'action d'obtenir un accès super-utilisateur (root) sur le système d'exploitation Android. Cela permet de modifier les protections natives et la chaîne de confiance du système, accordant des privilèges totaux. En laboratoire, c'est indispensable pour explorer le fonctionnement interne, mais dans la vraie vie, cela expose le téléphone à d'importants risques de sécurité (d'où la nécessité d'un isolement réseau, traçabilité et reset obligatoires).
- **Android Security (Sandboxing & Permissions) :** Les applications Android fonctionnent de base dans un environnement isolé (sandbox). Obtenir un accès root permet de transgresser ces limites d'isolation.
- **Verified Boot & AVB :**
  - **Verified Boot** (démarrage vérifié) garantit que le système lancé est intègre et n'a pas été modifié par un attaquant ou un programme malveillant au niveau de ses partitions de base. 
  - Il repose sur une **chaîne de confiance** (chain of trust) : une série d'étapes de démarrage où chaque étape vérifie cryptographiquement l'authenticité et l'intégrité du composant suivant avant de l'exécuter. L'intégrité au démarrage est critique, car si le premier rempart est corrompu, toutes les autres sécurités le seront aussi.
  - **Android Verified Boot (AVB)** est la version moderne qui inclut notamment la vérification de l'intégrité et une forte protection anti-rollback (empêchant l'installation de versions vulnérables plus anciennes du système).

---

## 💻 Commandes Réalisées et Leurs Résultats

| Commande exécutée | Explication / Résultat Compris |
| :--- | :--- |
| `adb devices` | **Vérification de la connexion de l'AVD** : Liste les appareils connectés. Mon AVD doit apparaître avec le statut `device` pour interagir avec. |
| `adb root` | **Élévation de privilèges (démon)** : Redémarre le démon ADB sur l'émulateur avec les privilèges `root`. Obligatoire pour manipuler les partitions protégées. |
| `adb remount` | **Montage des partitions en R/W** : Permet de remonter la partition `/system` en lecture/écriture, rendant possible la modification de fichiers systèmes vitaux. |
| `adb shell id` | **Vérification d'identité** : Le terminal renvoie `uid=0(root)`, ce qui confirme de manière factuelle que nous sommes devenus super-administrateur. |
| `adb shell getprop ro.boot.verifiedbootstate` | **Vérification du Verified Boot** : Interroge la machine sur l'intégrité du boot process. En temps normal on attend "green" (sain). Si le bootloader a été modifié ou déverrouillé on aura "yellow" ou "orange". |
| `adb shell getprop ro.boot.veritymode` | **Statut Verity** : Affiche l'état du dm-verity, responsable d'assurer que l'image système montée n'a pas été compromise. |
| `adb disable-verity` | **Mode permissif** : Désactive la routine de vérification d'intégrité `verity` pour nous permettre de faire de l'analyse dynamique intrusives sans déclencher le blocage d'OS (nécessite ensuite un reboot). |
| `adb logcat -d \| tail -n 200 > log.txt` | **Traçabilité** : Extrait les 200 dernières lignes de logs dans un fichier texte afin de sourcer nos audits et prouver le bon déroulé du TP. |

---

## 📱 Scénarios d'utilisation de l'Application (Testée)

Suite à l'installation de l'application (`adb install app-debug.apk`), voici les scénarios de base modélisés pour s'assurer du fonctionnement sans bug avant de commencer les analyses approfondies :
1. **Ouverture de l'écran d'accueil** (Vérification de l'interface principale).
2. **Recherche d'un item via la barre de recherche** (Validation des entrées utilisateur).
3. **Ouverture de la fiche de détail du produit/profil** (Validation des interactions d'UI).
*(Les captures d'écran des étapes réussies prouvant la bonne exécution des scénarios seront placées dans le dossier du LAB).*

---

## ⚠️ Matrice des Risques lié au Rooting

| Risques encourus | Mesures défensives appliquées au LAB |
| :--- | :--- |
| **1. Intégrité non garantie** (Conclusions faussées). | Journal de configuration détaillé (garantissant la reproductibilité). |
| **2. Surface d'attaque accrue** si l'appareil sort du labo. | Device/Virtual Device dédié **exclusivement** aux tests de sécurité. |
| **3. Données sensibles exposées** (Perte de confidentialité). | Ne manipuler que des **données fictives** sur cet environnement de test. |
| **4. Instabilité système** amenant des tests non-reproductibles. | Faire des captures (snapshots) et un "wipe" (remise à zéro) en fin de séance. |
| **5. Mélange compte perso/test** (Fuite possible de PII). | **Aucun compte personnel toléré**, utilisation d'identifiants de test uniquement. |
| **6. Mauvais nettoyage** laissant des traces de données sensibles. | Suppression totale de l'AVD en fin de séance, procédure encadrée rigoureusement. |
| **7. Réseau non isolé** engendrant un risque de rebond/fuite. | Connectivité réseau strictement cloisonnée et isolée. |
| **8. Traçabilité insuffisante** (Méthodologie inexploitable). | Prise de captures et horodatage pour chaque ligne de commande critique. |

---

## 📚 Analyse OWASP (MASVS & MASTG)

### Normes MASVS (2 Exigences vérifiables avec un accès root)
- **STORAGE-1 :** Les données hautement sensibles telles que les *clés API* ou *tokens de connexion* doivent être sauvegardées chiffrées/hachées de façon robuste et non en texte clair.
- **NETWORK-1 :** Les communications client-serveur de l'application mobile doivent exploiter du TLS sécurisé et vérifier rigoureusement les certificats (pour éviter le MiTM).

### Tests MASTG (2 Idées de tests liés)
1. **Audit des préférences locales (`shared_prefs`) :** Avec l'accès root obtenu, on peut naviguer dans l'arborescence `/data/data/[package_name]/shared_prefs/` pour s'assurer purement qu'aucun token en clair n'apparaitraient à un attaquant qui extrairait le volume physique.
2. **Audit des journaux (Logcat) :** Lancer et laisser `adb logcat` afficher le flux d'événements Android pendant la réalisation des étapes du scénario (Exemple: Créer un compte, taper un mdp). Si l'application fuite le mdp en console (Debug leak), alors on signale immédiatement une vulnérabilité.

---

## 🧹 Remise à zéro (Fin de séance)

Preuve et confirmation que l'environnement temporaire est désinfecté. 
Pour recréer des conditions saines après le laboratoire, toutes les traces sont effacées :
```bash
adb emu avd stop
adb emu avd wipe-data
```
**Conclusion du Labo :** Lors du prochain démarrage de cet AVD, le terminal d'accueil classique sans aucune trace d'application tierce ou root permanent se lancera, attestant du respect des règles du protocole défensif.
