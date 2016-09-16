-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *

-keepclassmembers class * { @com.facebook.common.internal.DoNotStrip *; }
-keepclassmembers class * { native <methods>; }

-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }

-keepattributes SourceFile,LineNumberTable,Annotation

-keep class .R
-keep class **.R$* { <fields>;}
-keep class android.support.v7.widget.SearchView { *; }
-keep class retrofit2.** { *; }
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**

# Works around a bug in the animated GIF module which will be fixed in 0.12.0
-keep class com.facebook.imagepipeline.animated.factory.AnimatedFactoryImpl {
    public AnimatedFactoryImpl(com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory,com.facebook.imagepipeline.core.ExecutorSupplier);
}

-dontwarn com.crashlytics.**
-dontwarn retrofit2.**
-dontwarn butterknife.internal.**
-dontwarn com.facebook.**
-dontwarn org.jaudiotagger.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**