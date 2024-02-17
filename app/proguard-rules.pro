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

-dontwarn com.adivery.data.*
-dontwarn com.chartboost.sdk.*
-dontwarn com.chartboost.sdk.Libraries.*
-dontwarn com.chartboost.sdk.Model.*
-dontwarn com.adcolony.sdk.*
-dontwarn com.google.ads.interactivemedia.v3.api.*
-dontwarn com.google.ads.interactivemedia.v3.api.*.*
-dontwarn com.google.android.gms.ads.*
-dontwarn com.google.common.*.*
-dontwarn com.unity3d.*.*
-dontwarn com.unity3d.*.*.*
-dontwarn com.google.android.gms.ads.*.*
-dontwarn com.ironsource.mediationsdk.*
-dontwarn com.ironsource.mediationsdk.*.*
-dontwarn com.mbridge.msdk.*
-dontwarn com.mbridge.msdk.*.*
-dontwarn com.mbridge.msdk.video.bt.module.b.*
-dontwarn com.mbridge.msdk.newinterstitial.*.*
-dontwarn com.startapp.sdk.*.*
-dontwarn com.startapp.sdk.adsbase.*.*
