package nl.tue.hci.core.model

import androidx.compose.ui.graphics.Color
import nl.tue.hci.core.ui.AppColors

data class ChatMessage(
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val imagePreview: String? = null, // For image previews like "Yuzu mousse (preview)"
    val avatarText: String = "ME",
    val avatarImageName: String? = null, // Image name for avatar (e.g., "sophie", "ichiraku")
    val avatarColor: Color = AppColors.DinerSecondary,
    val bubbleColor: Color = AppColors.DinerPrimary,
)

