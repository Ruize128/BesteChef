package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

/**
 * Loads an image resource by name that works on both Android and Web.
 * 
 * ## Setup Instructions:
 * 
 * ### For Android:
 * 1. Place your images in `app/src/androidMain/res/drawable/` (or `app/src/main/res/drawable/`)
 * 2. Add a mapping in `core/src/androidMain/kotlin/nl/tue/hci/core/ui/ImageLoader.kt`:
 *    ```kotlin
 *    "chef_avatar" -> R.drawable.chef_avatar
 *    ```
 * 
 * ### For Web:
 * 1. Place your images in `app/src/wasmJsMain/resources/`
 * 2. The Web implementation will be enhanced in the future
 * 
 * ## Usage:
 * ```kotlin
 * import androidx.compose.foundation.Image
 * import nl.tue.hci.core.ui.rememberImagePainter
 * 
 * Image(
 *     painter = rememberImagePainter("chef_avatar"),
 *     contentDescription = "Chef avatar",
 *     modifier = Modifier.size(100.dp)
 * )
 * ```
 * 
 * @param imageName The name of the image file without extension (e.g., "chef_avatar" for chef_avatar.png)
 * @return A Painter that can be used with Image composable
 */
@Composable
expect fun rememberImagePainter(imageName: String): Painter

