package nl.tue.hci.feature.diner.notification

import nl.tue.hci.core.data.PlatformContext

actual fun sendBookingOfferNotification() {
    val context = PlatformContext.context ?: return
    DinerNotificationManager.showBookingOfferNotification(context)
}
