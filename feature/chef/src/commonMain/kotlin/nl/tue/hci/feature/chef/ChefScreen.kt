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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import nl.tue.hci.feature.chef.pages.ChefChatHistoryScreen
import nl.tue.hci.core.ui.BesteChefTheme
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.PlatformBackHandler
import nl.tue.hci.core.ui.rememberAppExitHandler
import nl.tue.hci.core.ui.components.InAppNotificationOverlay

enum class ChefDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    CHAT("Chat", Icons.Default.Email),
    ORDERS("Orders", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

@Composable
fun ChefScreenPreview() {
    ChefScreen(
        modifier = Modifier
    )
}

@Composable
fun ChefScreen(
    modifier: Modifier = Modifier,
    initialNavigateToOrders: Boolean = false,
    initialNavigateToChat: Boolean = false,
    initialChatCustomerName: String? = null,
    onLogout: () -> Unit = {}
) {
    BesteChefTheme {
        var currentDestination by rememberSaveable { mutableStateOf(
            when {
                initialNavigateToChat -> ChefDestinations.CHAT
                initialNavigateToOrders -> ChefDestinations.ORDERS
                else -> ChefDestinations.HOME
            }
        ) }
        var showChatScreen by rememberSaveable { mutableStateOf(initialNavigateToChat) }
        var showBookingConfirmedScreen by rememberSaveable { mutableStateOf(false) }
        var chatCustomerName by rememberSaveable { mutableStateOf(initialChatCustomerName ?: "") }
        var editOrderId by rememberSaveable { mutableStateOf("") }
        var editOrderSource by rememberSaveable { mutableStateOf<String?>(null) } // Track if edit order came from chat or orders
        var orderDetailsForConfirmed by rememberSaveable { mutableStateOf<nl.tue.hci.feature.chef.model.OrderDetails?>(null) }
        var menuItemsForConfirmed by remember { mutableStateOf<List<nl.tue.hci.feature.chef.model.OfferMenuItem>>(emptyList()) }
        var sentOrderId by rememberSaveable { mutableStateOf<String?>(null) }
        
        val exitApp = rememberAppExitHandler()

        // Handle back button
        PlatformBackHandler(
            enabled = true,
            onBack = {
                when {
                    showBookingConfirmedScreen -> {
                        // On booking confirmed screen, go back to orders
                        showBookingConfirmedScreen = false
                        orderDetailsForConfirmed = null
                        menuItemsForConfirmed = emptyList()
                    }
                    showChatScreen -> {
                        // On chat screen, go back to chat history
                        showChatScreen = false
                    }
                    currentDestination == ChefDestinations.HOME -> {
                        // On home page (base page), exit app
                        exitApp()
                    }
                    else -> {
                        // On any other section page, go back to home
                        currentDestination = ChefDestinations.HOME
                    }
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (showBookingConfirmedScreen && orderDetailsForConfirmed != null) {
                // Only OrderConfirmedScreen is full-screen
                nl.tue.hci.feature.chef.pages.OrderConfirmedScreen(
                    orderDetails = orderDetailsForConfirmed!!,
                    menuItems = menuItemsForConfirmed,
                    modifier = modifier,
                    onDoneClick = {
                        // Update the order status from DRAFT to SENT and store the sent order ID
                        sentOrderId = "1" // Store identifier for the sent order (order ID for Sophie)
                        
                        showBookingConfirmedScreen = false
                        orderDetailsForConfirmed = null
                        menuItemsForConfirmed = emptyList()
                        // Navigate to orders section home (clear any specific order being edited)
                        editOrderId = ""
                        currentDestination = ChefDestinations.ORDERS
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
                        editOrderSource = "chat" // Track that we came from chat
                        currentDestination = ChefDestinations.ORDERS
                        showChatScreen = false
                    },
                    onBookingOfferClick = {
                        // Navigate to booking offer in Orders
                        showChatScreen = false
                        editOrderId = "chat-${chatCustomerName}"
                        editOrderSource = "chat" // Track that we came from chat
                        currentDestination = ChefDestinations.ORDERS
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
                            editOrderSource = editOrderSource,
                            sentOrderId = sentOrderId,
                            onOrderClick = { orderId ->
                                editOrderId = orderId
                                editOrderSource = "orders" // Track that we came from orders list
                            },
                            onSendOfferClick = { orderDetails, menuItems ->
                                // Send notification before showing confirmation screen
                                nl.tue.hci.feature.chef.notification.sendBookingConfirmedNotification {
                                    // When notification is clicked, dismiss booking confirmed screen and go to orders
                                    showBookingConfirmedScreen = false
                                    orderDetailsForConfirmed = null
                                    menuItemsForConfirmed = emptyList()
                                    editOrderId = ""
                                    currentDestination = ChefDestinations.ORDERS
                                }
                                orderDetailsForConfirmed = orderDetails
                                menuItemsForConfirmed = menuItems
                                showBookingConfirmedScreen = true
                            },
                            onBackFromEditOrder = { source ->
                                // Handle back from edit order - return to chat or stay in orders
                                if (source == "chat") {
                                    showChatScreen = true
                                    currentDestination = ChefDestinations.CHAT
                                }
                                editOrderId = ""
                                editOrderSource = null
                            }
                        )
                        ChefDestinations.PROFILE -> ChefProfileScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLogout = onLogout
                        )
                    }
                }
            }
            
            // Add in-app notification overlay on top of everything - always visible
            InAppNotificationOverlay()
        }
    }
}

// ChefHomeScreen is now in ChefHomeScreen.kt

@Composable
fun ChefOrdersScreen(
    modifier: Modifier = Modifier,
    initialOrderId: String = "",
    editOrderSource: String? = null,
    sentOrderId: String? = null,
    onOrderClick: (String) -> Unit = {},
    onSendOfferClick: (nl.tue.hci.feature.chef.model.OrderDetails, List<nl.tue.hci.feature.chef.model.OfferMenuItem>) -> Unit = { _, _ -> },
    onBackFromEditOrder: (source: String?) -> Unit = {}
) {
    var showEditOrder by rememberSaveable { mutableStateOf(initialOrderId.isNotEmpty()) }
    var showMenuPicker by rememberSaveable { mutableStateOf(false) }
    var selectedOrderId by rememberSaveable { mutableStateOf(if (initialOrderId.isNotEmpty()) initialOrderId else null) }
    var selectedOrderStatus by rememberSaveable { mutableStateOf(nl.tue.hci.feature.chef.model.OrderStatus.DRAFT) }
    var pendingItemsToAdd by remember { mutableStateOf<List<nl.tue.hci.feature.chef.model.SelectedMenuItem>?>(null) }
    
    // Mock orders list (same as in ChefOrdersListScreen)
    val orders = remember(sentOrderId) {
        listOf(
            nl.tue.hci.feature.chef.model.Order(
                id = "1",
                customerName = "Sophie",
                orderDate = "Dec 12, 2025",
                status = if (sentOrderId == "1") nl.tue.hci.feature.chef.model.OrderStatus.SENT else nl.tue.hci.feature.chef.model.OrderStatus.DRAFT,
                totalPrice = "€22",
                itemCount = 2,
                timeAgo = "2h ago"
            ),
            nl.tue.hci.feature.chef.model.Order(
                id = "2",
                customerName = "Liam",
                orderDate = "Dec 12, 2025",
                status = nl.tue.hci.feature.chef.model.OrderStatus.CONFIRMED,
                totalPrice = "€65",
                itemCount = 1,
                timeAgo = "1d ago"
            ),
            nl.tue.hci.feature.chef.model.Order(
                id = "3",
                customerName = "Emma",
                orderDate = "Dec 11, 2025",
                status = nl.tue.hci.feature.chef.model.OrderStatus.COMPLETED,
                totalPrice = "€36",
                itemCount = 3,
                timeAgo = "2d ago"
            )
        )
    }
    
    // Refresh orders whenever navigating to orders tab or when sentOrderId changes
    androidx.compose.runtime.LaunchedEffect(sentOrderId) {
        // Orders list will be refreshed with default mock data
    }
    
    // Update selectedOrderId when initialOrderId changes (e.g., from chat screen)
    androidx.compose.runtime.LaunchedEffect(initialOrderId) {
        if (initialOrderId.isNotEmpty() && selectedOrderId != initialOrderId) {
            selectedOrderId = initialOrderId
            // Look up the status of the selected order
            val order = orders.find { it.id == initialOrderId }
            if (order != null) {
                selectedOrderStatus = order.status
            }
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
            orderStatus = selectedOrderStatus,
            modifier = modifier,
            onBackClick = {
                showEditOrder = false
                selectedOrderId = null
                pendingItemsToAdd = null
                onOrderClick("") // Clear the order selection
                onBackFromEditOrder(editOrderSource) // Notify parent about the source to return
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
            sentOrderId = sentOrderId,
            onOrderClick = { orderId ->
                selectedOrderId = orderId
                // Look up the status of the selected order
                val order = orders.find { it.id == orderId }
                if (order != null) {
                    selectedOrderStatus = order.status
                }
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
