# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\EF\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends BaseBundle
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(android.view.View);
}
-dontwarn com.bumptech.glide.**

-dontwarn sun.misc.Unsafe

-keep class android.support.v7.widget.SearchView { *; }

-dontwarn com.yalantis.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn io.github.kexanie.**
-keep class android.support.v4.content{*;}
-keep class com.x5.** { *; }
-dontwarn com.x5.**
#twitter4j
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }
-dontwarn javax.**
-dontwarn android.databinding.**

-dontwarn xyz.donot.**
# Add this global rule
-keepattributes Signature



##---------------Begin: proguard configuration common for all Android apps ----------
-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''



