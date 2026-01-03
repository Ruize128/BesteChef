package nl.tue.hci.feature.chef.notification

import nl.tue.hci.core.data.PlatformContext

actual fun sendBookingConfirmedNotification() {
    val context = PlatformContext.context ?: return
    NotificationManager.showBookingConfirmedNotification(context)
}

actual fun sendChatNotification(customerName: String, message: String) {
    val context = PlatformContext.context ?: return
    NotificationManager.showChatNotification(context, customerName, message)
}
