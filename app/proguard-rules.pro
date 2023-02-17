# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#loader Progard
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }


#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Retrofit
 -dontwarn retrofit2.**
 -dontwarn org.codehaus.mojo.**
 -keep class retrofit2.** { *; }
 -keepattributes Signature
 -keepattributes Exceptions
 -keepattributes Annotation

 -keepattributes RuntimeVisibleAnnotations
 -keepattributes RuntimeInvisibleAnnotations
 -keepattributes RuntimeVisibleParameterAnnotations
 -keepattributes RuntimeInvisibleParameterAnnotations

 -keepattributes EnclosingMethod

 -keepclasseswithmembers class * {
     @retrofit2.* <methods>;
 }

 -keepclasseswithmembers interface * {
     @retrofit2.* <methods>;
 }

 -keepclasseswithmembers class * {
     @retrofit2.http.* <methods>;
 }
 # Platform calls Class.forName on types which do not exist on Android to determine platform.
 -dontnote retrofit2.Platform
 # Platform used when running on RoboVM on iOS. Will not be used at runtime.
 -dontnote retrofit2.Platform$IOS$MainThreadExecutor
 # Platform used when running on Java 8 VMs. Will not be used at runtime.
 -dontwarn retrofit2.Platform$Java8
 # Retain generic type information for use by reflection by converters and adapters.
 -keepattributes Signature
 # Retain declared checked exceptions for use by a Proxy instance.
 -keepattributes Exceptions

 # FIREBASE
 -keep class com.firebase.** { *; }
 -keep class org.apache.** { *; }
 -keepnames class com.fasterxml.jackson.** { *; }
 -keepnames class javax.servlet.** { *; }
 -keepnames class org.ietf.jgss.** { *; }
 -dontwarn org.apache.**
 -dontwarn org.w3c.dom.**

#ROOM
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

#AVLoadingIndicatorView
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

#GoogleDirectionLibrary
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.* { *; }

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keep class com.admin.ozieats_app.model.** { *;}