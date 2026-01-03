package nl.tue.hci.feature.chef.notification

actual fun sendBookingConfirmedNotification() {
    // Web version - could use browser notifications or just log
    println("Booking confirmed notification (web)")
}

actual fun sendChatNotification(customerName: String, message: String) {
    // Web version - could use browser notifications or just log
    println("Chat notification (web): $customerName - $message")
}
