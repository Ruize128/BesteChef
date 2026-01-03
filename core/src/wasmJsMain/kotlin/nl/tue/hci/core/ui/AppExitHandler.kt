package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable

/**
 * WASM/Web implementation of app exit handler.
 * Currently a no-op. Can be extended to handle browser exit.
 */
@Composable
actual fun rememberAppExitHandler(): () -> Unit {
    return {
        // Web platform exit handling can be implemented here in the future
        // For now, this is a no-op
    }
}
