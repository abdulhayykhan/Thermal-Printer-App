# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Bluetooth classes
-keep class android.bluetooth.** { *; }

# Keep data classes
-keep class com.naeem.thermalprinter.domain.model.** { *; }
-keep class com.naeem.thermalprinter.domain.printer.** { *; }

# Keep Compose runtime
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Kotlinx Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
