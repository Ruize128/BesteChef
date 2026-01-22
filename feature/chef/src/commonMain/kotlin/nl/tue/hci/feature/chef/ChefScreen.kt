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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
// Preview removed for multiplatform
import nl.tue.hci.feature.chef.pages.ChefChatHistoryScreen
import nl.tue.hci.core.ui.BesteChefTheme
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.ui.PlatformBackHandler
import nl.tue.hci.core.ui.rememberAppExitHandler
import nl.tue.hci.core.ui.components.InAppNotificationOverlay
import nl.tue.hci.feature.chef.pages.ChefProfileScreen
import nl.tue.hci.feature.chef.model.OrderStatus
import nl.tue.hci.feature.chef.model.OrderDetails
import nl.tue.hci.feature.chef.model.OfferMenuItem
import nl.tue.hci.core.data.GlobalDatabase
import nl.tue.hci.core.utils.formatDate
import kotlinx.datetime.LocalDate

enum class ChefDestinations(
    val label: String,
    val icon: ImageVector,
    val iconName: String,
) {
    HOME("Home", Icons.Default.Home, "home"),
    CHAT("Chat", Icons.Default.Email, "comments"),
    ORDERS("Booking", Icons.Default.Favorite, "booking"),
    PROFILE("Profile", Icons.Default.AccountBox, "profile"),
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
        val orderDetailsSaver = Saver<OrderDetails?, List<Any?>>(
            save = { details ->
                details?.let {
                    listOf(it.date, it.time, it.guests, it.address, it.status.name)
                }
            },
            restore = { saved ->
                (saved as? List<*>)?.let {
                    OrderDetails(
                        date = it.getOrNull(0) as? String ?: "",
                        time = it.getOrNull(1) as? String ?: "",
                        guests = (it.getOrNull(2) as? Int) ?: 0,
                        address = it.getOrNull(3) as? String ?: "",
                        status = (it.getOrNull(4) as? String)?.let(OrderStatus::valueOf) ?: OrderStatus.DRAFT
                    )
                }
            }
        )

        val offerMenuItemListSaver = Saver<List<OfferMenuItem>, List<List<Any?>>>(
            save = { items ->
                items.map { item ->
                    listOf(
                        item.id,
                        item.title,
                        item.description,
                        item.price,
                        item.imageColor.value,
                        item.quantity
                    )
                }
            },
            restore = { saved ->
                saved.mapNotNull { entry ->
                    if (entry.size >= 6) {
                        OfferMenuItem(
                            id = entry[0] as? String ?: "",
                            title = entry[1] as? String ?: "",
                            description = entry[2] as? String ?: "",
                            price = entry[3] as? String ?: "",
                            imageColor = androidx.compose.ui.graphics.Color((entry[4] as? Long) ?: 0L),
                            quantity = (entry[5] as? Int) ?: 0
                        )
                    } else null
                }
            }
        )

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
        var editOrderStatus by rememberSaveable { mutableStateOf<String?>(null) }
        var editOrderSource by rememberSaveable { mutableStateOf<String?>(null) } // Track if edit order came from chat or orders
        var orderDetailsForConfirmed by rememberSaveable(stateSaver = orderDetailsSaver) { mutableStateOf<OrderDetails?>(null) }
        // Flag to ensure new message notification only shows once per app session
        var hasShownNewMessageNotification by remember { mutableStateOf(false) }
        var menuItemsForConfirmed by rememberSaveable(stateSaver = offerMenuItemListSaver) { mutableStateOf<List<OfferMenuItem>>(emptyList()) }
        var sentOrderId by rememberSaveable { mutableStateOf<String?>(null) }
        
        // Track unread message count from database
        var unreadMessageCount by remember { 
            mutableStateOf(GlobalDatabase.readString("chef_unread_count")?.toIntOrNull() ?: 1) 
        }
        
        val exitApp = rememberAppExitHandler()

        // Handle back button
        PlatformBackHandler(
            enabled = true,
            onBack = {
                when {
                    showBookingConfirmedScreen -> {
                        // On booking confirmed screen, go back to orders and mark as sent
                        showBookingConfirmedScreen = false
                        orderDetailsForConfirmed = null
                        menuItemsForConfirmed = emptyList()
                        sentOrderId = "1"
                        editOrderStatus = "SENT"
                        currentDestination = ChefDestinations.ORDERS
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
                        editOrderStatus = "SENT"
                        
                        showBookingConfirmedScreen = false
                        orderDetailsForConfirmed = null
                        menuItemsForConfirmed = emptyList()
                        // Return to edit order page showing SENT status (keep editOrderId set)
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
                    onEditOrderClick = { orderId ->
                        // Navigate to Orders section and open EditOrderScreen there
                        val status = GlobalDatabase.readString("chef_order_status") ?: if (sentOrderId == orderId) "SENT" else null
                        editOrderId = orderId
                        editOrderStatus = status
                        editOrderSource = "chat" // Track that we came from chat
                        currentDestination = ChefDestinations.ORDERS
                        showChatScreen = false
                    },
                    onBookingOfferClick = { orderId ->
                        // Navigate to booking offer in Orders
                        showChatScreen = false
                        editOrderId = orderId
                        val status = GlobalDatabase.readString("chef_order_status") ?: if (sentOrderId == orderId) "SENT" else null
                        editOrderStatus = status
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
                                        Box {
                                            val painter = rememberImagePainter(destination.iconName)
                                            Icon(
                                                painter = painter,
                                                contentDescription = destination.label
                                            )
                                            // Show badge on Chat icon when there are unread messages
                                            if (destination == ChefDestinations.CHAT && unreadMessageCount > 0) {
                                                Badge(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .offset(x = 8.dp, y = (-4).dp),
                                                    containerColor = colors.alert,
                                                    contentColor = colors.textOnPrimary
                                                ) {
                                                    Text(text = unreadMessageCount.toString())
                                                }
                                            }
                                        }
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
                                // Clear unread count when opening Sophie's chat
                                if (customerName == "Sophie") {
                                    unreadMessageCount = 0
                                    GlobalDatabase.writeString("chef_unread_count", "0")
                                }
                            },
                            onOrderClick = { bookingId, status ->
                                editOrderId = bookingId
                                editOrderStatus = status
                                currentDestination = ChefDestinations.ORDERS
                            },
                            onNotificationShown = {
                                hasShownNewMessageNotification = true
                            },
                            hasShownNotification = hasShownNewMessageNotification,
                            unreadMessageCount = unreadMessageCount
                        )
                        ChefDestinations.CHAT -> ChefChatHistoryScreen(
                            modifier = Modifier.padding(innerPadding),
                            onChatClick = { customerName ->
                                chatCustomerName = customerName
                                showChatScreen = true
                                // Clear unread count when opening Sophie's chat
                                if (customerName == "Sophie") {
                                    unreadMessageCount = 0
                                    GlobalDatabase.writeString("chef_unread_count", "0")
                                }
                            }
                        )
                        ChefDestinations.ORDERS -> ChefOrdersScreen(
                            modifier = Modifier.padding(innerPadding),
                            initialOrderId = editOrderId,
                            initialOrderStatus = editOrderStatus,
                            editOrderSource = editOrderSource,
                            sentOrderId = sentOrderId,
                            onOrderClick = { orderId ->
                                editOrderId = orderId
                                editOrderSource = "orders" // Track that we came from orders list
                            },
                            onSendOfferClick = { orderDetails, menuItems ->
                                // Update booking status to SENT in GlobalDatabase
                                GlobalDatabase.writeString("chef_order_status", "SENT")
                                
                                // Persist booking card into chat history so it shows on next open
                                val bookingEntry = "BOOKING|Now|true"
                                val existingChat = GlobalDatabase.readString("chef_chat_messages").orEmpty()
                                val updatedChat = listOf(existingChat.takeIf { it.isNotBlank() }, bookingEntry)
                                    .filterNotNull()
                                    .joinToString("||")
                                GlobalDatabase.writeString("chef_chat_messages", updatedChat)

                                // Send notification before showing confirmation screen
                                nl.tue.hci.feature.chef.notification.sendBookingConfirmedNotification {
                                    // When notification is clicked, dismiss booking confirmed screen and go to orders
                                    showBookingConfirmedScreen = false
                                    orderDetailsForConfirmed = null
                                    menuItemsForConfirmed = emptyList()
                                    val orderId = sentOrderId ?: "1"
                                    editOrderId = orderId
                                    editOrderStatus = "SENT"
                                    editOrderSource = "notification"
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
                                editOrderStatus = null
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
    initialOrderStatus: String? = null,
    editOrderSource: String? = null,
    sentOrderId: String? = null,
    onOrderClick: (String) -> Unit = {},
    onSendOfferClick: (nl.tue.hci.feature.chef.model.OrderDetails, List<nl.tue.hci.feature.chef.model.OfferMenuItem>) -> Unit = { _, _ -> },
    onBackFromEditOrder: (source: String?) -> Unit = {}
) {
    var showEditOrder by rememberSaveable { mutableStateOf(initialOrderId.isNotEmpty()) }
    var showMenuPicker by rememberSaveable { mutableStateOf(false) }
    var selectedOrderId by rememberSaveable { mutableStateOf(if (initialOrderId.isNotEmpty()) initialOrderId else null) }
    var selectedOrderStatus by rememberSaveable { 
        mutableStateOf(
            when (initialOrderStatus) {
                "CONFIRMED" -> nl.tue.hci.feature.chef.model.OrderStatus.CONFIRMED
                "SENT" -> nl.tue.hci.feature.chef.model.OrderStatus.SENT
                "COMPLETED" -> nl.tue.hci.feature.chef.model.OrderStatus.COMPLETED
                "CANCELLED" -> nl.tue.hci.feature.chef.model.OrderStatus.CANCELLED
                else -> nl.tue.hci.feature.chef.model.OrderStatus.DRAFT
            }
        )
    }
    var pendingItemsToAdd by remember { mutableStateOf<List<nl.tue.hci.feature.chef.model.SelectedMenuItem>?>(null) }
    
    // Mock orders list (same as in ChefOrdersListScreen)
    // Read order status from database for order 1
    val order1Status = remember(sentOrderId, showEditOrder) {
        val dbStatus = nl.tue.hci.core.data.GlobalDatabase.readString("chef_order_status")
        when (dbStatus) {
            "CANCELLED" -> nl.tue.hci.feature.chef.model.OrderStatus.CANCELLED
            "COMPLETED" -> nl.tue.hci.feature.chef.model.OrderStatus.COMPLETED
            "CONFIRMED" -> nl.tue.hci.feature.chef.model.OrderStatus.CONFIRMED
            "ON_GOING" -> nl.tue.hci.feature.chef.model.OrderStatus.CONFIRMED
            "PENDING" -> nl.tue.hci.feature.chef.model.OrderStatus.CONFIRMED
            else -> if (sentOrderId == "1") nl.tue.hci.feature.chef.model.OrderStatus.SENT else nl.tue.hci.feature.chef.model.OrderStatus.DRAFT
        }
    }
    
    // Read selected date from GlobalDatabase (set by diner), default to tomorrow if not found
    val selectedOrderDate = remember {
        val dateString = nl.tue.hci.core.data.GlobalDatabase.readString("diner_selected_date")
        if (!dateString.isNullOrEmpty()) {
            try {
                LocalDate.parse(dateString)
            } catch (e: Exception) {
                // If parsing fails, default to tomorrow
                LocalDate(2026, 1, 22) // Tomorrow from current date (2026-01-21)
            }
        } else {
            // Default to tomorrow if no date found
            LocalDate(2026, 1, 22) // Tomorrow from current date (2026-01-21)
        }
    }
    
    val orders = remember(sentOrderId, showEditOrder, order1Status) {
        listOf(
            nl.tue.hci.feature.chef.model.Order(
                id = "1",
                customerName = "Sophie",
                orderDate = formatDate(selectedOrderDate) ?: "Jan 22, 2026",
                status = order1Status,
                totalPrice = "€22",
                itemCount = 2,
                timeAgo = "2h ago"
            ),
            nl.tue.hci.feature.chef.model.Order(
                id = "2",
                customerName = "Liam",
                orderDate = formatDate(selectedOrderDate) ?: "Jan 22, 2026",
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
            onSendOfferClick = onSendOfferClick,
            onCancelClick = {
                showEditOrder = false
                selectedOrderId = null
                pendingItemsToAdd = null
                onOrderClick("") // Clear the order selection
                onBackFromEditOrder(editOrderSource) // Notify parent about the source to return
            }
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
