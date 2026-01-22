package nl.tue.hci.bestechef

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlin.math.abs

// Import the shared app from wasmJsMain
// Since we can't use source sets sharing directly, we'll reuse the BesteChefApp from commonMain

/**
 * macOS/Desktop entry point for BesteChef application.
 * Configured with iPhone 15 Pro Max dimensions (430x932 dp)
 * Window is resizable but maintains the aspect ratio
 */
fun main() = application {
    val aspectRatio = 430f / 932f  // Width / Height ratio
    val windowState = rememberWindowState(width = 430.dp, height = 932.dp)
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "BesteChef",
        state = windowState,
        resizable = true
    ) {
        // Maintain aspect ratio when window is resized
        LaunchedEffect(windowState) {
            snapshotFlow { windowState.size }
                .collect { size ->
                    val currentRatio = size.width.value / size.height.value
                    // Only adjust if ratio differs significantly (to avoid infinite loop)
                    if (abs(currentRatio - aspectRatio) > 0.01f) {
                        windowState.size = DpSize(
                            width = size.width,
                            height = (size.width.value / aspectRatio).dp
                        )
                    }
                }
        }
        
        // BesteChefApp is in commonMain, so it's accessible here
        BesteChefApp()
    }
}

