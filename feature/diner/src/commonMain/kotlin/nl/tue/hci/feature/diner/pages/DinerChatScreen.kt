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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.statusBarsPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.BesteChefColors
import nl.tue.hci.core.ui.components.ChatBubble
import nl.tue.hci.core.ui.components.ImagePreviewOverlay
import nl.tue.hci.core.ui.components.InAppNotificationOverlay
import nl.tue.hci.core.ui.PlatformBackHandler
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.model.ChatMessage
import nl.tue.hci.core.data.GlobalDatabase
import nl.tue.hci.core.notification.NotificationState
import nl.tue.hci.core.notification.NotificationType



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
    
    // Load messages from database or start with empty list
    // For diner chat: isFromMe=false = chef, isFromMe=true = diner
    val messages = remember(colors) {
        mutableStateListOf<ChatMessage>().apply {
            val loadedMessages = loadChatMessagesFromDatabase(colors)
            if (loadedMessages.isNotEmpty()) {
                addAll(loadedMessages)
            }
            // Start with empty messages for demo
        }
    }
    
    // Track conversation state for auto-replies
    var conversationState by rememberSaveable { mutableStateOf(0) } // 0=initial, 1=after first message, 2=after second
    var isAutoReplying by remember { mutableStateOf(false) }
    
    // Check if automatic messages have been shown (chef has already replied)
    val hasAutoMessagesShown = messages.any { !it.isFromMe && it.text.contains("Yes! I can") }
    
    // Initialize message text with default message only if auto messages haven't been shown
    var messageText by rememberSaveable { 
        mutableStateOf(
            if (hasAutoMessagesShown) {
                "" // Empty if conversation has progressed
            } else {
                when (conversationState) {
                    0 -> "Can desserts on the menu be replaced with sugar-free options?"
                    1 -> "Thanks — yes please, that would help."
                    else -> ""
                }
            }
        )
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    // Initialize chat in database when screen opens for the first time
    LaunchedEffect(Unit) {
        // Mark that this chat has been opened (add a marker message if no messages exist)
        val existingMessages = GlobalDatabase.readString("ichiraku_chat_messages")
        if (existingMessages == null || existingMessages.isEmpty()) {
            // Initialize with a marker message so the chat appears in the list
            GlobalDatabase.writeString("ichiraku_chat_messages", "INIT")
        }
    }
    
    // Scroll to bottom when new message is added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }
    
    // Auto-reply logic based on conversation state
    LaunchedEffect(isAutoReplying, conversationState) {
        if (isAutoReplying) {
            delay(500)
            // Show typing indicator message for 2 seconds
            val typingMessage = ChatMessage(
                text = "",
                timestamp = "Now",
                isFromMe = false,
                isTyping = true,
                avatarText = "DH",
                avatarImageName = "ichiraku",
                avatarColor = colors.chefSecondary,
                bubbleColor = colors.chefPrimary,
            )
            messages.add(typingMessage)
            delay(2000)
            // Remove typing indicator message
            messages.removeAt(messages.size - 1)
            
            // Then add the message after typing indicator disappears
            delay(100) // Small delay for smooth transition
            
            if (conversationState == 0) {
                // First auto-reply: chef's text response
                messages.add(
                    ChatMessage(
                        text = "Yes! I can replace the original dessert with a nut-free yuzu mousse. Here's a photo.",
                        timestamp = "Now",
                        isFromMe = false,
                        avatarText = "DH",
                        avatarImageName = "ichiraku",
                        avatarColor = colors.chefSecondary,
                        bubbleColor = colors.chefPrimary,
                    )
                )
                saveChatMessagesToDatabase(messages)
                
                delay(1000) // Short delay before next item
                
                // Then the image message
                messages.add(
                    ChatMessage(
                        text = "",
                        timestamp = "Now",
                        isFromMe = false,
                        imagePreview = "Yuzu mousse (preview)",
                        avatarText = "DH",
                        avatarImageName = "ichiraku",
                        avatarColor = colors.chefSecondary,
                        bubbleColor = colors.chefPrimary,
                    )
                )
                saveChatMessagesToDatabase(messages)
                
                // Only now change the placeholder after auto-reply is complete
                conversationState = 1
                messageText = "Thanks — yes please, that would help."
                
            } else if (conversationState == 1) {
                // Second auto-reply: booking offer card
                messages.add(
                    ChatMessage(
                        text = "Here's my offer for your event:",
                        timestamp = "Now",
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
                saveChatMessagesToDatabase(messages)
                
                // Show in-app notification for the new booking offer with a small delay
                scope.launch {
                    delay(200) // Small delay to ensure UI is ready
                    NotificationState.showNotification(
                        title = "New Booking Offer",
                        message = "Chef $chefName sent you a booking offer",
                        type = NotificationType.OFFER,
                        onAction = {
                            onBookingOfferClick()
                        },
                        autoDismissMillis = 5000
                    )
                }
                
                // Create a new order in the database when booking offer is sent
                GlobalDatabase.writeString("ichiraku_order_status", "PENDING")
                
                // Update state after second auto-reply
                conversationState = 2
            }
            
            isAutoReplying = false
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
                    maxLines = 5 // Allow up to 5 lines with word wrapping
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
                            saveChatMessagesToDatabase(messages)
                            messageText = ""
                            
                            // Trigger auto-reply based on conversation state
                            // Don't increment conversationState here - it will be done after auto-reply completes
                            if (conversationState < 2) {
                                isAutoReplying = true
                            }
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
    
    // In-app notification overlay - displayed on top of everything
    InAppNotificationOverlay()
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


