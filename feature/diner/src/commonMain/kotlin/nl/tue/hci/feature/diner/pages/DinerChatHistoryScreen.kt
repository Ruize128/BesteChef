package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.Avatar
import nl.tue.hci.core.data.GlobalDatabase
import nl.tue.hci.feature.diner.DinerChatHistoryItem

@Composable
fun DinerChatHistoryScreen(
    modifier: Modifier = Modifier,
    onChatClick: (String) -> Unit = {} // chefName
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Load chat history from database - only shows chefs that have been chatted with
    val chatHistory = remember {
        val history = mutableListOf<DinerChatHistoryItem>()
        
        // Check if Ichiraku chef has been chatted with (has a conversation in database)
        val ichirakuMessages = GlobalDatabase.readString("ichiraku_chat_messages")
        if (ichirakuMessages != null && ichirakuMessages.isNotEmpty()) {
            history.add(
                DinerChatHistoryItem(
                    id = "2",
                    chefName = "Chef Ichiraku",
                    lastMessage = "[Image]",
                    timestamp = "Now",
                    unreadCount = 0
                )
            )
        }
        
        history
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "Chats",
                    style = typography.titleLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
        
        // Chat list or empty state
        if (chatHistory.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "No chats yet",
                        style = typography.titleMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Start a chat by clicking the menu icon on a chef's menu",
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            // Chat list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatHistory) { chatItem ->
                    ChatHistoryItemCard(
                        chatItem = chatItem,
                        onClick = { onChatClick(chatItem.chefName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatHistoryItemCard(
    chatItem: DinerChatHistoryItem,
    onClick: () -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Avatar(
                text = chatItem.chefName.take(1).uppercase(),
                size = 56,
                backgroundColor = when (chatItem.chefName) {
                    "Sophie" -> colors.imagePlaceholder3 // Light pink
                    "Chef Marco" -> colors.imagePlaceholder1 // Light green
                    "Chef Elena" -> colors.dinerSecondary // Light blue/cyan
                    else -> colors.imagePlaceholder2 // Light orange
                },
                imageName = if (chatItem.chefName == "Chef Ichiraku") "ichiraku" else null
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chatItem.chefName,
                        style = typography.titleMedium,
                        color = colors.textPrimary
                    )
                    
                    Text(
                        text = chatItem.timestamp,
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chatItem.lastMessage,
                        style = typography.bodyMedium,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (chatItem.unreadCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = colors.dinerPrimary, // Use diner primary color
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = chatItem.unreadCount.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = typography.labelMedium,
                                color = colors.textOnPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

