package nl.tue.hci.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.rememberImagePainter

@Composable
fun Avatar(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = 48,
    backgroundColor: Color? = null,
    textColor: Color? = null,
    imageName: String? = null,
) {
    val colors = BesteChefThemeColors.current()
    val finalBackgroundColor = backgroundColor ?: colors.dinerSecondary
    val finalTextColor = textColor ?: colors.textOnSecondary
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(if (imageName == null) finalBackgroundColor else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        if (imageName != null) {
            // Use image if provided
            Image(
                painter = rememberImagePainter(imageName),
                contentDescription = text,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fall back to text/color
            Text(
                text = text,
                fontSize = (size * 0.4).sp,
                fontWeight = FontWeight.SemiBold,
                color = finalTextColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

