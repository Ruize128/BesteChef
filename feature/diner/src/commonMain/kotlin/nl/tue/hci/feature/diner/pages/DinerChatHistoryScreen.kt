package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.AppColors
import nl.tue.hci.core.ui.components.Avatar
import nl.tue.hci.feature.diner.DinerChatHistoryItem

@Composable
fun DinerChatHistoryScreen(
    modifier: Modifier = Modifier,
    onChatClick: (String) -> Unit = {} // chefName
) {
    // Hardcoded chat history data (chefs the diner has chatted with)
    val chatHistory = listOf(
        DinerChatHistoryItem(
            id = "2",
            chefName = "Chef Ichiraku",
            lastMessage = "[Image]",
            timestamp = "Yesterday",
            unreadCount = 2
        ),
        DinerChatHistoryItem(
            id = "3",
            chefName = "Chef Elena",
            lastMessage = "I can accommodate all your dietary restrictions.",
            timestamp = "2 days ago",
            unreadCount = 0
        )
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Chats",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
        
        // Chat list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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

@Composable
private fun ChatHistoryItemCard(
    chatItem: DinerChatHistoryItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
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
                    "Sophie" -> Color(0xFFFFB3BA) // Light pink
                    "Chef Marco" -> Color(0xFFB3FFBA) // Light green
                    "Chef Elena" -> Color(0xFFB3BAFF) // Light blue
                    else -> Color(0xFFFFD4B2) // Light orange
                }
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    
                    Text(
                        text = chatItem.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chatItem.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (chatItem.unreadCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AppColors.DinerPrimary, // Use diner primary color
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = chatItem.unreadCount.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

