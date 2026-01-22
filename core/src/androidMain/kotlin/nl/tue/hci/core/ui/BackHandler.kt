package nl.tue.hci.core.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * Android implementation of PlatformBackHandler.
 * Uses Jetpack Compose's BackHandler for intercepting system back navigation.
 */
@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
