package com.lee.cliplay;

/**
 * Created by xl on 16/6/2.
 */
public class HelloJNI {

    static {
        System.loadLibrary("hello-jni");
    }

    public static native String dbStringFromJNI();

    public static native String dbFileFromJNI();

    public static native String uidFromJNI();

    public static native String pwdFromJNI();

    public static native String keyFromJNI();

    public static native String dbNameFromJNI();

    public static native String apiKeyFromJNI();
}
