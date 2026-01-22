package nl.tue.hci.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.tue.hci.core.ui.BesteChefThemeColors

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null
) {
    val colors = BesteChefThemeColors.current()
    val finalBackgroundColor = backgroundColor ?: colors.surface
    
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(finalBackgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = colors.textSecondary,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 24.sp,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

