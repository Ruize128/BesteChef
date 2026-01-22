# Debug-specific ProGuard rules
# These rules enable R8 optimizations while keeping code readable for debugging

# Disable obfuscation for easier debugging
-dontobfuscate

# Keep line numbers for stack traces
-keepattributes SourceFile,LineNumberTable

# Keep all class names and method names readable
-keepnames class *
-keepnames interface *
-keepclassmembernames class * {
    *;
}

# Keep all Compose-related code for debugging
-keep class androidx.compose.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Keep all your app's classes readable
-keep class nl.tue.hci.** { *; }

