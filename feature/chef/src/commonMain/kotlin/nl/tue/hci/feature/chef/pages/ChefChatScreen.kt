package nl.tue.hci.feature.chef.pages

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.style.TextAlign
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.BesteChefColors
import nl.tue.hci.core.ui.components.ChatBubble
import nl.tue.hci.core.ui.components.ImagePreviewOverlay
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.model.ChatMessage
import nl.tue.hci.core.data.GlobalDatabase

@Composable
fun ChefChatScreen(
    customerName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditOrderClick: () -> Unit = {},
    onBookingOfferClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Full-screen image preview state
    var showImagePreview by rememberSaveable { mutableStateOf(false) }
    var previewImageName by rememberSaveable { mutableStateOf<String?>(null) }
    
    // Load messages from database or start with Sophie's initial message
    // For chef chat: isFromMe=true = chef, isFromMe=false = customer
    val messages = remember(colors) {
        mutableStateListOf<ChatMessage>().apply {
            val loadedMessages = loadChefChatMessagesFromDatabase(colors)
            if (loadedMessages.isNotEmpty()) {
                addAll(loadedMessages)
            } else {
                // Show Sophie's initial message
                add(
                    ChatMessage(
                        text = "Can desserts on the menu be replaced with sugar-free options?",
                        timestamp = "10:10",
                        isFromMe = false, // From customer
                        avatarText = "DH",
                        avatarImageName = "sophie",
                        avatarColor = colors.dinerSecondary,
                        bubbleColor = colors.dinerPrimary,
                    )
                )
                saveChatMessagesToDatabase(this)
            }
        }
    }
    
    // Track conversation state for auto-replies
    var conversationState by rememberSaveable { mutableStateOf(0) } // 0=initial, 1=after image sent
    var isAutoReplying by remember { mutableStateOf(false) }
    var showImageBubble by rememberSaveable { mutableStateOf(false) } // Show image bubble after first message sent
    
    // Check if automatic messages have been shown (customer has already replied)
    val hasAutoMessagesShown = messages.any { !it.isFromMe && it.text.contains("Thanks") }
    
    // Initialize message text with default response only if auto messages haven't been shown
    var messageText by rememberSaveable { 
        mutableStateOf(
            if (hasAutoMessagesShown) {
                "" // Empty if conversation has progressed
            } else {
                "Yes! I can replace the original dessert with a nut-free yuzu mousse. Here's a photo."
            }
        )
    }
    
    val listState = rememberLazyListState()
    
    // Scroll to bottom when new message is added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }
    
    // Auto-reply logic for customer responses
    LaunchedEffect(isAutoReplying) {
        if (isAutoReplying) {
            kotlinx.coroutines.delay(2000) // Wait 2 seconds
            
            if (conversationState == 1) {
                // Sophie replies after receiving image
                messages.add(
                    ChatMessage(
                        text = "Thanks — yes please, that would help.",
                        timestamp = "Now",
                        isFromMe = false, // From customer
                        avatarText = "DH",
                        avatarImageName = "sophie",
                        avatarColor = colors.dinerSecondary,
                        bubbleColor = colors.dinerPrimary,
                    )
                )
                saveChatMessagesToDatabase(messages)
                conversationState = 2
            }
            
            isAutoReplying = false
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surfaceVariant)
        ) {
        // Header with status bar padding
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
                    .padding(16.dp),
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
                        tint = colors.textPrimary
                    )
                }
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chat with $customerName",
                        style = typography.cardTitle,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Online",
                        style = typography.bodySmall,
                        color = colors.onlineIndicator,
                    )
                }
                
                // Placeholder for right side icons (status bar icons in design)
                Spacer(modifier = Modifier.size(40.dp))
            }
        }
        
        // Box to overlay floating dish bar on chat messages
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Chat messages area (behind the floating bar)
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 88.dp, // Space for floating bar
                    bottom = 16.dp
                ),
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
            
            // Floating Dish description bar (on top)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .align(Alignment.TopCenter),
                color = colors.surface,
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 3.dp,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dish image
                    Image(
                        painter = rememberImagePainter("omakase_5_course"),
                        contentDescription = "5-course Omakase",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Dish name and price
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "5-course Omakase",
                            style = typography.cardTitle,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "€120",
                            style = typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                    
                    // Edit Offer button
                    Button(
                        onClick = onEditOrderClick,
                        modifier = Modifier
                            .height(32.dp)
                            .widthIn(min = 60.dp),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.chefPrimary,
                            contentColor = colors.textOnPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Text(
                            text = "Edit Offer",
                            style = typography.buttonText,
                        )
                    }
                }
            }


            // Floating image bubble above the + button
            if (showImageBubble) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 16.dp)
                ) {
                    // Main bubble
                    Surface(
                        modifier = Modifier
                            .size(width = 160.dp, height = 120.dp)
                            .clickable {
                                messages.add(
                                    ChatMessage(
                                        text = "",
                                        timestamp = "Now",
                                        isFromMe = true,
                                        imagePreview = "Yuzu mousse (preview)",
                                        avatarText = "ME",
                                        avatarImageName = "ichiraku",
                                        avatarColor = colors.chefSecondary,
                                        bubbleColor = colors.chefPrimary,
                                    )
                                )
                                saveChatMessagesToDatabase(messages)
                                showImageBubble = false
                                conversationState = 1
                                isAutoReplying = true
                            },
                        color = colors.surface,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 3.dp,
                        tonalElevation = 0.dp
                    ) {
                        Image(
                            painter = rememberImagePainter("yuzu_mousse"),
                            contentDescription = "Yuzu mousse preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Triangle tail pointing to bottom left
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(10.dp)
                            .align(Alignment.BottomStart)
                            .offset(y = 9.dp, x = 16.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = object : androidx.compose.ui.graphics.Shape {
                                    override fun createOutline(
                                        size: androidx.compose.ui.geometry.Size,
                                        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                                        density: androidx.compose.ui.unit.Density
                                    ): androidx.compose.ui.graphics.Outline {
                                        val trianglePath = androidx.compose.ui.graphics.Path().apply {
                                            moveTo(size.width, 0f) // Top right
                                            lineTo(0f, 0f) // Top left
                                            lineTo(size.width / 2, size.height) // Bottom center
                                            close()
                                        }
                                        return androidx.compose.ui.graphics.Outline.Generic(trianglePath)
                                    }
                                }
                            )
                            .background(
                                color = colors.surface,
                                shape = object : androidx.compose.ui.graphics.Shape {
                                    override fun createOutline(
                                        size: androidx.compose.ui.geometry.Size,
                                        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                                        density: androidx.compose.ui.unit.Density
                                    ): androidx.compose.ui.graphics.Outline {
                                        val trianglePath = androidx.compose.ui.graphics.Path().apply {
                                            moveTo(size.width, 0f) // Top right
                                            lineTo(0f, 0f) // Top left
                                            lineTo(size.width / 2, size.height) // Bottom center
                                            close()
                                        }
                                        return androidx.compose.ui.graphics.Outline.Generic(trianglePath)
                                    }
                                }
                            ),
                    )
                }
            }
        }
        
        // Message input area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
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
                    maxLines = 5
                )
                
                // Send button
                IconButton(
                    onClick = {
                        // If image bubble is showing, send the image
                        if (showImageBubble) {
                            messages.add(
                                ChatMessage(
                                    text = "",
                                    timestamp = "Now",
                                    isFromMe = true,
                                    imagePreview = "Yuzu mousse (preview)",
                                    avatarText = "ME",
                                    avatarImageName = "ichiraku",
                                    avatarColor = colors.chefSecondary,
                                    bubbleColor = colors.chefPrimary,
                                )
                            )
                            saveChatMessagesToDatabase(messages)
                            showImageBubble = false
                            conversationState = 1
                            isAutoReplying = true
                        } else if (messageText.isNotBlank()) {
                            // Otherwise send text message
                            val newMessage = ChatMessage(
                                text = messageText,
                                timestamp = "Now",
                                isFromMe = true, // From chef
                                avatarText = "ME",
                                avatarImageName = "ichiraku", // Chef's avatar
                                avatarColor = colors.chefSecondary,
                                bubbleColor = colors.chefPrimary, // Chef's bubble color
                            )
                            messages.add(newMessage)
                            saveChatMessagesToDatabase(messages)
                            messageText = ""
                            
                            // Show image bubble after sending first text message
                            showImageBubble = true
                            conversationState = 0
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.chefPrimary),
                    enabled = messageText.isNotBlank() || showImageBubble
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
        } // End Column
        

    }

    // Full-screen image preview overlay
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
    val typography = BesteChefThemeTypography.current()
    
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
                style = typography.bodySmall,
                color = colors.textTertiary
            )
        }
    }
}

/**
 * Save chef chat messages to database using a simplified encoding format.
 */
fun saveChatMessagesToDatabase(messages: List<ChatMessage>) {
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
        "chef_chat_messages",
        encodedMessages.joinToString("||")
    )
}

/**
 * Load chef chat messages from database and reconstruct ChatMessage objects.
 */
fun loadChefChatMessagesFromDatabase(
    colors: BesteChefColors
): List<ChatMessage> {
    val storedData = GlobalDatabase.readString("chef_chat_messages") ?: return emptyList()
    
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
                    avatarImageName = if (isFromMe) "ichiraku" else "sophie",
                    avatarColor = if (isFromMe) colors.chefSecondary else colors.dinerSecondary,
                    bubbleColor = if (isFromMe) colors.chefPrimary else colors.dinerPrimary,
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
                    avatarImageName = if (isFromMe) "ichiraku" else "sophie",
                    avatarColor = if (isFromMe) colors.chefSecondary else colors.dinerSecondary,
                    bubbleColor = if (isFromMe) colors.chefPrimary else colors.dinerPrimary,
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
                    avatarImageName = if (isFromMe) "ichiraku" else "sophie",
                    avatarColor = if (isFromMe) colors.chefSecondary else colors.dinerSecondary,
                    bubbleColor = if (isFromMe) colors.chefPrimary else colors.dinerPrimary,
                )
            }
            else -> null
        }
    }
}
