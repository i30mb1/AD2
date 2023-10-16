#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL Java_n7_ad2_nativesecret_NativeSecretExtractor_printHelloWorld(
        JNIEnv *env,
        jobject thiz
) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
