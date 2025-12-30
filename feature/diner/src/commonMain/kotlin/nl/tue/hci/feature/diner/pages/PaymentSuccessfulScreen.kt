package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.font.FontStyle
import nl.tue.hci.core.ui.BesteChefThemeColors

@Composable
fun PaymentSuccessfulScreen(
    modifier: Modifier = Modifier,
    onDoneClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(colors.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Success icon - green circle with checkmark
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(colors.statusConfirmedBackground), // Green
            contentAlignment = Alignment.Center
        ) {
            // Outer lighter green circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(colors.dinerPrimary), // Lighter green
            )
            
            // Checkmark icon
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Payment successful",
                modifier = Modifier.size(60.dp),
                tint = colors.textOnPrimary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Confirmation text
        Text(
            text = "Payment successful",
            style = MaterialTheme.typography.headlineLarge,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Done button
        Button(
            onClick = onDoneClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp, top = 16.dp)
                .height(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.dinerPrimary, // Green
                contentColor = colors.textPrimary,
            )
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = colors.textPrimary,
            )
        }
    }
}

