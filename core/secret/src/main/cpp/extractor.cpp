#include <jni.h>
#include <string>
//#include "http/httplib.h"

//static std::string getIp() {
//    httplib::Client httpClient("https://api.ipify.org");
//    auto res = httpClient.Get("/");
//    if (res && res->status == 200) {
//        return res->body;
//    } else {
//        return "jni error: " + res->reason;
//    }
//}

extern "C" JNIEXPORT jstring JNICALL Java_n7_ad2_nativesecret_NativeSecretExtractor_printHelloWorld(
        JNIEnv *env,
        jobject thiz
) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
