package nl.tue.hci.core.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import nl.tue.hci.core.data.PlatformContext

/**
 * Android implementation: Maps image names to Android drawable resources.
 * 
 * This implementation uses resource name lookup to find drawable resources
 * without needing direct access to the R class.
 */
@Composable
actual fun rememberImagePainter(imageName: String): Painter {
    val context = LocalContext.current
    
    // Map image names to resource names
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
        "ichiraku_menu_cover" -> "ichiraku_menu_cover"
        "ichiraku_menu_cover_2" -> "ichiraku_menu_cover_2"
        "ichiraku_menu_cover_3" -> "ichiraku_menu_cover_3"
        "ichiraku" -> "ichiraku"
        "middle_eastern_cuisine" -> "middle_eastern_cuisine"
        "middle_eastern_cuisine_2" -> "middle_eastern_cuisine_2"
        "middle_eastern_cuisine_3" -> "middle_eastern_cuisine_3"
        "sophie" -> "sophie"
        "chat_icon" -> "chat_icon"
        "google_logo" -> "ic_google_logo"
        "apple_logo" -> "ic_apple_logo"
        "filter_icon" -> "filter_icon"
        "sort_icon" -> "sort_icon"
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
    
    return painterResource(id = resId)
}

