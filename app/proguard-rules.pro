##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

-keep class com.easyroute.network.model.**
-keep class com.easyroute.network.request.**
-keep class com.easyroute.network.response.**
-keep class com.easyroute.content.constant.**
-keep class com.easyroute.content.model.**


-keep class org.kobjects.** { *; }
-keep class org.ksoap2.** { *; }
-keep class org.kxml2.** { *; }
-keep class org.xmlpull.** { *; }

-dontwarn okio.**
-dontwarn org.xmlpull.v1.**