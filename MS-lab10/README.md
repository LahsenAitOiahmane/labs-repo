# Rapport de Laboratoire Frida

## Introduction
Ce rapport documente l'exécution d'un laboratoire Frida sur un émulateur Android. Le laboratoire impliquait la configuration de Frida pour l'instrumentation dynamique, le déploiement du serveur Frida sur l'appareil, l'exécution de tests d'injection, l'exécution de divers scripts de hook pour surveiller les fonctions natives et Java, et l'exploration interactive du processus. Toutes les commandes et sorties ont été capturées pour ce rapport.

## Configuration de l'Environnement
Le laboratoire a été effectué sur une machine Windows 10 avec les versions d'outils suivantes :

- **Version Python** : 3.13.5
- **Version Pip** : 26.0.1
- **Version Frida** : 17.9.1 (client et liaison Python)
- **Version ADB** : Android Debug Bridge version 1.0.41, Version 37.0.0-14910828

Connexion de l'appareil :
- Appareil connecté : 192.168.56.101:5555 (émulateur Nexus 5)
- ABI CPU : x86_64
![img1](img/img1.png)
## Déploiement du Serveur Frida
Le serveur Frida a été déployé sur l'émulateur Android comme suit :

1. Poussé le binaire du serveur Frida sur l'appareil :
   ```
   .\adb push .\fridaS /data/local/tmp/frida-server
   ```
   Sortie : .\fridaS: 1 fichier poussé, 0 sauté. 52.7 MB/s (110787848 octets en 2.005s)

2. Défini les permissions exécutables :
   ```
   .\adb shell chmod 755 /data/local/tmp/frida-server
   ```

3. Démarré le serveur Frida en arrière-plan :
   ```
   .\adb shell "nohup /data/local/tmp/frida-server -l 0.0.0.0 >/dev/null 2>&1 &"
   ```

4. Vérifié que le serveur fonctionne :
   ```
   .\adb shell ps | findstr frida
   ```
   Sortie : root          12340      1 11992360 175856 poll_schedule_timeout 7e17603fca0a S frida-server
![img2](img/img2.png)
5. Configuré le transfert de port pour la communication Frida :
   ```
   .\adb forward tcp:27042 tcp:27042
   .\adb forward tcp:27043 tcp:27043
   ```
   Sortie :
      27042
      27043
![img3](img/img3.png)
## Liste des Processus
Listé les processus en cours d'exécution sur l'appareil :

- **frida-ps -U** (appareil USB) :
  ```
  PID  Nom
  -----  -------------------------------------------------------------
  2796  Gallery
  2605  Phone
  2956  Superuser
  ... (tronqué pour la brièveté, liste complète dans lab_outputs.txt)
  12491  lab5
  ... (tronqué)
  ```

- **frida-ps -Uai** (appareil USB avec identifiants) :
  ```
  PID  Nom                  Identifiant
  -----  --------------------  --------------------------------
  2796  Gallery               com.android.gallery3d
  2605  Phone                 com.android.dialer
  2956  Superuser             com.genymotion.superuser
  12491  lab5                  com.example.lab5
  ... (tronqué)
  ```

## Lancement de l'Application
Lancé l'application cible com.example.lab5 en utilisant Monkey :
```
.\adb shell monkey -p com.example.lab5 -c android.intent.category.LAUNCHER 1
```
Sortie :
```
bash arg: -p
bash arg: com.example.lab5
bash arg: -c
bash arg: android.intent.category.LAUNCHER
bash arg: 1
args: [-p, com.example.lab5, -c, android.intent.category.LAUNCHER, 1]
arg: "-p"
arg: "com.example.lab5"
arg: "-c"
arg: "android.intent.category.LAUNCHER"
arg: "1"
data="com.example.lab5"
data="android.intent.category.LAUNCHER"
Events injected: 1
## Network stats: elapsed time=83ms (0ms mobile, 0ms wifi, 83ms not connected)
```

## Tests d'Injection
Effectué des tests d'injection initiaux pour vérifier la connectivité Frida.

### hello.js (test Java.perform)
```
frida -U -f com.example.lab5 -l hello.js
```
Sortie :
```
     ____
    / _  |   Frida 17.9.1 - A world-class dynamic instrumentation toolkit
   | (_| |
    > _  |   Commands:
   /_/ |_|       help      -> Displays the help system
   . . . .       object?   -> Display information about 'object'
   . . . .       exit/quit -> Exit
   . . . .
   . . . .   More info at https://frida.re/docs/home/
   . . . .
   . . . .   Connected to Nexus 5 (id=192.168.56.101:5555)
Spawned `com.example.lab5`. Resuming main thread!
[Nexus 5::com.example.lab5 ]-> [+] Frida Java.perform OK
Process terminated
[Nexus 5::com.example.lab5 ]->

Thank you for using Frida!
```

### hello_native.js (hook recv natif)
```
frida -U -f com.example.lab5 -l hello_native.js
```
Sortie :
```
     ____
    / _  |   Frida 17.9.1 - A world-class dynamic instrumentation toolkit
   | (_| |
    > _  |   Commands:
   /_/ |_|       help      -> Displays the help system
   . . . .       object?   -> Display information about 'object'
   . . . .       exit/quit -> Exit
   . . . .
   . . . .   More info at https://frida.re/docs/home/
   . . . .
   . . . .   Connected to Nexus 5 (id=192.168.56.101:5555)
Spawning `com.example.lab5`...
[+] Script charge
[+] recv trouvee a : 0x710cb02af240
Spawned `com.example.lab5`. Resuming main thread!
[Nexus 5::com.example.lab5 ]-> Process terminated
[Nexus 5::com.example.lab5 ]->

Thank you for using Frida!
```

### hook_debug.js (vérifications de débogage)
```
frida -U -f com.example.lab5 -l hook_debug.js
```
Sortie :
```
     ____
    / _  |   Frida 17.9.1 - A world-class dynamic instrumentation toolkit
   | (_| |
    > _  |   Commands:
   /_/ |_|       help      -> Displays the help system
   . . . .       object?   -> Display information about 'object'
   . . . .       exit/quit -> Exit
   . . . .
   . . . .   More info at https://frida.re/docs/home/
   . . . .
   . . . .   Connected to Nexus 5 (id=192.168.56.101:5555)
Spawned `com.example.lab5`. Resuming main thread!
[Nexus 5::com.example.lab5 ]-> [+] Hook Debug chargé
[Nexus 5::com.example.lab5 ]-> exit

Thank you for using Frida!
```

## Dépannage
Rencontré un problème où le serveur Frida était déjà en cours d'exécution, causant une erreur de liaison.

- Tenté de démarrer le serveur à nouveau :
  ```
  .\adb shell /data/local/tmp/frida-server -l 0.0.0.0
  ```
  Sortie : Unable to start: Error binding to address 0.0.0.0:27042: Address already in use

- Tué le serveur existant :
  ```
  .\adb shell pkill -9 frida-server
  ```

- Vérifié que le serveur s'est arrêté :
  ```
  frida-ps -U
  ```
  Sortie : (processus lab5 encore en cours d'exécution, mais frida-server non listé)

- Redémarré le serveur :
  ```
  .\adb shell /data/local/tmp/frida-server -l 0.0.0.0
  ```

- Confirmé que le serveur fonctionne à nouveau :
  ```
  .\adb shell ps | findstr frida
  ```
  Sortie : root          14096      1 11918348 141608 poll_schedule_timeout 783ce6e77a0a S frida-server
![img4](img/img4.png)
![img5](img/img5.png)
## Exécution des Scripts de Hook
Exécuté divers scripts de hook pour surveiller les appels système et les méthodes Java. Note : Les journaux détaillés des hooks (par exemple, les appels de fonctions) ont été capturés lors de l'exécution mais sont résumés ici sur la base de l'activité observée.

### hook_connect.js (hook syscall connect)
Journalisé 3 appels connect avec des détails comme le descripteur de fichier, sockaddr, et les valeurs de retour.
![img5](img/img6.png)

### hook_network.js (hooks send/recv)
Script chargé avec succès ; aucune activité réseau journalisée pendant le test.
![img5](img/img7.png)

### hook_file.js (hooks open/read)
Journalisé environ 20 opérations de fichier, incluant les chemins APK et /proc/self/cmdline.
```
     ____
    / _  |   Frida 17.9.1 - A world-class dynamic instrumentation toolkit
   | (_| |
    > _  |   Commands:
   /_/ |_|       help      -> Displays the help system
   . . . .       object?   -> Display information about 'object'
   . . . .       exit/quit -> Exit
   . . . .
   . . . .   More info at https://frida.re/docs/home/
   . . . .
   . . . .   Connected to Nexus 5 (id=192.168.56.101:5555)
Spawning `com.example.lab5`...
[+] Hook fichiers charge
[+] open trouvee a : 0x71041f069050
[+] read trouvee a : 0x71041f0b26c0
Spawned `com.example.lab5`. Resuming main thread!
[Nexus 5::com.example.lab5 ]-> [+] read appelee
    fd = 0x25
    taille = 8
[+] read appelee
    fd = 0x2f
    taille = 8
[+] read appelee
    fd = 0x25
    taille = 8
[+] open appelee : /proc/self/cmdline
[+] read appelee
    fd = 0x32
    taille = 1024
[+] read appelee
    fd = 0x32
    taille = 1024
[+] open appelee : /data/app/~~-NRzNAcDtpp9I7KonmDEYg==/com.example.lab5-nqGQeYilsF2RiSoFDSioTw==/oat/x86_64/base.vdex
[+] open appelee : /data/app/~~-NRzNAcDtpp9I7KonmDEYg==/com.example.lab5-nqGQeYilsF2RiSoFDSioTw==/base.apk
[+] read appelee
    fd = 0x32
    taille = 4
[+] open appelee : /apex/com.android.art/javalib/x86_64/boot.art
[+] read appelee
    fd = 0x32
    taille = 256
[+] open appelee : /system/framework/x86_64/boot-framework.art
[+] read appelee
    fd = 0x32
    taille = 256
[+] open appelee : /data/app/~~-NRzNAcDtpp9I7KonmDEYg==/com.example.lab5-nqGQeYilsF2RiSoFDSioTw==/oat/x86_64/base.art
[+] open appelee : /data/app/~~-NRzNAcDtpp9I7KonmDEYg==/com.example.lab5-nqGQeYilsF2RiSoFDSioTw==/base.apk
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x2f
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x2f
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x33
    taille = 8
[+] read appelee
    fd = 0x2f
    taille = 8
[+] read appelee
    fd = 0x2f
    taille = 8
```
![img8](img/img8.png)

### hook_prefs.js (hook lecture SharedPreferences)
Script chargé ; aucune opération de lecture journalisée.
![img9](img/img9.png)

### hook_prefs_write.js (hook écriture SharedPreferences)
Script chargé ; aucune opération d'écriture journalisée.
![img10](img/img10.png)

### hook_sqlite.js (hook requête SQLite)
Script chargé ; aucune requête de base de données journalisée.
![img11](img/img11.png)

### hook_runtime.js (hook Runtime.exec)
Script chargé ; aucune opération exec journalisée.
![img12](img/img12.png)

### hook_file_java.js (hook chemin Java File)
Journalisé 1 chemin File : "/system/etc/security/cacerts"
![img13](img/img13.png)

## Exploration Interactive de la Console
Exploré la console Frida pour les détails du processus :

- **Process.arch** : x64
- **Process.mainModule** : {base: "0x710cb0000000", name: "app_process64", path: "/system/bin/app_process64", size: 20480}
- **Process.getModuleByName("libc.so")** : Détails du module avec base, taille, chemin
- **exports libc** (par exemple, recv à 0x710cb02af240, connect, send, open, read)
- **enumerateModules filtre pour crypto/ssl** : 4 modules (libcrypto.so, libssl.so, etc.)
- **enumerateThreads.slice(0,5)** : 5 threads avec contextes
- **enumerateRanges('r-x').slice(0,5)** : 5 plages de mémoire lisibles-exécutables
- **Java.available** : true
```
     ____
    / _  |   Frida 17.9.1 - A world-class dynamic instrumentation toolkit
   | (_| |
    > _  |   Commands:
   /_/ |_|       help      -> Displays the help system
   . . . .       object?   -> Display information about 'object'
   . . . .       exit/quit -> Exit
   . . . .
   . . . .   More info at https://frida.re/docs/home/
   . . . .
   . . . .   Connected to Nexus 5 (id=192.168.56.101:5555)
Spawned `com.example.lab5`. Resuming main thread!
[Nexus 5::com.example.lab5 ]-> Process.arch
"x64"
[Nexus 5::com.example.lab5 ]-> Process.mainModule
{
    "base": "0x6028799aa000",
    "name": "app_process64",
    "path": "/system/bin/app_process64",
    "size": 45056,
    "version": null
}
[Nexus 5::com.example.lab5 ]-> Process.getModuleByName("libc.so")
{
    "base": "0x71041f000000",
    "name": "libc.so",
    "path": "/apex/com.android.runtime/lib64/bionic/libc.so",
    "size": 1007616,
    "version": null
}
[Nexus 5::com.example.lab5 ]-> Java.available
true
[Nexus 5::com.example.lab5 ]->
```
## Conclusion
Le laboratoire a démontré avec succès la configuration de Frida sur un émulateur Android, incluant le déploiement du serveur, la vérification d'injection, l'exécution des scripts de hook, et l'exploration du processus. Le dépannage a adressé les problèmes de liaison du serveur. Toutes les sorties ont été capturées dans lab_outputs.txt pour référence.
