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


#如果要启用混淆，要注意以下平台：

#友盟统计
#-keep class com.umeng.** {*;}

#阿里云推送
#-keepclasseswithmembernames class ** {
#    native <methods>;
#}
#-keepattributes Signature
#-keep class sun.misc.Unsafe { *; }
#-keep class com.taobao.** {*;}
#-keep class com.alibaba.** {*;}
#-keep class com.alipay.** {*;}
#-keep class com.ut.** {*;}
#-keep class com.ta.** {*;}
#-keep class anet.**{*;}
#-keep class anetwork.**{*;}
#-keep class org.android.spdy.**{*;}
#-keep class org.android.agoo.**{*;}
#-keep class android.os.**{*;}
#-dontwarn com.taobao.**
#-dontwarn com.alibaba.**
#-dontwarn com.alipay.**
#-dontwarn anet.**
#-dontwarn org.android.spdy.**
#-dontwarn org.android.agoo.**
#-dontwarn anetwork.**
#-dontwarn com.ut.**
#-dontwarn com.ta.**

#百度地图
#-keep class com.baidu.** {*;}
#-keep class mapsdkvi.com.** {*;}
#-dontwarn com.baidu.**

#(持续维护)...