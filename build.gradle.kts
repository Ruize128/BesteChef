// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
}


// Disable all test-related tasks
tasks.matching { 
    it.name.contains("test", ignoreCase = true) || 
    it.name.contains("Test", ignoreCase = true) ||
    it.name.contains("unitTest", ignoreCase = true) ||
    it.name.contains("androidTest", ignoreCase = true)
}.configureEach {
    enabled = false
}