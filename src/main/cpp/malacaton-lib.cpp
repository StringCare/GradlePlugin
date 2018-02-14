#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL Java_CredentialUtils_getKey(JNIEnv * env, jclass, jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
