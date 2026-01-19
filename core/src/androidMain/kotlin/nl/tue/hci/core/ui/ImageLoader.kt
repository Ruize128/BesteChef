package nl.tue.hci.core.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.data.PlatformContext

/**
 * Android implementation: Loads images from drawable resources or renders vector icons.
 * Vector icons use BesteChefTheme colors directly for automatic theme support.
 */
@Composable
actual fun rememberImagePainter(imageName: String): Painter {
    val context = LocalContext.current
    val colors = BesteChefThemeColors.current()
    
    // Handle vector icons using BesteChefTheme colors
    return when (imageName) {
        "google_circle" -> createGoogleCircleIcon(colors.textPrimary)
        "google_logo" -> createGoogleCircleIcon(colors.textPrimary)
        "chat_icon" -> createChatIcon(colors.textPrimary)
        "filter_icon" -> createFilterIcon(colors.textPrimary)
        "sort_icon" -> createSortIcon(colors.textPrimary)
        "apple_logo" -> createAppleLogo()
        else -> {
            // Handle raster images from drawable resources
            val resourceName = when (imageName) {
                "yuzu_mousse" -> "yuzu_mousse"
                "grilled_mackerel_with_miso" -> "grilled_mackerel_with_miso"
                "grilled_mackerel_with_miso_2" -> "grilled_mackerel_with_miso_2"
                "grilled_mackerel_with_miso_3" -> "grilled_mackerel_with_miso_3"
                "seared_seabass" -> "seared_seabass"
                "omakase_5_course" -> "omakase_5_course"
                "wagyu_beef_steak" -> "wagyu_beef_steak"
                "sushi_platter" -> "sushi_platter"
                "caesar_salad" -> "caesar_salad"
                "honey_nut_caramel" -> "honey_nut_caramel"
                "ichiraku_menu_cover" -> "ichiraku_menu_cover"
                "ichiraku_menu_cover_2" -> "ichiraku_menu_cover_2"
                "ichiraku_menu_cover_3" -> "ichiraku_menu_cover_3"
                "ichiraku" -> "ichiraku"
                "middle_eastern_cuisine" -> "middle_eastern_cuisine"
                "middle_eastern_cuisine_2" -> "middle_eastern_cuisine_2"
                "middle_eastern_cuisine_3" -> "middle_eastern_cuisine_3"
                "sophie" -> "sophie"
                else -> throw IllegalArgumentException(
                    "Image resource '$imageName' not found. " +
                    "Make sure to:\n" +
                    "1. Place the image in app/src/androidMain/res/drawable/ (or app/src/main/res/drawable/)\n" +
                    "2. Add a mapping entry in ImageLoader.kt: \"$imageName\" -> \"$imageName\""
                )
            }
            
            // Get resource ID by name
            val resId = context.resources.getIdentifier(
                resourceName,
                "drawable",
                context.packageName
            )
            
            if (resId == 0) {
                throw IllegalArgumentException(
                    "Drawable resource '$resourceName' not found in package '${context.packageName}'. " +
                    "Make sure the image file exists in res/drawable/"
                )
            }
            
            painterResource(id = resId)
        }
    }
}

/**
 * Creates Google Circle icon as a vector graphic using BesteChefTheme colors
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
 * Creates Chat icon as a vector graphic
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
 * Creates Filter icon as a vector graphic
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
 * Creates Sort icon as a vector graphic
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
 * Creates Apple logo placeholder
 */
@Composable
private fun createAppleLogo(): Painter {
    val imageVector = remember {
        ImageVector.Builder(
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF888888))) {
                // Simple apple-like shape
                moveTo(12f, 2f)
                curveTo(8f, 2f, 5f, 5f, 5f, 9f)
                curveTo(5f, 14f, 8f, 18f, 12f, 22f)
                curveTo(16f, 18f, 19f, 14f, 19f, 9f)
                curveTo(19f, 5f, 16f, 2f, 12f, 2f)
                close()
            }
        }.build()
    }
    return rememberVectorPainter(imageVector)
}

