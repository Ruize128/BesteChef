package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable

/**
 * A multiplatform back handler that works across Android and Web platforms.
 * 
 * On Android: Intercepts the system back button/gesture
 * On Web: Can be extended to handle browser back navigation
 * 
 * @param enabled Whether the back handler is active
 * @param onBack Callback when back is triggered
 */
@Composable
expect fun PlatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)
