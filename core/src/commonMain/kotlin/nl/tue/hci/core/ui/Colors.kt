package nl.tue.hci.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Legacy AppColors object for backward compatibility.
 * @deprecated Use BesteChefThemeColors.current instead for theme-aware colors.
 * This will be removed in a future version.
 */
@Deprecated("Use BesteChefThemeColors.current for theme-aware colors", ReplaceWith("BesteChefThemeColors.current"))
object AppColors {
    // Legacy properties - default to light theme colors for backward compatibility
    val ChefPrimary: Color = Color(0xFFFFA958)
    val ChefSecondary: Color = Color(0xFFFFC997)
    val DinerPrimary: Color = Color(0xFF22F3BB)
    val DinerSecondary: Color = Color(0xFF99F8DF)
    val TextPrimary: Color = Color(0xFF212121)
    val TextSecondary: Color = Color(0xFF757575)
    val OutlineLight: Color = Color(0xFFE0E0E0)
    val ButtonGrey: Color = Color(0xFFF2F4F7)
    val White: Color = Color(0xFFFFFFFF)
    val Black: Color = Color(0xFF000000)
    val StatusNewBackground: Color = Color(0xFFFFEBEE)
    val StatusNewText: Color = Color(0xFFD32F2F)
    val StatusConfirmedBackground: Color = Color(0xFFE8F5E9)
    val StatusConfirmedText: Color = Color(0xFF2E7D32)
}

