package nl.tue.hci.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
fun EditButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    textColor: Color? = null
) {
    val colors = BesteChefThemeColors.current()
    val finalBackgroundColor = backgroundColor ?: colors.buttonBackground
    val finalTextColor = textColor ?: colors.textPrimary
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(finalBackgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Edit",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = finalTextColor
        )
    }
}

