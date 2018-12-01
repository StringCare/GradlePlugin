#include <jni.h>
#include <stdio.h>

JNIEXPORT jstring JNICALL Java_CredentialUtils_sign(JNIEnv *env, jclass clazz, jstring key) {
printf("Hello World!\n");
return key;
}