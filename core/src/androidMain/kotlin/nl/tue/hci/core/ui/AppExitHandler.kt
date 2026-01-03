package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.app.Activity

/**
 * Android implementation of app exit handler.
 * Uses Activity.finishAffinity() to close the app cleanly.
 */
@Composable
actual fun rememberAppExitHandler(): () -> Unit {
    val context = LocalContext.current
    return {
        (context as? Activity)?.finishAffinity()
    }
}
