-keep class com.jarvis.controller.** { *; }
-keepclassmembers class com.jarvis.controller.** { *; }

-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

-keep class com.google.gson.** { *; }
-keepclassmembers class com.google.gson.** { *; }

-keep class com.google.dagger.** { *; }
-keep class javax.inject.** { *; }

-keep class androidx.room.** { *; }
-keep class androidx.datastore.** { *; }

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

-dontwarn java.lang.invoke.*
-dontwarn sun.misc.Unsafe
-dontwarn com.google.protobuf.**