package nl.tue.hci.feature.chef.pages

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
import nl.tue.hci.feature.chef.model.ChatHistoryItem
import nl.tue.hci.core.data.GlobalDatabase

@Composable
fun ChefChatHistoryScreen(
    modifier: Modifier = Modifier,
    onChatClick: (String) -> Unit = {} // customerName
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Read last message from database for Sophie's chat
    val sophieLastMessage = remember {
        val chatMessages = GlobalDatabase.readString("chef_chat_messages").orEmpty()
        if (chatMessages.isNotBlank()) {
            // Get the last message from the chat
            val messages = chatMessages.split("||")
            if (messages.isNotEmpty()) {
                val lastMsg = messages.last()
                val parts = lastMsg.split("|")
                if (parts.size >= 4 && parts[0] == "TEXT") {
                    parts[3] // The text content
                } else {
                    "Can desserts on the menu be replaced with sugar-free options?"
                }
            } else {
                "Can desserts on the menu be replaced with sugar-free options?"
            }
        } else {
            "Can desserts on the menu be replaced with sugar-free options?"
        }
    }
    
    // Read unread count from database
    val unreadCount = remember {
        GlobalDatabase.readString("chef_unread_count")?.toIntOrNull() ?: 1
    }
    
    // Hardcoded chat history data
    val chatHistory = listOf(
        ChatHistoryItem(
            id = "1",
            customerName = "Sophie",
            lastMessage = sophieLastMessage,
            timestamp = "10:16",
            unreadCount = unreadCount
        ),
        ChatHistoryItem(
            id = "2",
            customerName = "Liam",
            lastMessage = "Looking forward to the event!",
            timestamp = "Yesterday",
            unreadCount = 0
        ),
        ChatHistoryItem(
            id = "3",
            customerName = "Emma",
            lastMessage = "Can we adjust the menu for dietary restrictions?",
            timestamp = "2 days ago",
            unreadCount = 0
        ),
        ChatHistoryItem(
            id = "4",
            customerName = "James",
            lastMessage = "Thank you for the amazing service!",
            timestamp = "3 days ago",
            unreadCount = 0
        )
    )
    
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
                    onClick = { onChatClick(chatItem.customerName) }
                )
            }
        }
    }
}

@Composable
private fun ChatHistoryItemCard(
    chatItem: ChatHistoryItem,
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
                text = chatItem.customerName.take(1).uppercase(),
                size = 56,
                backgroundColor = when (chatItem.customerName) {
                    "Sophie" -> colors.imagePlaceholder3 // Light pink
                    "Liam" -> colors.imagePlaceholder1 // Light green
                    "Emma" -> colors.dinerSecondary // Light blue/cyan
                    else -> colors.imagePlaceholder2 // Light orange
                },
                imageName = if (chatItem.customerName == "Sophie") "sophie" else null
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
                        text = chatItem.customerName,
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
                            shape = RoundedCornerShape(12.dp),
                            color = colors.chefPrimary,
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

