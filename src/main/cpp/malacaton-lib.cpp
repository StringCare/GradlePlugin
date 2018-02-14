#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL Java_CredentialUtils_getKey
    (JNIEnv *, jclass, jobject) {
    std::string hello = "Hello from C++";
    return "Hello from C++";
}
