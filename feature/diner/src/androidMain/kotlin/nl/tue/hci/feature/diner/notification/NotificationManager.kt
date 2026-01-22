package nl.tue.hci.feature.diner.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

object DinerNotificationManager {
    private const val CHANNEL_ID = "bestechef_offers_diner"
    private const val CHANNEL_NAME = "BesteChef Booking Offers"
    private const val NOTIFICATION_ID = 2001
    
    fun createOfferNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifications for booking offer messages"
                enableVibration(true)
                setShowBadge(true)
                enableLights(true)
                setBypassDnd(true)
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = android.media.AudioAttributes.Builder()
                    .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showBookingOfferNotification(context: Context) {
        // Use a Handler to post notification from main thread context
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        handler.postDelayed({
            createOfferNotificationChannel(context)
            
            // Check if we have permission to post notifications (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@postDelayed
                }
            }
            
            // Create intent to open MainActivity and navigate to Booking Summary
            val intent = Intent().apply {
                setClassName("nl.tue.hci.bestechef", "nl.tue.hci.bestechef.MainActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "booking_summary")
            }
            
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Create full-screen intent for heads-up notification
            val fullScreenIntent = Intent().apply {
                setClassName("nl.tue.hci.bestechef", "nl.tue.hci.bestechef.MainActivity")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "booking_summary")
            }
            
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt() + 1,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Booking Offer")
                .setContentText("Chef Ichiraku sent you an offer - €150 for 4 guests")
                .setStyle(NotificationCompat.BigTextStyle().bigText("Chef Ichiraku sent you an offer - €150 for 4 guests"))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 500, 250, 500))
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(false)
                .setTimeoutAfter(10000)
            
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }, 100) // Small delay to ensure it's posted after UI is settled
    }
}
