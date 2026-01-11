package nl.tue.hci.feature.chef.notification

// Common expect functions
expect fun sendBookingConfirmedNotification(onNavigate: (() -> Unit)? = null)
expect fun sendChatNotification(customerName: String, message: String, onNavigate: (() -> Unit)? = null)
