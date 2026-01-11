package nl.tue.hci.core.notification

import kotlin.random.Random

/**
 * Data class representing an in-app notification
 */
data class InAppNotification(
    val id: String = "notif_${Random.nextInt()}",
    val title: String,
    val message: String,
    val type: NotificationType = NotificationType.INFO,
    val onAction: (() -> Unit)? = null
)

enum class NotificationType {
    INFO,
    CHAT,
    BOOKING,
    OFFER
}
