package nl.tue.hci.feature.chef.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.Avatar


@Composable
fun ChefProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        
        // Avatar
        Avatar(
            text = "I",
            size = 120,
            backgroundColor = colors.chefPrimary,
            textColor = colors.textOnPrimary,
            imageName = "ichiraku"
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // User Name
        Text(
            text = "Ichiraku",
            style = typography.titleLarge,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 48.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.chefPrimary,
                contentColor = colors.textOnPrimary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Logout",
                style = typography.labelLarge
            )
        }
    }
}
