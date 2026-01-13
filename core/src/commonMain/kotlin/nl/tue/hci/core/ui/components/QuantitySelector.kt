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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import nl.tue.hci.core.ui.BesteChefThemeColors

@Composable
fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 32.dp
) {
    val colors = BesteChefThemeColors.current()
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Decrease button
        val isZero = quantity == 0
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(colors.buttonBackground.copy(alpha = if (isZero) 0.5f else 1f))
                .clickable(onClick = onDecrease),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                fontSize = (buttonSize.value / 2).sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary.copy(alpha = if (isZero) 0.5f else 1f)
            )
        }
        
        // Quantity display
        Text(
            text = quantity.toString(),
            fontSize = (buttonSize.value / 2.2).sp,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary,
            modifier = Modifier.width(buttonSize),
            textAlign = TextAlign.Center
        )
        
        // Increase button (always active)
        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(colors.buttonBackground)
                .clickable(onClick = onIncrease),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = (buttonSize.value / 2).sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
    }
}

