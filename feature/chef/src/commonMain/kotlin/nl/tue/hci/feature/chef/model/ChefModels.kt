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

