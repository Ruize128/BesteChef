package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable
import kotlin.system.exitProcess

/**
 * Desktop implementation of app exit handler.
 * Calls System.exit() to close the application.
 */
@Composable
actual fun rememberAppExitHandler(): () -> Unit {
    return {
        exitProcess(0)
    }
}
