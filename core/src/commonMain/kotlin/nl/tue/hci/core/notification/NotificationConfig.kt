package nl.tue.hci.core.notification

/**
 * Global notification configuration.
 * 
 * Change USE_IN_APP_NOTIFICATIONS to switch between notification modes:
 * - true: Show notifications inside the app when app is active
 * - false: Use OS built-in notifications (original behavior)
 */
object NotificationConfig {
    /**
     * Set to true to use in-app notifications when the app is active.
     * Set to false to use OS system notifications.
     */
    const val USE_IN_APP_NOTIFICATIONS = true
}
