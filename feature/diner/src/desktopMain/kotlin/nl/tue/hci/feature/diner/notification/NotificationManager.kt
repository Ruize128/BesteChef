package nl.tue.hci.feature.diner.notification

import nl.tue.hci.core.notification.NotificationConfig
import nl.tue.hci.core.notification.NotificationState
import nl.tue.hci.core.notification.NotificationType

/**
 * Desktop implementation of notification functions.
 * Uses in-app notifications for desktop platform.
 */
actual fun sendBookingOfferNotification(onNavigate: (() -> Unit)?) {
    if (NotificationConfig.USE_IN_APP_NOTIFICATIONS) {
        // Use in-app notification
        NotificationState.showNotification(
            title = "New Booking Offer",
            message = "You have received a new offer from a chef!",
            type = NotificationType.BOOKING,
            onAction = onNavigate
        )
    } else {
        // Fallback: just print and navigate
        println("Desktop: Booking offer notification")
        onNavigate?.invoke()
    }
}
