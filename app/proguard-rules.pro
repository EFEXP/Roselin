-keep class com.chad.library.adapter.** {*;}
-keep public class * extends BaseBundle
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(android.view.View);
}
-dontwarn com.bumptech.glide.**
-dontwarn xyz.donot.roselinx.view.**
-dontwarn com.yalantis.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn io.github.kexanie.**
-keep class android.arch.lifecycle.**
-keep public class  * extends RecyclerView.ViewHolder{ *; }
-keep class android.support.v7.widget.Toolbar.** { *; }
#twitter4j
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }
-dontwarn javax.**
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn android.databinding.**
-dontwarn com.android.**
# Add this global rule
-keepattributes Signature
##---------------Begin: proguard configuration common for all Android apps ----------
-optimizationpasses 3
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



