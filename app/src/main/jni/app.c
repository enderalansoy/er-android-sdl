#include <jni.h>

JNIEXPORT jobjectArray JNICALL Java_com_easyroute_constant_Constant_getConstants(JNIEnv *env, jobject jobj) {

    char *constants[] = {"http://95.0.225.138/WS/Karayollari.asmx",
                         "http://www.mobilion.com.tr/WS/",
                         "OtoyolRetrievePrices"};
    jstring str;
    jobjectArray day = 0;
    jsize len = 3;
    int i;

    day = (*env)->NewObjectArray(env, len, (*env)->FindClass(env, "java/lang/String"), 0);

    for (i = 0; i < 3; i++) {
        str = (*env)->NewStringUTF(env, constants[i]);
        (*env)->SetObjectArrayElement(env, day, i, str);
    }

    return day;
}