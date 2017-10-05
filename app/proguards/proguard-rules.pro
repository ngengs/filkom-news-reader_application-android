# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\N_Developer\Android\SDK/tools/proguard/proguard-android.txt
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

# Don't note duplicate definition (Legacy Apche Http Client)
-dontnote android.net.http.*
-dontnote org.apache.http.**

-dontnote sun.misc.Unsafe

-keep class com.ngengs.android.app.filkomnewsreader.data.model.** { *; }
-keep class com.ngengs.android.app.filkomnewsreader.data.response.** { *; }
-keep class com.ngengs.android.baking.apps.network.FilkomService { *; }
