package nl.tue.hci.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppColors.ButtonGrey,
    textColor: Color = AppColors.TextPrimary
) {
    Box(
        modifier = modifier
            .height(24.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .align(Alignment.Center)
            ,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = TextAlign.Center,
        )
    }
}

