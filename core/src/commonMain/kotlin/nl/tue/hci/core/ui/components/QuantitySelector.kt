package nl.tue.hci.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.tue.hci.core.ui.AppColors

@Composable
fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease button
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AppColors.ButtonGrey)
                .clickable(onClick = onDecrease),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }
        
        // Quantity display
        Text(
            text = quantity.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextPrimary,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center
        )
        
        // Increase button
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AppColors.ButtonGrey)
                .clickable(onClick = onIncrease),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )
        }
    }
}

