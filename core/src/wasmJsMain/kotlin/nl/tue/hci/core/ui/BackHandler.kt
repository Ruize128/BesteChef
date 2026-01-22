package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable

/**
 * WASM/Web implementation of PlatformBackHandler.
 * Currently a no-op. Can be extended to handle browser back navigation.
 */
@Composable
actual fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // Web platform back handling can be implemented here in the future
    // For now, this is a no-op as browser back is typically handled differently
}
