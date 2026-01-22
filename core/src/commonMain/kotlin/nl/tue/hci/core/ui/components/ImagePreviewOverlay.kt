package nl.tue.hci.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import nl.tue.hci.core.ui.rememberImagePainter

/**
 * A full-screen image preview overlay component.
 *
 * @param showPreview Whether to show the preview overlay
 * @param imageName The name of the image to display (passed to rememberImagePainter)
 * @param onDismiss Callback when the preview should be dismissed (e.g., when clicked)
 */
@Composable
fun ImagePreviewOverlay(
    showPreview: Boolean,
    imageName: String?,
    onDismiss: () -> Unit
) {
    if (showPreview && imageName != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberImagePainter(imageName),
                contentDescription = "Full screen image preview",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() },
                contentScale = ContentScale.Fit
            )
        }
    }
}