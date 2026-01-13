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
    /**
     * Order Status Transitions for Chef Orders:
     * DRAFT -> SENT -> CONFIRMED -> COMPLETED
     *                           -> CANCELLED
     *
     * DRAFT: Initial state when order is created (not yet sent to customer)
     * SENT: Order offer has been sent to customer
     * CONFIRMED: Customer has confirmed the order
     * COMPLETED: Order has been completed
     * CANCELLED: Order has been cancelled at any point
     */
    DRAFT,      // Order created, waiting to be sent
    SENT,       // Order sent to customer, waiting for confirmation
    CONFIRMED,  // Customer confirmed the order
    COMPLETED,  // Order completed
    CANCELLED   // Order cancelled
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
    val venue: String,
    var status: OrderStatus // Changed to var to allow reassignment
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
    val total: String // Added total field
)

