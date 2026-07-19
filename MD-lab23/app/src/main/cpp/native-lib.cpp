#include <jni.h>
#include <string>
#include <cstring>
#include <cstdio>
#include <cstdlib>
#include <algorithm>
#include <climits>
#include <android/log.h>
#include <sys/ptrace.h>
#include <unistd.h>

#define LOG_TAG "ANTI_DEBUG"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// --------------------------------------------------
// Contrôle 1 : tentative de détection de traçage
// --------------------------------------------------
static bool isBeingTraced() {
    long result = ptrace(PTRACE_TRACEME, 0, 0, 0);
    if (result == -1) {
        LOGE("Etat suspect : trace/debug detecte");
        return true;
    }
    LOGI("Aucun trace/debug detecte via ptrace");
    return false;
}

// --------------------------------------------------
// Contrôle 2 : recherche de signatures dans /proc/self/maps
// --------------------------------------------------
static bool containsSuspiciousLibraryNames() {
    FILE* maps = fopen("/proc/self/maps", "r");
    if (!maps) {
        LOGW("Impossible d'ouvrir /proc/self/maps");
        return false;
    }

    char line[512];

    while (fgets(line, sizeof(line), maps)) {
        if (strstr(line, "frida") ||
            strstr(line, "xposed") ||
            strstr(line, "libfrida") ||
            strstr(line, "gdbserver") ||
            strstr(line, "libgdb") ||
            strstr(line, "magisk")) {
            LOGE("Signature suspecte trouvee dans maps : %s", line);
            fclose(maps);
            return true;
        }
    }

    fclose(maps);
    LOGI("Aucune signature suspecte trouvee dans /proc/self/maps");
    return false;
}

// --------------------------------------------------
// Contrôle global appelé depuis Java
// --------------------------------------------------
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_lab23_MainActivity_isDebugDetected(
        JNIEnv* env,
        jobject /* this */) {

    bool traced = isBeingTraced();
    bool suspiciousMaps = containsSuspiciousLibraryNames();

    if (traced || suspiciousMaps) {
        LOGE("Etat de securite : DEBUG / INSTRUMENTATION detecte");
        return JNI_TRUE;
    }

    LOGI("Etat de securite : OK");
    return JNI_FALSE;
}

// --------------------------------------------------
// Fonctions JNI du laboratoire précédent (Lab 22)
// --------------------------------------------------

// 1) Hello World natif
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_lab23_MainActivity_helloFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    LOGI("Appel de helloFromJNI depuis le natif");
    return env->NewStringUTF("Hello from C++ via JNI !");
}

// 2) Factoriel avec gestion d'erreur
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_lab23_MainActivity_factorial(
        JNIEnv* env,
        jobject /* this */,
        jint n) {

    if (n < 0) {
        LOGE("Erreur : n negatif");
        return -1;
    }

    long long fact = 1;
    for (int i = 1; i <= n; i++) {
        fact *= i;
        if (fact > INT_MAX) {
            LOGE("Overflow detecte pour n=%d", n);
            return -2;
        }
    }

    LOGI("Factoriel de %d calcule en natif = %lld", n, fact);
    return static_cast<jint>(fact);
}

// 3) Inversion d'une chaine Java -> C++ -> Java
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_lab23_MainActivity_reverseString(
        JNIEnv* env,
        jobject /* this */,
        jstring javaString) {

    if (javaString == nullptr) {
        LOGE("Chaine nulle recue");
        return env->NewStringUTF("Erreur : chaine nulle");
    }

    const char* chars = env->GetStringUTFChars(javaString, nullptr);
    if (chars == nullptr) {
        LOGE("Impossible de lire la chaine Java");
        return env->NewStringUTF("Erreur JNI");
    }

    std::string s(chars);
    env->ReleaseStringUTFChars(javaString, chars);

    std::reverse(s.begin(), s.end());

    LOGI("String inversee = %s", s.c_str());
    return env->NewStringUTF(s.c_str());
}

// 4) Somme d'un tableau int[]
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_lab23_MainActivity_sumArray(
        JNIEnv* env,
        jobject /* this */,
        jintArray array) {

    if (array == nullptr) {
        LOGE("Tableau nul");
        return -1;
    }

    jsize len = env->GetArrayLength(array);
    jint* elements = env->GetIntArrayElements(array, nullptr);

    if (elements == nullptr) {
        LOGE("Impossible d'acceder aux elements du tableau");
        return -2;
    }

    long long sum = 0;
    for (jsize i = 0; i < len; i++) {
        sum += elements[i];
    }

    env->ReleaseIntArrayElements(array, elements, 0);

    if (sum > INT_MAX) {
        LOGE("Overflow sur la somme");
        return -3;
    }

    LOGI("Somme du tableau = %lld", sum);
    return static_cast<jint>(sum);
}
