package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable

/**
 * Desktop implementation of PlatformBackHandler.
 * Desktop doesn't have a system back button, so this is a no-op.
 * Users can use keyboard shortcuts (Escape, Alt+Left, etc.) if needed.
 */
@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // Desktop implementation: currently a no-op
    // Could be extended to handle keyboard shortcuts like Escape key
}
