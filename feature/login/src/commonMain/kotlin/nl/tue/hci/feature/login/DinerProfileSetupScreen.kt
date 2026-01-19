package nl.tue.hci.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale

@Composable
fun DinerProfileSetupScreen(
    onProfileComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    var name by remember { mutableStateOf("Sophie") }
    var address by remember { mutableStateOf("Keizersgracht 123, 1015 CJ Amsterdam") }
    var avatarSet by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        
        Text(
            text = "Complete Your Profile",
            style = typography.sectionTitle,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tell us a bit about yourself",
            style = typography.bodyMedium,
            color = colors.textSecondary
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(if (avatarSet) Color.Transparent else colors.outline.copy(alpha = 0.3f))
                .clickable { avatarSet = true },
            contentAlignment = Alignment.Center
        ) {
            if (avatarSet) {
                Image(
                    painter = rememberImagePainter("sophie"),
                    contentDescription = "Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Add Avatar",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        
        if (!avatarSet) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap to add photo",
                style = typography.labelSmall,
                color = colors.textSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Name input
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = {
                Text(
                    text = "Name",
                    color = colors.textSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                focusedBorderColor = colors.dinerPrimary,
                unfocusedBorderColor = colors.outline,
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Address input
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = {
                Text(
                    text = "Address",
                    color = colors.textSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                focusedBorderColor = colors.dinerPrimary,
                unfocusedBorderColor = colors.outline,
            ),
            singleLine = false
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Continue button
        Button(
            onClick = onProfileComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.dinerPrimary
            )
        ) {
            Text(
                text = "Continue",
                color = colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
