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

data class DinerChatHistoryItem(
    val id: String,
    val chefName: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0
)

data class DinerOrder(
    val id: String,
    val chefName: String,
    val orderDate: String,
    val status: DinerOrderStatus,
    val totalPrice: String,
    val itemCount: Int,
    val timeAgo: String
)

enum class DinerOrderStatus {
    PENDING,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

