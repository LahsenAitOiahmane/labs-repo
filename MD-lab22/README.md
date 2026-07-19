# LAB 22: Développement Android avec JNI (Java Native Interface)

## Présentation
L'objectif de ce laboratoire est de construire une application Android nommée `JNIDemo` capable de communiquer avec du code natif C++ via JNI. L'application illustre comment appeler plusieurs fonctions natives, envoyer des paramètres Java vers C++, récupérer des résultats calculés côté natif, et apprendre à gérer correctement le chargement de la bibliothèque partagée `.so`.

Ce projet démontre l'utilisation de JNI dans les applications modernes, pertinente pour les calculs intensifs, la réutilisation de bibliothèques C/C++, et la protection logique.

## Fonctionnalités
L’application `JNIDemo` réalise les quatres démonstrations suivantes :
1. **Hello World :** appel d’une fonction native simple `helloFromJNI()`.
2. **Factoriel :** calcul natif d’un factoriel (ex. n=10) avec un strict contrôle d’erreur de dépassement de capacité (overflow).
3. **Inversion de Chaîne :** l'inversion d’une chaîne de caractères `String` envoyée depuis Java, modifiée en C++ et renvoyée modifiée.
4. **Somme de Tableau :** traitement d’un tableau `int[]` envoyé à la méthode `sumArray()` au natif, puis la récupération de la somme.

## Architecture

Le flux d'exécution standard de l'application est :
1. **Java / MainActivity** : Appelle les méthodes annotées avec le mot-clé `native`.
2. **Android** : Charge la librairie `libnative-lib.so` (via `System.loadLibrary("native-lib")`).
3. **JNI** : Transmet l'appel au code C++ écrit dans `native-lib.cpp`.
4. **Code C++** : Exécute le traitement demandé.
5. **JNI** : Convertit et renvoie le résultat vers l'environnement Java.
6. **Interface Android** : Met à jour dynamiquement les vues (TextViews) avec les résultats.

## Prérequis et Installation
- Android Studio installé avec le SDK Android configuré.
- Les outils externes (NDK, CMake, et LLDB) téléchargés depuis le SDK Manager.

## Compilation et Exécution
1. Ouvrez le projet dans Android Studio.
2. Laissez Gradle se synchroniser. Le script CMakeLists configure la compilation C++ en mode SHARED pour créer `libnative-lib.so`.
3. Cliquez sur **Run** (ou `Shift+F10`) pour compiler et installer l'application sur votre émulateur.

## Implémentations Principales

La mise en place de JNI nécessite 3 composants principaux : le code Java avec l'annotation `native`, l'exportation correcte de chaque fonction en C++, et la liaison avec CMakeLists.

### 1. Code Java (MainActivity.java)
```java
public native String helloFromJNI();
public native int factorial(int n);
public native String reverseString(String s);
public native int sumArray(int[] values);

// Chargement de la bibliothèque
static {
    System.loadLibrary("native-lib");
}
```

### 2. Code C++ (native-lib.cpp)
```cpp
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_jnidemo_MainActivity_helloFromJNI(JNIEnv* env, jobject) {
    return env->NewStringUTF("Hello from C++ via JNI !");
}
```

### 3. CMakeLists.txt
```cmake
cmake_minimum_required(VERSION 3.22.1)
project("jnidemo")

add_library(native-lib SHARED native-lib.cpp)
find_library(log-lib log)
target_link_libraries(native-lib ${log-lib})
```

## Captures d'écrans demandées
> Si nécessaire, vous pouvez intégrer une capture d'écran du logcat (qui confirme l'exécution de: `__android_log_print` / `LOGI` depuis C++) ou de l'application lancée sur l'appareil.

![screenshot](img/screen.jpeg)