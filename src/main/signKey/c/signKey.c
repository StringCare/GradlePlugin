#include <jni.h>
#include <stdio.h>

JNIEXPORT void JNICALL Java_CredentialUtils_print(JNIEnv *env, jclass clazz)
{
printf("Hello World!\n");
return;
}