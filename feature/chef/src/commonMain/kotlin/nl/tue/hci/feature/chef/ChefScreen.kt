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
import nl.tue.hci.core.ui.BesteChefTheme
import nl.tue.hci.core.ui.BesteChefThemeColors


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
        var showBookingConfirmedScreen by rememberSaveable { mutableStateOf(false) }
        var chatCustomerName by rememberSaveable { mutableStateOf("") }
        var editOrderId by rememberSaveable { mutableStateOf("") }
        var orderDetailsForConfirmed by rememberSaveable { mutableStateOf<nl.tue.hci.feature.chef.model.OrderDetails?>(null) }
        var menuItemsForConfirmed by rememberSaveable { mutableStateOf<List<nl.tue.hci.feature.chef.model.OfferMenuItem>>(emptyList()) }

        if (showBookingConfirmedScreen && orderDetailsForConfirmed != null) {
            // Only OrderConfirmedScreen is full-screen
            nl.tue.hci.feature.chef.pages.OrderConfirmedScreen(
                orderDetails = orderDetailsForConfirmed!!,
                menuItems = menuItemsForConfirmed,
                modifier = modifier,
                onDoneClick = {
                    showBookingConfirmedScreen = false
                    orderDetailsForConfirmed = null
                    menuItemsForConfirmed = emptyList()
                    // Navigate to home screen
                    currentDestination = ChefDestinations.HOME
                }
            )
        } else if (showChatScreen) {
            nl.tue.hci.feature.chef.pages.ChefChatScreen(
                customerName = chatCustomerName,
                modifier = modifier,
                onBackClick = {
                    showChatScreen = false
                },
                onEditOrderClick = {
                    // Navigate to Orders section and open EditOrderScreen there
                    editOrderId = "chat-${chatCustomerName}" // Generate order ID from customer name
                    currentDestination = ChefDestinations.ORDERS
                    showChatScreen = false
                }
            )
        } else {
            val colors = BesteChefThemeColors.current()
            
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = colors.surfaceContainer
                    ) {
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
                                onClick = { currentDestination = destination },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = colors.chefPrimary,
                                    selectedTextColor = colors.chefPrimary,
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
                        modifier = Modifier.padding(innerPadding),
                        initialOrderId = editOrderId,
                        onOrderClick = { orderId ->
                            editOrderId = orderId
                        },
                        onSendOfferClick = { orderDetails, menuItems ->
                            orderDetailsForConfirmed = orderDetails
                            menuItemsForConfirmed = menuItems
                            showBookingConfirmedScreen = true
                        }
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
fun ChefOrdersScreen(
    modifier: Modifier = Modifier,
    initialOrderId: String = "",
    onOrderClick: (String) -> Unit = {},
    onSendOfferClick: (nl.tue.hci.feature.chef.model.OrderDetails, List<nl.tue.hci.feature.chef.model.OfferMenuItem>) -> Unit = { _, _ -> }
) {
    var showEditOrder by rememberSaveable { mutableStateOf(initialOrderId.isNotEmpty()) }
    var showMenuPicker by rememberSaveable { mutableStateOf(false) }
    var selectedOrderId by rememberSaveable { mutableStateOf(if (initialOrderId.isNotEmpty()) initialOrderId else null) }
    var pendingItemsToAdd by rememberSaveable { mutableStateOf<List<nl.tue.hci.feature.chef.model.SelectedMenuItem>?>(null) }
    
    // Update selectedOrderId when initialOrderId changes (e.g., from chat screen)
    androidx.compose.runtime.LaunchedEffect(initialOrderId) {
        if (initialOrderId.isNotEmpty() && selectedOrderId != initialOrderId) {
            selectedOrderId = initialOrderId
            showEditOrder = true
        }
    }
    
    if (showMenuPicker) {
        nl.tue.hci.feature.chef.pages.MenuPickerScreen(
            modifier = modifier,
            onBackClick = {
                showMenuPicker = false
            },
            onItemSelected = { selectedItems ->
                pendingItemsToAdd = selectedItems
                showMenuPicker = false
            }
        )
    } else if (showEditOrder && selectedOrderId != null) {
        nl.tue.hci.feature.chef.pages.EditOrderScreen(
            orderId = selectedOrderId ?: "",
            modifier = modifier,
            onBackClick = {
                showEditOrder = false
                selectedOrderId = null
                pendingItemsToAdd = null
                onOrderClick("") // Clear the order selection
            },
            onAddDishClick = {
                showMenuPicker = true
            },
            itemsToAdd = pendingItemsToAdd,
            onItemsAdded = {
                pendingItemsToAdd = null
            },
            onSendOfferClick = onSendOfferClick
        )
    } else {
        nl.tue.hci.feature.chef.pages.ChefOrdersListScreen(
            modifier = modifier,
            onOrderClick = { orderId ->
                selectedOrderId = orderId
                showEditOrder = true
                onOrderClick(orderId)
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
