package nl.tue.hci.feature.chef.notification

import nl.tue.hci.core.data.PlatformContext
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
        // Use OS notification
        val context = PlatformContext.context ?: return
        NotificationManager.showBookingConfirmedNotification(context)
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
        // Use OS notification
        val context = PlatformContext.context ?: return
        NotificationManager.showChatNotification(context, customerName, message)
    }
}
