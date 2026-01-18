package nl.tue.hci.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import nl.tue.hci.core.model.ChatMessage
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.rememberImagePainter

@Composable
fun ChatBubble(
    message: ChatMessage,
    onBookingOfferClick: () -> Unit = {},
    onImageClick: (String) -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromMe) {
            Avatar(
                text = message.avatarText,
                size = 32,
                backgroundColor = message.avatarColor,
                modifier = Modifier,
                imageName = message.avatarImageName,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.weight(1f, false),
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
        ) {
            if (message.isTyping) {
                // Typing indicator bubble
                TypingIndicatorBubble(bubbleColor = message.bubbleColor)
            } else if (message.bookingOffer != null) {
                // Booking offer message - Card design
                val typography = BesteChefThemeTypography.current()
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onBookingOfferClick),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column {
                        // Restaurant image header
                        Image(
                            painter = rememberImagePainter("ichiraku_menu_cover"),
                            contentDescription = "Menu Cover",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Content
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Title
                            Text(
                                text = "Classic Japanese",
                                style = typography.cardTitle.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                color = colors.textPrimary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            // Details in a more organized layout
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = colors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = message.bookingOffer.date,
                                            style = typography.bodySmall,
                                            color = colors.textPrimary
                                        )
                                    }
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = colors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = message.bookingOffer.time,
                                            style = typography.bodySmall,
                                            color = colors.textPrimary
                                        )
                                    }
                                }
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = colors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = message.bookingOffer.guests,
                                            style = typography.bodySmall,
                                            color = colors.textPrimary
                                        )
                                    }
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = colors.textSecondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = message.bookingOffer.venue,
                                            style = typography.bodySmall,
                                            color = colors.textPrimary
                                        )
                                    }
                                }
                            }
                            
                            // Price and CTA
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = message.bookingOffer.price,
                                    style = typography.cardTitle.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                    color = colors.chefPrimary
                                )
                                
                                Text(
                                    text = "View Details →",
                                    style = typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = colors.chefPrimary
                                )
                            }
                        }
                    }
                }
            } else if (message.imagePreview != null) {
                // Image preview message - use yuzu_mousse.png
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colors.outline,
                    modifier = Modifier
                        .width(200.dp)
                        .height(150.dp)
                        .clickable { onImageClick("yuzu_mousse") }
                ) {
                    Image(
                        painter = rememberImagePainter("yuzu_mousse"),
                        contentDescription = message.imagePreview,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = message.imagePreview,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textTertiary
                )
            } else if (message.text.isNotEmpty()) {
                // Text message
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = message.bubbleColor,
                    modifier = Modifier.padding(horizontal = 0.dp)
                ) {
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                    )
                }
            }
            
            // Timestamp - don't show for typing indicator
            if (!message.isTyping) {
                Text(
                    text = message.timestamp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    textAlign = if (message.isFromMe) TextAlign.End else TextAlign.Start
                )
            }
        }
        
        if (message.isFromMe) {
            Spacer(modifier = Modifier.width(8.dp))
            // User avatar - using Avatar component since it has dark background with white text
            Avatar(
                text = message.avatarText,
                size = 32,
                backgroundColor = message.avatarColor,
                modifier = Modifier,
                imageName = message.avatarImageName,
            )
        }
    }
}

@Composable
private fun TypingIndicatorBubble(bubbleColor: Color) {
    val colors = BesteChefThemeColors.current()

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bubbleColor,
        modifier = Modifier
            .height(32.dp)
            .padding(horizontal = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated typing dots
            repeat(3) { index ->
                var dotScale by remember { mutableStateOf(0.8f) }

                LaunchedEffect(Unit) {
                    while (true) {
                        delay((index * 150).toLong())
                        dotScale = 1.2f
                        delay(500)
                        dotScale = 0.8f
                        delay((3 - index) * 150.toLong())
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(4.dp)
                        .scale(dotScale),
                    shape = CircleShape,
                    color = colors.textTertiary,
                    tonalElevation = 0.dp
                ) {}
            }
        }
    }
}
