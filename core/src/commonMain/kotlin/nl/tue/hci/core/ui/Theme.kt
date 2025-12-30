package nl.tue.hci.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Comprehensive color scheme for BesteChef application.
 * Supports both light and dark themes.
 */
interface BesteChefColors {
    // Brand colors
    val chefPrimary: Color
    val chefSecondary: Color
    val dinerPrimary: Color
    val dinerSecondary: Color
    
    // Background colors
    val background: Color
    val surface: Color
    val surfaceVariant: Color // For cards, elevated surfaces
    
    // Text colors
    val textPrimary: Color
    val textSecondary: Color
    val textTertiary: Color // For disabled/placeholder text
    val textOnPrimary: Color // Text on primary colored backgrounds
    val textOnSecondary: Color // Text on secondary colored backgrounds
    
    // UI element colors
    val outline: Color // Borders, dividers
    val outlineVariant: Color // Lighter borders
    val buttonBackground: Color // Default button background
    val buttonBackgroundDisabled: Color
    
    // Status colors
    val statusNewBackground: Color
    val statusNewText: Color
    val statusConfirmedBackground: Color
    val statusConfirmedText: Color
    
    // Special colors
    val onlineIndicator: Color
    val favoriteIcon: Color
    val error: Color
    val errorBackground: Color
    
    // Image placeholder colors (for when images aren't available)
    val imagePlaceholder1: Color // Light green
    val imagePlaceholder2: Color // Light orange/peach
    val imagePlaceholder3: Color // Light pink
    val imagePlaceholder4: Color // Light beige
}

/**
 * Light theme colors
 */
private val LightBesteChefColors = object : BesteChefColors {
    override val chefPrimary = Color(0xFFFFA958)
    override val chefSecondary = Color(0xFFFFC997)
    override val dinerPrimary = Color(0xFF22F3BB)
    override val dinerSecondary = Color(0xFF99F8DF)
    
    override val background = Color(0xFFFFFFFF) // White
    override val surface = Color(0xFFFFFFFF) // White
    override val surfaceVariant = Color(0xFFF5F5F5) // Light gray for cards
    
    override val textPrimary = Color(0xFF212121) // Almost black
    override val textSecondary = Color(0xFF757575) // Medium gray
    override val textTertiary = Color(0xFF9E9E9E) // Light gray
    override val textOnPrimary = Color(0xFFFFFFFF) // White
    override val textOnSecondary = Color(0xFF212121) // Dark text
    
    override val outline = Color(0xFFE0E0E0) // Light gray
    override val outlineVariant = Color(0xFFF5F5F5) // Very light gray
    override val buttonBackground = Color(0xFFF2F4F7) // Button grey
    override val buttonBackgroundDisabled = Color(0xFFE0E0E0)
    
    override val statusNewBackground = Color(0xFFFFEBEE) // Light red
    override val statusNewText = Color(0xFFD32F2F) // Red
    override val statusConfirmedBackground = Color(0xFFE8F5E9) // Light green
    override val statusConfirmedText = Color(0xFF2E7D32) // Green
    
    override val onlineIndicator = Color(0xFF4CAF50) // Green
    override val favoriteIcon = Color(0xFFFFD700) // Gold
    override val error = Color(0xFFD32F2F) // Red
    override val errorBackground = Color(0xFFFFEBEE) // Light red
    
    override val imagePlaceholder1 = Color(0xFFB2E5D4) // Light green
    override val imagePlaceholder2 = Color(0xFFFFD4B2) // Light orange/peach
    override val imagePlaceholder3 = Color(0xFFFFB3BA) // Light pink
    override val imagePlaceholder4 = Color(0xFFE8D5C4) // Light beige
}

/**
 * Dark theme colors
 */
private val DarkBesteChefColors = object : BesteChefColors {
    override val chefPrimary = Color(0xFFE77B34) // Slightly brighter orange
    override val chefSecondary = Color(0xFFD27F42) // Muted orange
    override val dinerPrimary = Color(0xFF18C49B) // Brighter cyan
    override val dinerSecondary = Color(0xFF53BFAE) // Muted cyan
    
    override val background = Color(0xFF121212) // Dark background
    override val surface = Color(0xFF1E1E1E) // Dark surface
    override val surfaceVariant = Color(0xFF2C2C2C) // Darker for cards
    
    override val textPrimary = Color(0xFFECECEC) // Light text
    override val textSecondary = Color(0xFFB0B0B0) // Medium light gray
    override val textTertiary = Color(0xFF808080) // Darker gray
    override val textOnPrimary = Color(0xFF121212) // Dark text on light primary
    override val textOnSecondary = Color(0xFFE0E0E0) // Light text
    
    override val outline = Color(0xFF424242) // Dark outline
    override val outlineVariant = Color(0xFF2C2C2C) // Darker outline
    override val buttonBackground = Color(0xFF2C2C2C) // Dark button
    override val buttonBackgroundDisabled = Color(0xFF1E1E1E)
    
    override val statusNewBackground = Color(0xFF4A1F1F) // Dark red background
    override val statusNewText = Color(0xFFFF5252) // Brighter red
    override val statusConfirmedBackground = Color(0xFF1F4A1F) // Dark green background
    override val statusConfirmedText = Color(0xFF66BB6A) // Brighter green
    
    override val onlineIndicator = Color(0xFF66BB6A) // Brighter green
    override val favoriteIcon = Color(0xFFFFD700) // Gold (same)
    override val error = Color(0xFFFF5252) // Brighter red
    override val errorBackground = Color(0xFF4A1F1F) // Dark red background
    
    override val imagePlaceholder1 = Color(0xFF2D4A3D) // Dark green
    override val imagePlaceholder2 = Color(0xFF4A3D2D) // Dark orange
    override val imagePlaceholder3 = Color(0xFF4A2D3D) // Dark pink
    override val imagePlaceholder4 = Color(0xFF3D3D2D) // Dark beige
}

/**
 * CompositionLocal for providing theme colors to all composables
 */
val LocalBesteChefColors = compositionLocalOf<BesteChefColors> {
    error("No BesteChefColors provided! Wrap your app with BesteChefTheme.")
}

/**
 * Helper object for easy access to theme colors
 */
object BesteChefThemeColors {
    @Composable
    fun current(): BesteChefColors = LocalBesteChefColors.current
}

/**
 * BesteChef theme composable that provides colors and MaterialTheme
 */
@Composable
fun BesteChefTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkBesteChefColors else LightBesteChefColors
    
    CompositionLocalProvider(LocalBesteChefColors provides colors) {
        MaterialTheme(
            colorScheme = if (darkTheme) {
                darkColorScheme(
                    primary = colors.chefPrimary,
                    secondary = colors.dinerPrimary,
                    background = colors.background,
                    surface = colors.surface,
                    error = colors.error,
                    onPrimary = colors.textOnPrimary,
                    onSecondary = colors.textOnSecondary,
                    onBackground = colors.textPrimary,
                    onSurface = colors.textPrimary,
                    onError = colors.textOnPrimary,
                )
            } else {
                lightColorScheme(
                    primary = colors.chefPrimary,
                    secondary = colors.dinerPrimary,
                    background = colors.background,
                    surface = colors.surface,
                    error = colors.error,
                    onPrimary = colors.textOnPrimary,
                    onSecondary = colors.textOnSecondary,
                    onBackground = colors.textPrimary,
                    onSurface = colors.textPrimary,
                    onError = colors.textOnPrimary,
                )
            },
            content = content
        )
    }
}

