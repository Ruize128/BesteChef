package nl.tue.hci.feature.diner
import nl.tue.hci.core.ui.AppColors

import androidx.compose.ui.graphics.Color

// Data classes for static data
data class ChefResult(
    val name: String,
    val rating: Float,
    val reviewCount: Int,
    val eventCount: Int,
    val canTravel: Boolean,
    val availableOnDate: Boolean,
    val quote: String,
    val imageColor: Color
)

data class MenuItem(
    val title: String,
    val description: String,
    val serves: String,
    val prepTime: String,
    val imageColor: Color
)

data class ChatMessage(
    val text: String,
    val timestamp: String,
    val isFromChef: Boolean,
    val imagePreview: String? = null // For image previews like "Yuzu mousse (preview)"
)

