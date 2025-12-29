package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import nl.tue.hci.core.ui.AppColors
import nl.tue.hci.core.ui.components.ChatBubble
import nl.tue.hci.core.model.ChatMessage



@Composable
fun DinerChatScreen(
    chefName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    // Hardcoded initial messages
    // For diner chat: isFromMe=false = chef, isFromMe=true = diner
    val initialMessages = remember {
        listOf(
            ChatMessage(
                text = "Yes! I can replace the original dessert with a nut-free yuzu mousse. Here's a photo.",
                timestamp = "10:12",
                isFromMe = false, // From chef
                avatarText = "DH",
                avatarImageName = "ichiraku", // Chef's avatar
                avatarColor = AppColors.ChefSecondary,
                bubbleColor = AppColors.ChefPrimary, // Chef's bubble color
            ),
            ChatMessage(
                text = "",
                timestamp = "10:13",
                isFromMe = false, // From chef
                imagePreview = "Yuzu mousse (preview)",
                avatarText = "DH",
                avatarImageName = "ichiraku", // Chef's avatar
                avatarColor = AppColors.ChefSecondary,
                bubbleColor = AppColors.ChefPrimary, // Chef's bubble color
            ),
            ChatMessage(
                text = "Thanks — yes please, that would help.",
                timestamp = "10:16",
                isFromMe = true, // From diner
                avatarText = "ME",
                avatarImageName = "sophie", // Diner's avatar
                avatarColor = AppColors.DinerSecondary,
                bubbleColor = AppColors.DinerPrimary, // Diner's bubble color
            )
        )
    }
    
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(initialMessages) } }
    var messageText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    // Scroll to bottom when new message is added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header with status bar padding (full-screen)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chat with $chefName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Online",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50)
                    )
                }
                
                // Placeholder for right side icons (status bar icons in design)
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
        
        // Chat messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date separator
            item {
                DateSeparator(dateText = "Today • Dec 12, 2025")
            }
            
            items(messages) { message ->
                ChatBubble(message = message)
            }
        }
        
        // Message input area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Attachment icon
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Text input
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Write a message...",
                            color = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    ),
                    singleLine = true
                )
                
                // Send button
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            val newMessage = ChatMessage(
                                text = messageText,
                                timestamp = "Now",
                                isFromMe = true, // From diner
                                avatarText = "ME",
                                avatarImageName = "sophie", // Diner's avatar
                                avatarColor = AppColors.DinerSecondary,
                                bubbleColor = AppColors.DinerPrimary, // Diner's bubble color
                            )
                            messages.add(newMessage)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AppColors.DinerPrimary),
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DateSeparator(dateText: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE0E0E0)
        ) {
            Text(
                text = dateText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}


