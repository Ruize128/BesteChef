package nl.tue.hci.core.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.notification.InAppNotification
import nl.tue.hci.core.notification.NotificationState
import nl.tue.hci.core.notification.NotificationType
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Overlay component that displays in-app notifications at the top of the screen
 */
@Composable
fun InAppNotificationOverlay(
    modifier: Modifier = Modifier
) {
    val notifications = NotificationState.notifications
    val colors = BesteChefThemeColors.current()
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            notifications.forEach { notification ->
                key(notification.id) {
                    InAppNotificationCard(
                        notification = notification,
                        onDismiss = { NotificationState.removeNotification(notification.id) },
                        onClick = {
                            notification.onAction?.invoke()
                            NotificationState.removeNotification(notification.id)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Individual notification card with slide-in animation
 */
@Composable
private fun InAppNotificationCard(
    notification: InAppNotification,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut()
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            color = colors.surfaceVariant,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon based on notification type
                val icon: ImageVector
                val iconColor: Color
                when (notification.type) {
                    NotificationType.CHAT -> {
                        icon = Icons.Default.Email
                        iconColor = colors.dinerPrimary
                    }
                    NotificationType.BOOKING -> {
                        icon = Icons.Default.CheckCircle
                        iconColor = colors.chefPrimary
                    }
                    NotificationType.OFFER -> {
                        icon = Icons.Default.Notifications
                        iconColor = colors.onlineIndicator
                    }
                    NotificationType.INFO -> {
                        icon = Icons.Default.Info
                        iconColor = colors.textSecondary
                    }
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
                
                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = notification.title,
                        style = typography.cardTitle,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = notification.message,
                        style = typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
                
                // Close button
                IconButton(
                    onClick = {
                        visible = false
                        CoroutineScope(Dispatchers.Default).launch {
                            delay(300)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
