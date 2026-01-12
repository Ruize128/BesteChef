package nl.tue.hci.core.model

import androidx.compose.ui.graphics.Color

data class ChatMessage(
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val imagePreview: String? = null, // For image previews like "Yuzu mousse (preview)"
    val bookingOffer: BookingOfferData? = null, // For booking offer messages
    val avatarText: String = "ME",
    val avatarImageName: String? = null, // Image name for avatar (e.g., "sophie", "ichiraku")
    val avatarColor: Color, // Should be provided from theme
    val bubbleColor: Color, // Should be provided from theme
)

data class BookingOfferData(
    val date: String,
    val time: String,
    val guests: String,
    val venue: String,
    val price: String
)

