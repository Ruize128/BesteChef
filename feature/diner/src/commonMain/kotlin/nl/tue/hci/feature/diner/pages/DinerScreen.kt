package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
// Preview removed for multiplatform
import nl.tue.hci.feature.diner.ui.theme.BesteChefTheme

@Composable
fun DinerScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    BesteChefTheme {
        var currentDestination by rememberSaveable { mutableStateOf(DinerDestinations.HOME) }
        var showChatScreen by rememberSaveable { mutableStateOf(false) }
        var showPaymentSuccessfulScreen by rememberSaveable { mutableStateOf(false) }
        var chatChefName by rememberSaveable { mutableStateOf("") }

        if (showPaymentSuccessfulScreen) {
            // Payment successful screen is full-screen
            PaymentSuccessfulScreen(
                modifier = modifier,
                onDoneClick = {
                    showPaymentSuccessfulScreen = false
                    // Navigate back to Orders section
                    currentDestination = DinerDestinations.ORDERS
                }
            )
        } else if (showChatScreen) {
            ChatScreen(
                chefName = chatChefName,
                modifier = modifier,
                onBackClick = {
                    showChatScreen = false
                }
            )
        } else {
            DinerNavigationScaffold(
                currentDestination = currentDestination,
                onDestinationChange = { currentDestination = it },
                modifier = modifier
            ) { innerPadding ->
                when (currentDestination) {
                    DinerDestinations.HOME -> DinerHomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                    DinerDestinations.CHAT -> DinerChatHistoryScreen(
                        modifier = Modifier.padding(innerPadding),
                        onChatClick = { chefName ->
                            chatChefName = chefName
                            showChatScreen = true
                        }
                    )
                    DinerDestinations.ORDERS -> DinerOrdersScreen(
                        modifier = Modifier.padding(innerPadding),
                        initialOrderId = "",
                        onOrderClick = { orderId ->
                            // Order selection handled internally
                        },
                        onBookAndPayClick = {
                            showPaymentSuccessfulScreen = true
                        }
                    )
                    DinerDestinations.PROFILE -> DinerProfileScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
expect fun DinerNavigationScaffold(
    currentDestination: DinerDestinations,
    onDestinationChange: (DinerDestinations) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
)

enum class DinerDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    CHAT("Chat", Icons.Default.Email),
    ORDERS("Orders", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

