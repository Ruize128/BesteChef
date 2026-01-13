package nl.tue.hci.feature.diner

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

data class ChefMenu(
    val id: String,
    val name: String,
    val description: String,
    val dishCount: Int,
    val priceRange: String,
    val imageColor: Color
)

data class MenuItem(
    val title: String,
    val description: String,
    val serves: String,
    val prepTime: String,
    val imageColor: Color,
    val defaultNumber: Int = 1
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

data class BookingSummaryDetails(
    val location: String,
    val date: String,
    val time: String,
    val guests: Int,
    val venue: String
)

data class BookingSummaryMenuItem(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val imageColor: Color
)

data class BookingPriceSummary(
    val subtotal: String,
    val serviceFee: String,
    val depositAmount: String,
    val depositPercentage: Int
)

