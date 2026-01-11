package nl.tue.hci.feature.diner.notification

import nl.tue.hci.core.data.PlatformContext
import nl.tue.hci.core.notification.NotificationConfig
import nl.tue.hci.core.notification.NotificationState
import nl.tue.hci.core.notification.NotificationType

actual fun sendBookingOfferNotification(onNavigate: (() -> Unit)?) {
    if (NotificationConfig.USE_IN_APP_NOTIFICATIONS) {
        // Use in-app notification
        NotificationState.showNotification(
            title = "New Booking Offer",
            message = "You have received a new booking offer from a chef",
            type = NotificationType.OFFER,
            onAction = onNavigate
        )
    } else {
        // Use OS notification
        val context = PlatformContext.context ?: return
        DinerNotificationManager.showBookingOfferNotification(context)
    }
}
