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
        "google_logo" -> createGoogleCircleIcon(colors.textPrimary)
        "apple_logo" -> createAppleIcon(colors.textPrimary)
        "chat_icon" -> createChatIcon(colors.textPrimary)
        "comments" -> createCommentsIcon(colors.textPrimary)
        "comments_light" -> createCommentsLightIcon(colors.textPrimary)
        "home" -> createHomeIcon(colors.textPrimary)
        "booking" -> createBookingIcon(colors.textPrimary)
        "profile" -> createProfileIcon(colors.textPrimary)
        "filter_icon" -> createFilterIcon(colors.textPrimary)
        "filter_list" -> createFilterListIcon(colors.textPrimary)
        "sort_icon" -> createSortIcon(colors.textPrimary)
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

/**
 * Creates Apple icon as a vector graphic
 */
@Composable
private fun createAppleIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(fillColor)) {
                // Main apple body with leaf
                moveTo(18.546f, 12.763f)
                curveTo(18.57f, 10.893f, 19.55f, 9.166f, 21.143f, 8.187f)
                curveTo(20.134f, 6.745f, 18.503f, 5.864f, 16.744f, 5.809f)
                curveTo(14.893f, 5.615f, 13.099f, 6.916f, 12.156f, 6.916f)
                curveTo(11.195f, 6.916f, 9.743f, 5.828f, 8.179f, 5.86f)
                curveTo(6.122f, 5.927f, 4.25f, 7.068f, 3.249f, 8.867f)
                curveTo(1.118f, 12.557f, 2.707f, 17.981f, 4.749f, 20.964f)
                curveTo(5.771f, 22.425f, 6.964f, 24.056f, 8.527f, 23.999f)
                curveTo(10.056f, 23.936f, 10.627f, 23.024f, 12.472f, 23.024f)
                curveTo(14.3f, 23.024f, 14.836f, 23.999f, 16.43f, 23.962f)
                curveTo(18.07f, 23.935f, 19.104f, 22.495f, 20.09f, 21.02f)
                curveTo(20.824f, 19.979f, 21.389f, 18.829f, 21.763f, 17.612f)
                curveTo(19.815f, 16.788f, 18.548f, 14.879f, 18.546f, 12.763f)
                close()
                // Leaf
                moveTo(15.535f, 3.847f)
                curveTo(16.429f, 2.773f, 16.87f, 1.393f, 16.763f, 0f)
                curveTo(15.397f, 0.144f, 14.134f, 0.797f, 13.228f, 1.829f)
                curveTo(12.333f, 2.848f, 11.879f, 4.18f, 11.967f, 5.534f)
                curveTo(13.352f, 5.548f, 14.667f, 4.926f, 15.535f, 3.847f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}

/**
 * Creates Home icon as a vector graphic
 */
@Composable
private fun createHomeIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(fillColor)) {
                moveTo(12f, 14f)
                curveTo(10.343f, 14f, 9f, 15.343f, 9f, 17f)
                verticalLineTo(24.026f)
                horizontalLineTo(15f)
                verticalLineTo(17f)
                curveTo(15f, 15.343f, 13.657f, 14f, 12f, 14f)
                close()
                moveTo(13.338f, 0.833f)
                curveTo(12.838f, 0.333f, 12.162f, 0.333f, 11.662f, 0.833f)
                lineTo(0f, 10.429f)
                verticalLineTo(20.829f)
                curveTo(0f, 22.219f, 1.61f, 24.029f, 3.2f, 24.029f)
                horizontalLineTo(7f)
                verticalLineTo(17f)
                curveTo(7f, 14.239f, 9.239f, 12f, 12f, 12f)
                curveTo(14.761f, 12f, 17f, 14.239f, 17f, 17f)
                verticalLineTo(24.026f)
                horizontalLineTo(20.8f)
                curveTo(22.39f, 24.026f, 24f, 22.216f, 24f, 20.826f)
                verticalLineTo(10.426f)
                lineTo(13.338f, 0.833f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}

/**
 * Creates Comments icon as a vector graphic
 */
@Composable
private fun createCommentsIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(fillColor)) {
                moveTo(8.7f, 18f)
                horizontalLineTo(3f)
                curveTo(1.507f, 18f, 0f, 16.866f, 0f, 14.334f)
                verticalLineTo(9.294f)

                arcToRelative(9.418f, 9.418f, 0f, false, true, 8.349f, -9.271f)
                arcToRelative(9f, 9f, 0f, false, true, 9.628f, 9.628f)
                arcToRelative(9.419f, 9.419f, 0f, false, true, -9.277f, 8.349f)
                close()

                moveTo(20f, 9.08f)
                horizontalLineTo(19.988f)
                curveToRelative(0f, 0.237f, 0f, 0.474f, -0.012f, 0.712f)
                curveTo(19.59f, 15.2f, 14.647f, 19.778f, 9.084f, 19.981f)
                lineToRelative(0f, 0.015f)

                arcToRelative(8f, 8f, 0f, false, false, 6.916f, 4.004f)
                horizontalLineTo(21f)
                arcToRelative(3f, 3f, 0f, false, false, 3f, -3f)
                verticalLineTo(16f)
                arcToRelative(8f, 8f, 0f, false, false, -4f, -6.92f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}



/**
 * Creates Comments Light icon as a vector graphic
 */
@Composable
private fun createCommentsLightIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(fillColor)) {
                // M24,16v5a3,3,0,0,1-3,3H16
                moveTo(24f, 16f)
                verticalLineTo(21f)
                arcToRelative(3f, 3f, 0f, false, true, -3f, 3f)
                horizontalLineTo(16f)

                // a8,8,0,0,1-6.92-4
                arcToRelative(8f, 8f, 0f, false, true, -6.92f, -4f)

                // a10.968,10.968,0,0,0,2.242-.248
                arcToRelative(10.968f, 10.968f, 0f, false, false, 2.242f, -0.248f)

                // A5.988,5.988,0,0,0,16,22
                arcToRelative(5.988f, 5.988f, 0f, false, false, 4.678f, 2.248f)

                // h5a1,1,0,0,0,1-1
                horizontalLineTo(21f)
                arcToRelative(1f, 1f, 0f, false, false, 1f, -1f)

                // V16
                verticalLineTo(16f)

                // a5.988,5.988,0,0,0-2.252-4.678
                arcToRelative(5.988f, 5.988f, 0f, false, false, -2.252f, -4.678f)

                // A10.968,10.968,0,0,0,20,9.08
                arcToRelative(10.968f, 10.968f, 0f, false, false, 0.252f, -2.242f)

                // A8,8,0,0,1,24,16
                arcToRelative(8f, 8f, 0f, false, true, 4f, 6.92f)
                close()

                // M17.977,9.651A9,9,0,0,0,8.349.023
                moveTo(17.977f, 9.651f)
                arcToRelative(9f, 9f, 0f, false, false, -9.628f, -9.628f)

                // A9.418,9.418,0,0,0,0,9.294
                arcToRelative(9.418f, 9.418f, 0f, false, false, -8.349f, 9.271f)

                // v5.04C0,16.866,1.507,18,3,18
                verticalLineToRelative(5.04f)
                curveTo(0f, 16.866f, 1.507f, 18f, 3f, 18f)

                // H8.7A9.419,9.419,0,0,0,17.977,9.651
                horizontalLineTo(8.7f)
                arcToRelative(9.419f, 9.419f, 0f, false, false, 9.277f, -8.349f)
                close()

                // m-4.027-5.6
                moveToRelative(-4.027f, -5.6f)

                // a7.018,7.018,0,0,1,2.032,5.46
                arcToRelative(7.018f, 7.018f, 0f, false, true, 2.032f, 5.46f)

                // A7.364,7.364,0,0,1,8.7,16
                arcToRelative(7.364f, 7.364f, 0f, false, true, -7.282f, 6.489f)

                // H3c-.928,0-1-1.275-1-1.666
                horizontalLineTo(3f)
                curveTo(2.072f, 16f, 2f, 14.725f, 2f, 14.334f)

                // V9.294A7.362,7.362,0,0,1,8.49,2.018
                verticalLineTo(9.294f)
                arcToRelative(7.362f, 7.362f, 0f, false, true, 6.49f, -7.276f)

                // Q8.739,2,8.988,2
                quadTo(8.739f, 2f, 8.988f, 2f)

                // A7.012,7.012,0,0,1,13.95,4.051
                arcToRelative(7.012f, 7.012f, 0f, false, true, 4.962f, 2.051f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}


/**
 * Creates Booking icon as a vector graphic
 */
@Composable
private fun createBookingIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(fillColor)) {
                // Main calendar body
                moveTo(19.908f, 2.581f)
                curveTo(19.801f, 2.349f, 19.609f, 2.166f, 19.371f, 2.071f)
                curveTo(19.262f, 2.028f, 16.639f, 1f, 11.999f, 1f)
                curveTo(7.36f, 1f, 4.737f, 2.028f, 4.627f, 2.071f)
                curveTo(4.248f, 2.223f, 3.999f, 2.591f, 3.999f, 3f)
                curveTo(3.999f, 13.583f, 6.954f, 20.601f, 7.08f, 20.894f)
                curveTo(7.209f, 21.197f, 7.48f, 21.416f, 7.803f, 21.48f)
                curveTo(7.909f, 21.501f, 10.452f, 22f, 14.999f, 22f)
                curveTo(19.638f, 22f, 22.261f, 20.972f, 22.371f, 20.929f)
                curveTo(22.75f, 20.777f, 22.999f, 20.409f, 22.999f, 20f)
                curveTo(22.999f, 9.402f, 20.033f, 2.854f, 19.908f, 2.581f)
                close()
                // First line
                moveTo(8.5f, 8f)
                curveTo(8.5f, 7.448f, 8.948f, 7f, 9.5f, 7f)
                horizontalLineTo(16.5f)
                curveTo(17.052f, 7f, 17.5f, 7.448f, 17.5f, 8f)
                curveTo(17.5f, 8.552f, 17.052f, 9f, 16.5f, 9f)
                horizontalLineTo(9.5f)
                curveTo(8.948f, 9f, 8.5f, 8.552f, 8.5f, 8f)
                close()
                // Second line
                moveTo(9.192f, 12f)
                curveTo(9.192f, 11.448f, 9.64f, 11f, 10.192f, 11f)
                horizontalLineTo(17.192f)
                curveTo(17.744f, 11f, 18.192f, 11.448f, 18.192f, 12f)
                curveTo(18.192f, 12.552f, 17.744f, 13f, 17.192f, 13f)
                horizontalLineTo(10.192f)
                curveTo(9.64f, 13f, 9.192f, 12.552f, 9.192f, 12f)
                close()
                // Third line
                moveTo(18f, 17f)
                horizontalLineTo(11f)
                curveTo(10.448f, 17f, 10f, 16.552f, 10f, 16f)
                curveTo(10f, 15.448f, 10.448f, 15f, 11f, 15f)
                horizontalLineTo(18f)
                curveTo(18.552f, 18f, 18f, 17.448f, 18f, 18f)
                curveTo(18f, 17.448f, 18.552f, 17f, 18f, 17f)
                close()
                // Left-bottom page piece
                moveTo(5f, 22f)
                curveTo(2.48f, 21.84f, 1.243f, 21.579f, 1.17f, 21.563f)
                curveTo(0.709f, 21.464f, 0.38f, 21.057f, 0.38f, 20.585f)
                curveTo(0.38f, 15.808f, 0.983f, 11.652f, 1.655f, 8.626f)
                curveTo(2.417f, 15.449f, 4.515f, 20.553f, 4.622f, 20.803f)
                curveTo(4.652f, 20.873f, 4.701f, 20.932f, 4.736f, 21.002f)
                lineTo(5f, 22f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}

/**
 * Creates Profile icon as a vector graphic
 */
@Composable
private fun createProfileIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(fillColor)) {
                // Bottom curved section (neck/shoulders)
                moveTo(16.499f, 22.486f)
                curveTo(15.207f, 22.826f, 13.719f, 23f, 12f, 23f)
                curveTo(10.281f, 23f, 8.793f, 22.826f, 7.501f, 22.486f)
                curveTo(7.504f, 19.133f, 8.641f, 18f, 12f, 18f)
                curveTo(15.359f, 18f, 16.496f, 19.134f, 16.499f, 22.486f)
                close()
                // Inner head circle
                moveTo(12f, 8f)
                curveTo(10.411f, 8f, 10f, 8.411f, 10f, 10f)
                curveTo(10f, 11.589f, 10.411f, 12f, 12f, 12f)
                curveTo(13.589f, 12f, 14f, 11.589f, 14f, 10f)
                curveTo(14f, 8.411f, 13.589f, 8f, 12f, 8f)
                close()
                // Outer circle boundary
                moveTo(23f, 12f)
                curveTo(23f, 17.028f, 21.59f, 20.165f, 18.478f, 21.747f)
                curveTo(18.258f, 17.782f, 16.235f, 16f, 12f, 16f)
                curveTo(7.765f, 16f, 5.742f, 17.782f, 5.522f, 21.747f)
                curveTo(2.411f, 20.165f, 1f, 17.027f, 1f, 12f)
                curveTo(1f, 4.29f, 4.29f, 1f, 12f, 1f)
                curveTo(19.71f, 1f, 23f, 4.29f, 23f, 12f)
                close()
                // Outer head circle
                moveTo(16f, 10f)
                curveTo(16f, 7.309f, 14.691f, 6f, 12f, 6f)
                curveTo(9.309f, 6f, 8f, 7.309f, 8f, 10f)
                curveTo(8f, 12.691f, 9.309f, 14f, 12f, 14f)
                curveTo(14.691f, 14f, 16f, 12.691f, 16f, 10f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}

/**
 * Creates Filter List icon as a vector graphic
 */
@Composable
private fun createFilterListIcon(fillColor: Color): Painter {
    val imageVector = remember(fillColor) {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(fillColor)) {
                moveTo(11f, 22f)
                curveTo(10.788f, 22f, 10.577f, 21.932f, 10.4f, 21.8f)
                lineTo(6.4f, 18.8f)
                curveTo(6.148f, 18.612f, 6f, 18.315f, 6f, 18f)
                verticalLineTo(11.37f)
                lineTo(0.944f, 5.471f)
                curveTo(0.335f, 4.761f, 0f, 3.855f, 0f, 2.92f)
                curveTo(0f, 0.758f, 1.758f, -0.999f, 3.92f, -0.999f)
                horizontalLineTo(14.08f)
                curveTo(16.242f, -0.999f, 18f, 0.761f, 18f, 2.921f)
                curveTo(18f, 3.856f, 17.665f, 4.762f, 17.056f, 5.472f)
                lineTo(12f, 11.37f)
                verticalLineTo(18f)
                curveTo(12f, 18.379f, 11.786f, 18.725f, 11.447f, 18.895f)
                curveTo(11.305f, 18.966f, 11.152f, 19f, 11f, 19f)
                close()
                moveTo(24f, 16f)
                verticalLineTo(21f)
                curveTo(24f, 21.552f, 23.552f, 22f, 23f, 22f)
                horizontalLineTo(15f)
                curveTo(14.448f, 22f, 14f, 21.552f, 14f, 21f)
                curveTo(14f, 20.448f, 14.448f, 20f, 15f, 20f)
                horizontalLineTo(23f)
                curveTo(23.552f, 20f, 24f, 20.448f, 24f, 21f)
                close()
                moveTo(24f, 12f)
                verticalLineTo(17f)
                curveTo(24f, 17.552f, 23.552f, 18f, 23f, 18f)
                horizontalLineTo(15f)
                curveTo(14.448f, 18f, 14f, 17.552f, 14f, 17f)
                curveTo(14f, 16.448f, 14.448f, 16f, 15f, 16f)
                horizontalLineTo(23f)
                curveTo(23.552f, 16f, 24f, 16.448f, 24f, 17f)
                close()
                moveTo(24f, 8f)
                verticalLineTo(13f)
                curveTo(24f, 13.552f, 23.552f, 14f, 23f, 14f)
                horizontalLineTo(19f)
                curveTo(18.448f, 14f, 18f, 13.552f, 18f, 13f)
                curveTo(18f, 12.448f, 18.448f, 12f, 19f, 12f)
                horizontalLineTo(23f)
                curveTo(23.552f, 12f, 24f, 12.448f, 24f, 13f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}
