package nl.tue.hci.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.model.ChatMessage
import nl.tue.hci.core.ui.AppColors

@Composable
fun ChatBubble(
    message: ChatMessage,
    meAvatarText: String = "ME",
    partnerAvatarText: String = "DH",
    meBubbleColor: Color = AppColors.ChefPrimary,
    partnerBubbleColor: Color = AppColors.DinerPrimary, // Light green for user
    meAvatarColor: Color = AppColors.ChefSecondary,
    partnerAvatarColor: Color = AppColors.DinerSecondary,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isFromMe) {
            Avatar(
                text = partnerAvatarText,
                size = 32,
                backgroundColor = partnerAvatarColor,
                modifier = Modifier,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Column(
            modifier = Modifier.weight(1f, false),
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
        ) {
            if (message.imagePreview != null) {
                // Image preview message
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .width(200.dp)
                        .height(150.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Placeholder for image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .fillMaxHeight(0.6f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFB2E5D4))
                        )
                    }
                }
                Text(
                    text = message.imagePreview,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else if (message.text.isNotEmpty()) {
                // Text message
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (message.isFromMe) {
                        meBubbleColor
                    } else {
                        partnerBubbleColor
                    },
                    modifier = Modifier.padding(horizontal = 0.dp)
                ) {
                    Text(
                        text = message.text,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextPrimary,
                    )
                }
            }
            
            // Timestamp
            Text(
                text = message.timestamp,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextSecondary,
                textAlign = if (message.isFromMe) TextAlign.End else TextAlign.Start
            )
        }
        
        if (message.isFromMe) {
            Spacer(modifier = Modifier.width(8.dp))
            // User avatar - using Avatar component since it has dark background with white text
            Avatar(
                text = meAvatarText,
                size = 32,
                backgroundColor = meAvatarColor,
                modifier = Modifier,
            )
        }
    }
}

