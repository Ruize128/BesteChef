package nl.tue.hci.feature.chef.model

import androidx.compose.ui.graphics.Color

data class ChefStats(
    val totalOrders: Int,
    val totalRevenue: String,
    val totalReviews: Int
)

data class ChefMenuItem(
    val id: String,
    val title: String,
    val description: String,
    val serves: String,
    val prepTime: String,
    val imageColor: Color
)

data class BookingInquiry(
    val id: String,
    val customerName: String,
    val message: String,
    val date: String,
    val guests: Int,
    val timeAgo: String,
    val status: BookingStatus,
    val statusLabel: String
)

enum class BookingStatus {
    NEW,
    CONFIRMED,
    UNANSWERED
}

data class ChatHistoryItem(
    val id: String,
    val customerName: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0
)

data class Order(
    val id: String,
    val customerName: String,
    val orderDate: String,
    val status: OrderStatus,
    val totalPrice: String,
    val itemCount: Int,
    val timeAgo: String
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

data class MenuPickerItem(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val imageColor: Color,
    val dietaryTag: String? = null,
    val dietaryTagColor: Color? = null,
    val category: String = "All"
)

data class SelectedMenuItem(
    val menuItem: MenuPickerItem,
    val quantity: Int = 1
)

data class OrderDetails(
    val date: String,
    val time: String,
    val guests: Int,
    val venue: String
)

data class OfferMenuItem(
    val id: String,
    val title: String,
    val description: String,
    val price: String,
    val imageColor: Color,
    val quantity: Int = 1
)

data class PriceSummary(
    val subtotal: String,
    val serviceFee: String,
    val depositAmount: String,
    val depositPercentage: Int,
    val total: String
)

