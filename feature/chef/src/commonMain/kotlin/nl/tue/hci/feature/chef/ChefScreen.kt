package nl.tue.hci.feature.chef

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import nl.tue.hci.feature.chef.pages.ChefChatHistoryScreen
import nl.tue.hci.feature.chef.ui.theme.BesteChefTheme


@Composable
fun ChefScreenPreview() {
    ChefScreen(
        modifier = Modifier
    )
}

@Composable
fun ChefScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    BesteChefTheme {
        var currentDestination by rememberSaveable { mutableStateOf(ChefDestinations.HOME) }
        var showChatScreen by rememberSaveable { mutableStateOf(false) }
        var chatCustomerName by rememberSaveable { mutableStateOf("") }

        if (showChatScreen) {
            nl.tue.hci.feature.chef.pages.ChefChatScreen(
                customerName = chatCustomerName,
                modifier = modifier,
                onBackClick = {
                    showChatScreen = false
                }
            )
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        ChefDestinations.entries.forEach { destination ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        destination.icon,
                                        contentDescription = destination.label
                                    )
                                },
                                label = { Text(destination.label) },
                                selected = destination == currentDestination,
                                onClick = { currentDestination = destination }
                            )
                        }
                    }
                },
                modifier = modifier.fillMaxSize()
            ) { innerPadding ->
                when (currentDestination) {
                    ChefDestinations.HOME -> ChefHomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        onChatClick = { customerName ->
                            chatCustomerName = customerName
                            showChatScreen = true
                        }
                    )
                    ChefDestinations.CHAT -> ChefChatHistoryScreen(
                        modifier = Modifier.padding(innerPadding),
                        onChatClick = { customerName ->
                            chatCustomerName = customerName
                            showChatScreen = true
                        }
                    )
                    ChefDestinations.ORDERS -> ChefOrdersScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                    ChefDestinations.PROFILE -> ChefProfileScreen(
                        modifier = Modifier.padding(innerPadding),
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

enum class ChefDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    CHAT("Chat", Icons.Default.Email),
    ORDERS("Orders", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

// ChefHomeScreen is now in ChefHomeScreen.kt

@Composable
fun ChefOrdersScreen(modifier: Modifier = Modifier) {
    var showComposeOffer by rememberSaveable { mutableStateOf(false) }
    var showMenuPicker by rememberSaveable { mutableStateOf(false) }
    var selectedOrderId by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingItemsToAdd by rememberSaveable { mutableStateOf<List<nl.tue.hci.feature.chef.model.SelectedMenuItem>?>(null) }
    
    if (showMenuPicker) {
        nl.tue.hci.feature.chef.pages.MenuPickerScreen(
            modifier = modifier,
            onClose = {
                showMenuPicker = false
            },
            onItemSelected = { selectedItems ->
                pendingItemsToAdd = selectedItems
                showMenuPicker = false
            }
        )
    } else if (showComposeOffer) {
        nl.tue.hci.feature.chef.pages.ComposeOfferScreen(
            orderId = selectedOrderId ?: "",
            modifier = modifier,
            onBackClick = {
                showComposeOffer = false
                selectedOrderId = null
                pendingItemsToAdd = null
            },
            onAddDishClick = {
                showMenuPicker = true
            },
            itemsToAdd = pendingItemsToAdd,
            onItemsAdded = {
                pendingItemsToAdd = null
            }
        )
    } else {
        nl.tue.hci.feature.chef.pages.ChefOrdersListScreen(
            modifier = modifier,
            onOrderClick = { orderId ->
                selectedOrderId = orderId
                showComposeOffer = true
            }
        )
    }
}

@Composable
fun ChefProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
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
            text = "Manage your chef profile and settings.",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout button
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
