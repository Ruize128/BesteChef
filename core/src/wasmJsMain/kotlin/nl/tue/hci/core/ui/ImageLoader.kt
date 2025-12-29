package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

/**
 * Web/WASM implementation: Loads images from the resources folder.
 * 
 * Images should be placed in app/src/wasmJsMain/resources/
 * The imageName should match the filename without extension.
 * 
 * This implementation maps image names to file paths. The images are served
 * as static files from the resources folder when the web app is built.
 */
@Composable
actual fun rememberImagePainter(imageName: String): Painter {
    return remember(imageName) {
        // Map image names to their file paths
        // Images in wasmJsMain/resources/images/ are accessible via their filename
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
            "ichiraku_menu_cover" -> "images/ichiraku_menu_cover.png"
            "ichiraku_menu_cover_2" -> "images/ichiraku_menu_cover_2.png"
            "ichiraku_menu_cover_3" -> "images/ichiraku_menu_cover_3.png"
            "ichiraku" -> "images/ichiraku.png"
            "middle_eastern_cuisine" -> "images/middle_eastern_cuisine.png"
            "sophie" -> "images/sophie.png"
            else -> {
                // Try to find the image by attempting common extensions
                val extensions = listOf("png", "jpg", "jpeg", "webp")
                // Use first extension as default
                "images/$imageName.${extensions.first()}"
            }
        }
        
        // For Web/WASM, resources are copied to the output directory and served as static files
        // The image path will be relative to the webpack output root
        // Note: This creates a placeholder painter. For a full implementation, you would
        // need to asynchronously load the image and convert it to a Painter.
        // For now, this provides the mapping structure.
        
        // TODO: Implement proper async image loading for Web
        // For production use, consider using a library like Coil or implementing
        // async image loading with kotlinx.coroutines
        
        throw UnsupportedOperationException(
            "Web image loading requires async implementation. " +
            "Image mapped to: '$imagePath'. " +
            "Please implement async loading or use AsyncImage composable. " +
            "The image should be in app/src/wasmJsMain/resources/$imagePath"
        )
    }
}

