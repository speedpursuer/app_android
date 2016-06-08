#-libraryjars libs/pushservice-5.0.0.66.jar
-dontwarn com.baidu.**
-keep class com.baidu.**{*; }
-keep interface com.baidu.**{*; }


-keep public class * extends org.apache.cordova.CordovaPlugin
-keep class org.apache.cordova.** { *; }
-dontwarn android.webkit.*

#-keepattributes *Annotation*,SourceFile,LineNumberTable

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**

-keep class com.facebook.imagepipeline.animated.factory.** { *; }