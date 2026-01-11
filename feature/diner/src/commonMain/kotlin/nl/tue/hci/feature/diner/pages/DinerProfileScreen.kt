package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.feature.diner.notification.sendBookingOfferNotification


@Composable
fun DinerProfileScreen(
    modifier: Modifier = Modifier,
    onNavigateToOrders: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // Send booking offer notification when screen appears
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000) // Delay 1 second to ensure heads-up appears
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            sendBookingOfferNotification {
                // Navigate to orders with the specific order ID
                onNavigateToOrders("ichiraku_offer")
            }
        }
    }
    
    val colors = BesteChefThemeColors.current()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Manage your diner profile and preferences.",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.dinerPrimary,
                contentColor = colors.textOnPrimary,
            ),
        ) {
            Text("Logout")
        }
    }
}

