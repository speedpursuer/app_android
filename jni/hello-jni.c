//
// Created by 邢磊 on 16/6/2.
//

#include <string.h>
#include <jni.h>

jstring Java_com_lee_cliplay_HelloJNI_dbStringFromJNI( JNIEnv* env, jobject thiz ) {
    return (*env)->NewStringUTF(env, "http://app_viewer:Cliplay1234@121.40.197.226:4984/");
}

jstring Java_com_lee_cliplay_HelloJNI_dbFileFromJNI( JNIEnv* env, jobject thiz ) {
    return (*env)->NewStringUTF(env, "ionic.min.css");
}

jstring Java_com_lee_cliplay_HelloJNI_uidFromJNI( JNIEnv* env, jobject thiz ) {
    return (*env)->NewStringUTF(env, "app_viewer");
}

jstring Java_com_lee_cliplay_HelloJNI_pwdFromJNI( JNIEnv* env, jobject thiz ) {
    return (*env)->NewStringUTF(env, "Cliplay1234");
}

jstring Java_com_lee_cliplay_HelloJNI_keyFromJNI( JNIEnv* env, jobject thiz ) {
    return (*env)->NewStringUTF(env, "jordankobelebron");
}
