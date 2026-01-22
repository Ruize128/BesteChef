package nl.tue.hci.feature.chef.notification

import nl.tue.hci.core.notification.NotificationConfig
import nl.tue.hci.core.notification.NotificationState
import nl.tue.hci.core.notification.NotificationType

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
        // Web version - could use browser notifications or just log
        println("Booking proposal sent notification (web)")
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
        // Web version - could use browser notifications or just log
        println("Chat notification (web): $customerName - $message")
    }
}
