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
#-dontwarn sun.misc.**
#-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
#    long producerIndex;
#    long consumerIndex;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode producerNode;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
#    rx.internal.util.atomic.LinkedQueueNode consumerNode;
#}

#-dontwarn retrofit.**
#-keep class retrofit.** { *; }
#-keepattributes Signature
#-keepattributes Exceptions
#
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.module.AppGlideModule
#-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#
#-keep class kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoaderImpl
#
#-keepclassmembers class kotlin.Metadata {
#    public <methods>;
#}
#
#
#-ignorewarnings
#
#-keepattributes Signature,*Annotation*
#
## keep BmobSDK
#-dontwarn cn.bmob.v3.**
#-keep class cn.bmob.v3.** {*;}
#
## 确保JavaBean不被混淆-否则gson将无法将数据解析成具体对象
#-keep class * extends cn.bmob.v3.BmobObject {
#    *;
#}
#-keep class com.example.bmobexample.bean.BankCard{*;}
#-keep class com.example.bmobexample.bean.GameScore{*;}
#-keep class com.example.bmobexample.bean.MyUser{*;}
#-keep class com.example.bmobexample.bean.Person{*;}
#-keep class com.example.bmobexample.file.Movie{*;}
#-keep class com.example.bmobexample.file.Song{*;}
#-keep class com.example.bmobexample.relation.Post{*;}
#-keep class com.example.bmobexample.relation.Comment{*;}
#
## keep BmobPush
#-dontwarn  cn.bmob.push.**
#-keep class cn.bmob.push.** {*;}
#
## keep okhttp3、okio
#-dontwarn okhttp3.**
#-keep class okhttp3.** { *;}
#-keep interface okhttp3.** { *; }
#-dontwarn okio.**
#
## keep rx
#-dontwarn sun.misc.**
#-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
# long producerIndex;
# long consumerIndex;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
# rx.internal.util.atomic.LinkedQueueNode producerNode;
#}
#-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
# rx.internal.util.atomic.LinkedQueueNode consumerNode;
#}