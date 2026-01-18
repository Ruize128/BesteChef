package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.clickable
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
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.BesteChefColors
import nl.tue.hci.core.ui.components.ChatBubble
import nl.tue.hci.core.ui.components.ImagePreviewOverlay
import nl.tue.hci.core.ui.PlatformBackHandler
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.model.ChatMessage
import nl.tue.hci.core.data.GlobalDatabase



@Composable
fun DinerChatScreen(
    chefName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onBookingOfferClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Full-screen image preview state
    var showImagePreview by rememberSaveable { mutableStateOf(false) }
    var previewImageName by rememberSaveable { mutableStateOf<String?>(null) }
    
    // Load messages from database or use default initial messages
    // For diner chat: isFromMe=false = chef, isFromMe=true = diner
    val messages = remember(colors) {
        mutableStateListOf<ChatMessage>().apply {
            val loadedMessages = loadChatMessagesFromDatabase(colors)
            if (loadedMessages.isNotEmpty()) {
                addAll(loadedMessages)
            } else {
                // Default initial messages if database is empty
                addAll(
                    listOf(
                        ChatMessage(
                            text = "Yes! I can replace the original dessert with a nut-free yuzu mousse. Here's a photo.",
                            timestamp = "10:12",
                            isFromMe = false,
                            avatarText = "DH",
                            avatarImageName = "ichiraku",
                            avatarColor = colors.chefSecondary,
                            bubbleColor = colors.chefPrimary,
                        ),
                        ChatMessage(
                            text = "",
                            timestamp = "10:13",
                            isFromMe = false,
                            imagePreview = "Yuzu mousse (preview)",
                            avatarText = "DH",
                            avatarImageName = "ichiraku",
                            avatarColor = colors.chefSecondary,
                            bubbleColor = colors.chefPrimary,
                        ),
                        ChatMessage(
                            text = "Thanks — yes please, that would help.",
                            timestamp = "10:16",
                            isFromMe = true,
                            avatarText = "ME",
                            avatarImageName = "sophie",
                            avatarColor = colors.dinerSecondary,
                            bubbleColor = colors.dinerPrimary,
                        ),
                        ChatMessage(
                            text = "Here's my offer for your event:",
                            timestamp = "10:20",
                            isFromMe = false,
                            bookingOffer = nl.tue.hci.core.model.BookingOfferData(
                                date = "Dec 12, 2025",
                                time = "18:30",
                                guests = "6 guests",
                                venue = "Private Dining Room",
                                price = "€250"
                            ),
                            avatarText = "DH",
                            avatarImageName = "ichiraku",
                            avatarColor = colors.chefSecondary,
                            bubbleColor = colors.chefPrimary,
                        )
                    )
                )
                // Save initial messages to database
                saveChatMessagesToDatabase(this)
            }
        }
    }
    var messageText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    // Scroll to bottom when new message is added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            // Use immediate scroll (non-suspending) to avoid entering suspend/animation code paths
            // on wasm targets (these can cause the compiled wasm to require Wasm GC / Exception-Handling
            // proposals which are not available in all browsers). Using scrollToItem keeps behavior
            // compatible while providing a safe fallback.
            listState.scrollToItem(messages.size - 1)
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {
        // Header with status bar padding (full-screen)
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (showImagePreview) {
                            showImagePreview = false
                            previewImageName = null
                        } else {
                            onBackClick()
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chat with $chefName",
                        style = typography.cardTitle,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Online",
                        style = typography.bodySmall,
                        color = colors.onlineIndicator
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
                ChatBubble(
                    message = message,
                    onBookingOfferClick = onBookingOfferClick,
                    onImageClick = { imageName ->
                        previewImageName = imageName
                        showImagePreview = true
                    }
                )
            }
        }
        
        // Message input area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Attachment icon
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach",
                        tint = colors.textTertiary,
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
                            color = colors.textTertiary
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = colors.surfaceVariant,
                        unfocusedContainerColor = colors.surfaceVariant
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
                                avatarColor = colors.dinerSecondary,
                                bubbleColor = colors.dinerPrimary, // Diner's bubble color
                            )
                            messages.add(newMessage)
                            // Save messages to database
                            saveChatMessagesToDatabase(messages)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.dinerPrimary),
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = colors.textOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Full-screen image preview overlay
    PlatformBackHandler(enabled = showImagePreview) {
        showImagePreview = false
        previewImageName = null
    }
    ImagePreviewOverlay(
        showPreview = showImagePreview,
        imageName = previewImageName,
        onDismiss = {
            showImagePreview = false
            previewImageName = null
        }
    )
}

@Composable
fun DateSeparator(dateText: String) {
    val colors = BesteChefThemeColors.current()
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.outline
        ) {
            Text(
                text = dateText,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary
            )
        }
    }
}

/**
 * Save chat messages to database using a simplified encoding format.
 * Special message types (images, booking offers) are encoded with markers.
 */
private fun saveChatMessagesToDatabase(messages: List<ChatMessage>) {
    val encodedMessages = messages.map { message ->
        when {
            // Image message: "IMAGE|timestamp|isFromMe"
            message.imagePreview != null -> {
                "IMAGE|${message.timestamp}|${message.isFromMe}"
            }
            // Booking offer message: "BOOKING|timestamp|isFromMe"
            message.bookingOffer != null -> {
                "BOOKING|${message.timestamp}|${message.isFromMe}"
            }
            // Text message: "TEXT|timestamp|isFromMe|messageText"
            else -> {
                "TEXT|${message.timestamp}|${message.isFromMe}|${message.text}"
            }
        }
    }
    
    // Store as pipe-separated string
    GlobalDatabase.writeString(
        "ichiraku_chat_messages",
        encodedMessages.joinToString("||")
    )
}

/**
 * Load chat messages from database and reconstruct ChatMessage objects.
 * Uses theme colors for avatars and bubbles.
 */
private fun loadChatMessagesFromDatabase(
    colors: BesteChefColors
): List<ChatMessage> {
    val storedData = GlobalDatabase.readString("ichiraku_chat_messages") ?: return emptyList()
    
    if (storedData.isBlank()) return emptyList()
    
    return storedData.split("||").mapNotNull { encodedMessage ->
        val parts = encodedMessage.split("|")
        if (parts.size < 3) return@mapNotNull null
        
        val type = parts[0]
        val timestamp = parts[1]
        val isFromMe = parts[2].toBoolean()
        
        when (type) {
            "IMAGE" -> {
                // Reconstruct image message with fixed demo content
                ChatMessage(
                    text = "",
                    timestamp = timestamp,
                    isFromMe = isFromMe,
                    imagePreview = "Yuzu mousse (preview)",
                    avatarText = if (isFromMe) "ME" else "DH",
                    avatarImageName = if (isFromMe) "sophie" else "ichiraku",
                    avatarColor = if (isFromMe) colors.dinerSecondary else colors.chefSecondary,
                    bubbleColor = if (isFromMe) colors.dinerPrimary else colors.chefPrimary,
                )
            }
            "BOOKING" -> {
                // Reconstruct booking offer message with fixed demo content
                ChatMessage(
                    text = "Here's my offer for your event:",
                    timestamp = timestamp,
                    isFromMe = isFromMe,
                    bookingOffer = nl.tue.hci.core.model.BookingOfferData(
                        date = "Dec 12, 2025",
                        time = "18:30",
                        guests = "6 guests",
                        venue = "Private Dining Room",
                        price = "€250"
                    ),
                    avatarText = if (isFromMe) "ME" else "DH",
                    avatarImageName = if (isFromMe) "sophie" else "ichiraku",
                    avatarColor = if (isFromMe) colors.dinerSecondary else colors.chefSecondary,
                    bubbleColor = if (isFromMe) colors.dinerPrimary else colors.chefPrimary,
                )
            }
            "TEXT" -> {
                // Reconstruct text message
                if (parts.size < 4) return@mapNotNull null
                val text = parts.drop(3).joinToString("|") // Handle pipe characters in message text
                
                ChatMessage(
                    text = text,
                    timestamp = timestamp,
                    isFromMe = isFromMe,
                    avatarText = if (isFromMe) "ME" else "DH",
                    avatarImageName = if (isFromMe) "sophie" else "ichiraku",
                    avatarColor = if (isFromMe) colors.dinerSecondary else colors.chefSecondary,
                    bubbleColor = if (isFromMe) colors.dinerPrimary else colors.chefPrimary,
                )
            }
            else -> null
        }
    }
}


