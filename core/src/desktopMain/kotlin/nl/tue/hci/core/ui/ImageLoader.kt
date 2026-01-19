package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.BesteChefThemeColors
import org.jetbrains.skia.Image

/**
 * Desktop implementation: Loads images from the resources folder or application directory.
 * Icons are rendered as vector graphics for crisp display at any size.
 */
@Composable
actual fun rememberImagePainter(imageName: String): Painter {
    val colors = BesteChefThemeColors.current()
    
    // Handle vector icons - must be called as composable functions outside remember
    return when (imageName) {
        "google_circle" -> createGoogleCircleIcon(colors.textSecondary)
        "chat_icon" -> createChatIcon(colors.textPrimary)
        "filter_icon" -> createFilterIcon(colors.textPrimary)
        "sort_icon" -> createSortIcon(colors.textPrimary)
        "google_logo" -> createGoogleCircleIcon(colors.textSecondary)
        "apple_logo" -> remember { ColorPainter(Color(0xFF888888)) }
        else -> {
            // Handle raster images (photos, etc.)
            remember(imageName) {
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
                    "honey_nut_caramel" -> "images/honey_nut_caramel.jpg"
                    "ichiraku_menu_cover" -> "images/ichiraku_menu_cover.webp"
                    "ichiraku_menu_cover_2" -> "images/ichiraku_menu_cover_2.webp"
                    "ichiraku_menu_cover_3" -> "images/ichiraku_menu_cover_3.webp"
                    "ichiraku" -> "images/ichiraku.png"
                    "middle_eastern_cuisine" -> "images/middle_eastern_cuisine_2.jpg"
                    "middle_eastern_cuisine_2" -> "images/middle_eastern_cuisine_2.jpg"
                    "middle_eastern_cuisine_3" -> "images/middle_eastern_cuisine_3.jpg"
                    "sophie" -> "images/sophie.png"
                    else -> "images/$imageName.png"
                }

                try {
                    val imageUrl = object {}.javaClass.classLoader.getResource(imagePath)
                    if (imageUrl != null) {
                        val imageData = imageUrl.readBytes()
                        val skiaImage = Image.makeFromEncoded(imageData)
                        val imageBitmap = skiaImage.toComposeImageBitmap()
                        androidx.compose.ui.graphics.painter.BitmapPainter(imageBitmap)
                    } else {
                        ColorPainter(Color.LightGray)
                    }
                } catch (e: Exception) {
                    ColorPainter(Color.LightGray)
                }
            }
        }
    }
}

/**
 * Creates Google Circle icon as a vector graphic
 */
@Composable
private fun createGoogleCircleIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Google logo circle
            path(fill = SolidColor(fillColor)) {
                // Outer circle
                moveTo(12f, 0f)
                curveTo(5.373f, 0f, 0f, 5.373f, 0f, 12f)
                curveTo(0f, 18.627f, 5.373f, 24f, 12f, 24f)
                curveTo(18.627f, 24f, 24f, 18.627f, 24f, 12f)
                curveTo(24f, 5.373f, 18.627f, 0f, 12f, 0f)
                close()
                // Google 'G' design
                moveTo(20f, 12f)
                curveTo(20f, 16.411f, 16.411f, 20f, 12f, 20f)
                curveTo(7.589f, 20f, 4f, 16.411f, 4f, 12f)
                curveTo(4f, 7.589f, 7.589f, 4f, 12f, 4f)
                curveTo(13.782f, 4f, 15.468f, 4.573f, 16.876f, 5.658f)
                lineTo(15.017f, 8.073f)
                curveTo(14.146f, 7.402f, 13.093f, 6.975f, 12f, 6.975f)
                curveTo(9.269f, 6.975f, 7.048f, 9.197f, 7.048f, 11.928f)
                curveTo(7.048f, 14.659f, 9.269f, 16.881f, 12f, 16.881f)
                curveTo(14.199f, 16.881f, 16.068f, 15.44f, 16.713f, 13.452f)
                horizontalLineTo(12f)
                verticalLineTo(10.404f)
                horizontalLineTo(20f)
                verticalLineTo(11.928f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}

/**
 * Creates Chat icon as a vector graphic (speech bubble outline)
 */
@Composable
private fun createChatIcon(strokeColor: Color): Painter {
    val imageVector = remember(strokeColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Chat bubble outline
            path(
                stroke = SolidColor(strokeColor),
                strokeLineWidth = 2f
            ) {
                moveTo(20f, 2f)
                horizontalLineTo(4f)
                curveTo(2.9f, 2f, 2f, 2.9f, 2f, 4f)
                verticalLineTo(22f)
                lineTo(6f, 18f)
                horizontalLineTo(20f)
                curveTo(21.1f, 18f, 22f, 17.1f, 22f, 16f)
                verticalLineTo(4f)
                curveTo(22f, 2.9f, 21.1f, 2f, 20f, 2f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}

/**
 * Creates Filter icon as a vector graphic (funnel)
 */
@Composable
private fun createFilterIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Filter/menu icon (three horizontal lines of different lengths)
            path(fill = SolidColor(fillColor)) {
                // Bottom line
                moveTo(10f, 18f)
                horizontalLineTo(14f)
                verticalLineTo(16f)
                horizontalLineTo(10f)
                verticalLineTo(18f)
                close()
                // Top line
                moveTo(3f, 6f)
                verticalLineTo(8f)
                horizontalLineTo(21f)
                verticalLineTo(6f)
                horizontalLineTo(3f)
                close()
                // Middle line
                moveTo(6f, 13f)
                horizontalLineTo(18f)
                verticalLineTo(11f)
                horizontalLineTo(6f)
                verticalLineTo(13f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}

/**
 * Creates Sort icon as a vector graphic (up/down arrows)
 */
@Composable
private fun createSortIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Sort icon (list with different line lengths)
            path(fill = SolidColor(fillColor)) {
                // Short bottom line
                moveTo(3f, 18f)
                horizontalLineTo(9f)
                verticalLineTo(16f)
                horizontalLineTo(3f)
                verticalLineTo(18f)
                close()
                // Long top line
                moveTo(3f, 6f)
                verticalLineTo(8f)
                horizontalLineTo(21f)
                verticalLineTo(6f)
                horizontalLineTo(3f)
                close()
                // Medium middle line
                moveTo(3f, 13f)
                horizontalLineTo(15f)
                verticalLineTo(11f)
                horizontalLineTo(3f)
                verticalLineTo(13f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}
