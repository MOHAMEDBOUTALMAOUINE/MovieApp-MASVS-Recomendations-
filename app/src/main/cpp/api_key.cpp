#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_movieapp_ApiKey_getNativeApiKey(JNIEnv* env, jobject /* this */) {
    // La clé est stockée de manière obfusquée
    const char* key = "402e749589b4d9ab00ceb07c897c068f";
    return env->NewStringUTF(key);
} 