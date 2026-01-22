package nl.tue.hci.feature.chef.notification

import nl.tue.hci.core.notification.NotificationConfig
import nl.tue.hci.core.notification.NotificationState
import nl.tue.hci.core.notification.NotificationType

/**
 * Desktop implementation of notification functions.
 * Uses in-app notifications for desktop platform.
 */
actual fun sendBookingConfirmedNotification(onNavigate: (() -> Unit)?) {
    if (NotificationConfig.USE_IN_APP_NOTIFICATIONS) {
        // Use in-app notification
        NotificationState.showNotification(
            title = "Booking Proposal Sent",
            message = "Your booking proposal has been sent successfully",
            type = NotificationType.BOOKING,
            onAction = onNavigate
        )
    } else {
        // Fallback: just print and navigate
        println("Desktop: Booking Proposal Sent notification")
        onNavigate?.invoke()
    }
}

actual fun sendChatNotification(customerName: String, message: String, onNavigate: (() -> Unit)?) {
    if (NotificationConfig.USE_IN_APP_NOTIFICATIONS) {
        // Use in-app notification
        NotificationState.showNotification(
            title = "New message from $customerName",
            message = message,
            type = NotificationType.CHAT,
            onAction = onNavigate
        )
    } else {
        // Fallback: just print and navigate
        println("Desktop: Chat notification from $customerName: $message")
        onNavigate?.invoke()
    }
}
