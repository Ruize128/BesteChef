package nl.tue.hci.core.notification

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Global state manager for in-app notifications
 */
object NotificationState {
    private val _notifications: SnapshotStateList<InAppNotification> = mutableStateListOf()
    val notifications: SnapshotStateList<InAppNotification> get() = _notifications
    
    // Coroutine scope for auto-dismiss
    private val scope = CoroutineScope(Dispatchers.Default)
    
    /**
     * Add a new notification to the list
     */
    fun addNotification(notification: InAppNotification) {
        _notifications.add(0, notification) // Add to top
    }
    
    /**
     * Remove a notification by ID
     */
    fun removeNotification(notificationId: String) {
        _notifications.removeAll { it.id == notificationId }
    }
    
    /**
     * Clear all notifications
     */
    fun clearAll() {
        _notifications.clear()
    }
    
    /**
     * Helper to show a notification with auto-dismiss after delay
     */
    fun showNotification(
        title: String,
        message: String,
        type: NotificationType = NotificationType.INFO,
        onAction: (() -> Unit)? = null,
        autoDismissMillis: Long = 5000
    ) {
        val notification = InAppNotification(
            title = title,
            message = message,
            type = type,
            onAction = onAction
        )
        addNotification(notification)
        
        // Auto-dismiss after delay
        if (autoDismissMillis > 0) {
            scope.launch {
                delay(autoDismissMillis)
                removeNotification(notification.id)
            }
        }
    }
}
