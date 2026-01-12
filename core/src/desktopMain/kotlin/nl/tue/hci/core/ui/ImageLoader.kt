package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image
import java.io.File

/**
 * Desktop implementation: Loads images from the resources folder or application directory.
 * 
 * Images can be loaded from:
 * 1. Resources embedded in the JAR (from desktopMain/resources/)
 * 2. Application directory for external images
 */
@Composable
actual fun rememberImagePainter(imageName: String): Painter {
    return remember(imageName) {
        // Map image names to their file paths
        val imagePath = when (imageName) {
            "yuzu_mousse" -> "images/yuzu_mousse.png"
            "grilled_mackerel_with_miso" -> "images/grilled_mackerel_with_miso.webp"
            "grilled_mackerel_with_miso_2" -> "images/grilled_mackerel_with_miso_2.webp"
            "grilled_mackerel_with_miso_3" -> "images/grilled_mackerel_with_miso_3.webp"
            "seared_seabass" -> "images/seared_seabass.jpeg"
            "omakase_5_course" -> "images/omakase_5_course.jpg"
            "wagyu_beef_steak" -> "images/wagyu_beef_steak.webp"
            "sushi_platter" -> "images/sushi_platter.webp"
            "caesar_salad" -> "images/caesar_salad.jpg"
            "ichiraku_menu_cover" -> "images/ichiraku_menu_cover.webp"
            "ichiraku_menu_cover_2" -> "images/ichiraku_menu_cover_2.webp"
            "ichiraku_menu_cover_3" -> "images/ichiraku_menu_cover_3.webp"
            "ichiraku" -> "images/ichiraku.png"
            "middle_eastern_cuisine" -> "images/middle_eastern_cuisine_2.jpg"
            "middle_eastern_cuisine_2" -> "images/middle_eastern_cuisine_2.jpg"
            "middle_eastern_cuisine_3" -> "images/middle_eastern_cuisine_3.jpg"
            "sophie" -> "images/sophie.png"
            // Icons will return placeholder colors for now (they're simple SVGs)
            "chat_icon" -> null
            "filter_icon" -> null
            "sort_icon" -> null
            "google_logo" -> null
            "apple_logo" -> null
            else -> "images/$imageName.png" // Try with .png extension by default
        }

        // Return placeholder for icons that don't have images
        if (imagePath == null) {
            return@remember ColorPainter(Color(0xFF888888))
        }

        try {
            // Try to load from resources first
            val imageUrl = object {}.javaClass.classLoader.getResource(imagePath)
            if (imageUrl != null) {
                val imageData = imageUrl.readBytes()
                val skiaImage = Image.makeFromEncoded(imageData)
                val imageBitmap = skiaImage.toComposeImageBitmap()
                androidx.compose.ui.graphics.painter.BitmapPainter(imageBitmap)
            } else {
                // Return a placeholder painter if image not found
                ColorPainter(Color.LightGray)
            }
        } catch (e: Exception) {
            // Return placeholder on error
            ColorPainter(Color.LightGray)
        }
    }
}
