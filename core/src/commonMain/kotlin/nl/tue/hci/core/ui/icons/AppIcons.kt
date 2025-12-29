package nl.tue.hci.core.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import nl.tue.hci.core.ui.rememberImagePainter

/**
 * Custom icons for the BesteChef application.
 * These icons are loaded from SVG files (Android: Vector Drawable XML, Web: SVG).
 * 
 * Usage:
 * ```kotlin
 * import nl.tue.hci.core.ui.icons.*
 * 
 * // As Image composable
 * ChatIcon()
 * FilterIcon()
 * SortIcon()
 * 
 * // Or get the Painter directly
 * val painter = rememberIconPainter("chat_icon")
 * Image(painter = painter, contentDescription = "Chat")
 * ```
 */

/**
 * Loads an icon by name as a Painter.
 * Icons: "chat_icon", "filter_icon", "sort_icon"
 */
@Composable
fun rememberIconPainter(iconName: String): Painter {
    return rememberImagePainter(iconName)
}

/**
 * Chat Icon - Speech bubble with dots
 */
@Composable
fun ChatIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Chat",
    tint: androidx.compose.ui.graphics.Color? = null
) {
    Image(
        painter = rememberImagePainter("chat_icon"),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}

/**
 * Filter Icon - Funnel shape
 */
@Composable
fun FilterIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Filter",
    tint: androidx.compose.ui.graphics.Color? = null
) {
    Image(
        painter = rememberImagePainter("filter_icon"),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}

/**
 * Sort Icon - Up and down arrows with lines
 */
@Composable
fun SortIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Sort",
    tint: androidx.compose.ui.graphics.Color? = null
) {
    Image(
        painter = rememberImagePainter("sort_icon"),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}
