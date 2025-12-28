package nl.tue.hci.core.model

data class ChatMessage(
    val text: String,
    val timestamp: String,
    val isFromMe: Boolean,
    val imagePreview: String? = null // For image previews like "Yuzu mousse (preview)"
)

