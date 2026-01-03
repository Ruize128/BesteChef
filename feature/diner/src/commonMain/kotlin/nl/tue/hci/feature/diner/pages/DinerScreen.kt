package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
// Preview removed for multiplatform
import nl.tue.hci.core.ui.BesteChefTheme
import nl.tue.hci.core.ui.BesteChefThemeColors

@Composable
fun DinerScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    BesteChefTheme {
        var currentDestination by rememberSaveable { mutableStateOf(DinerDestinations.HOME) }
        var showPaymentSuccessfulScreen by rememberSaveable { mutableStateOf(false) }
        var showChatScreen by rememberSaveable { mutableStateOf(false) }
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
            // Chat screen is full-screen
            DinerChatScreen(
                chefName = chatChefName,
                modifier = modifier,
                onBackClick = {
                    showChatScreen = false
                    // Ensure we're in the chat section when returning
                    currentDestination = DinerDestinations.CHAT
                }
            )
        } else {
            val colors = BesteChefThemeColors.current()
            
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = colors.surfaceContainer
                    ) {
                        DinerDestinations.entries.forEach { destination ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        destination.icon,
                                        contentDescription = destination.label
                                    )
                                },
                                label = { Text(destination.label) },
                                selected = destination == currentDestination,
                                onClick = { currentDestination = destination },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = colors.dinerPrimary,
                                    selectedTextColor = colors.dinerPrimary,
                                    indicatorColor = colors.surface,
                                    unselectedIconColor = colors.textSecondary,
                                    unselectedTextColor = colors.textSecondary
                                )
                            )
                        }
                    }
                },
                modifier = modifier.fillMaxSize()
            ) { innerPadding ->
                when (currentDestination) {
                    DinerDestinations.HOME -> DinerHomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onChatClick = { chefName ->
                            // Navigate to chat section and open chat
                            chatChefName = chefName
                            currentDestination = DinerDestinations.CHAT
                            showChatScreen = true
                        }
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

enum class DinerDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    CHAT("Chat", Icons.Default.Email),
    ORDERS("Orders", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

