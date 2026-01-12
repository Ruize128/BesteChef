package nl.tue.hci.feature.chef.pages

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.style.TextAlign
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.ChatBubble
import nl.tue.hci.core.model.ChatMessage

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
    
    // Hardcoded initial messages
    // For chef chat: isFromMe=true = chef, isFromMe=false = customer
    val initialMessages = remember(colors) {
        listOf(
            ChatMessage(
                text = "Yes! I can replace the original dessert with a nut-free yuzu mousse. Here's a photo.",
                timestamp = "10:12",
                isFromMe = true, // From chef
                avatarText = "ME",
                avatarImageName = "ichiraku", // Chef's avatar
                avatarColor = colors.chefSecondary,
                bubbleColor = colors.chefPrimary, // Chef's bubble color
            ),
            ChatMessage(
                text = "",
                timestamp = "10:13",
                isFromMe = true, // From chef
                imagePreview = "Yuzu mousse (preview)",
                avatarText = "ME",
                avatarImageName = "ichiraku", // Chef's avatar
                avatarColor = colors.chefSecondary,
                bubbleColor = colors.chefPrimary, // Chef's bubble color
            ),
            ChatMessage(
                text = "Thanks — yes please, that would help.",
                timestamp = "10:16",
                isFromMe = false, // From customer
                avatarText = "DH",
                avatarImageName = "sophie", // Customer's avatar
                avatarColor = colors.dinerSecondary,
                bubbleColor = colors.dinerPrimary, // Customer's bubble color
            ),
            ChatMessage(
                text = "Here's my offer for your event:",
                timestamp = "10:20",
                isFromMe = true, // From chef
                bookingOffer = nl.tue.hci.core.model.BookingOfferData(
                    date = "Dec 12, 2025",
                    time = "18:30",
                    guests = "6 guests",
                    venue = "Private Dining Room",
                    price = "€250"
                ),
                avatarText = "ME",
                avatarImageName = "ichiraku", // Chef's avatar
                avatarColor = colors.chefSecondary,
                bubbleColor = colors.chefPrimary, // Chef's bubble color
            )
        )
    }
    
    val messages = remember { mutableStateListOf<ChatMessage>().apply { addAll(initialMessages) } }
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
        
        // Dish description bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Column {
                // Top border
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = (0.5).dp,
                    color = colors.outline
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dish image
                    Image(
                        painter = nl.tue.hci.core.ui.rememberImagePainter("omakase_5_course"),
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
        }
        
        // Chat messages area
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
                    onBookingOfferClick = onBookingOfferClick
                )
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
                    singleLine = true
                )
                
                // Send button
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
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
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.chefPrimary),
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

