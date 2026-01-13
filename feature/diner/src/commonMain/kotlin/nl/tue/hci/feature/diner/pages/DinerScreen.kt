package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.datetime.LocalDate
// Preview removed for multiplatform
import nl.tue.hci.core.ui.BesteChefTheme
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.PlatformBackHandler
import nl.tue.hci.core.ui.rememberAppExitHandler
import nl.tue.hci.core.ui.components.InAppNotificationOverlay
import nl.tue.hci.feature.diner.DinerOrder
import nl.tue.hci.feature.diner.DinerOrderStatus

// Saver for LocalDate to make it work with rememberSaveable
private val LocalDateSaver = Saver<LocalDate?, String>(
    save = { it?.toString() ?: "" },
    restore = { if (it.isEmpty()) null else LocalDate.parse(it) }
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

@Composable
fun DinerScreen(
    modifier: Modifier = Modifier,
    initialNavigateToBookingSummary: Boolean = false,
    onLogout: () -> Unit = {}
) {
    BesteChefTheme {
        var currentDestination by rememberSaveable { mutableStateOf(
            if (initialNavigateToBookingSummary) DinerDestinations.ORDERS else DinerDestinations.HOME
        ) }
        var showPaymentSuccessfulScreen by rememberSaveable { mutableStateOf(false) }
        var showChatScreen by rememberSaveable { mutableStateOf(false) }
        var chatChefName by rememberSaveable { mutableStateOf("") }
        // Remember where we came from so we can restore after closing chat
        var previousDestination by rememberSaveable { mutableStateOf<DinerDestinations?>(null) }
        var previousHomeState by rememberSaveable { mutableStateOf<HomeScreenState?>(null) }
        var homeScreenState by rememberSaveable { mutableStateOf(HomeScreenState.SEARCH) }
        var selectedChefName by rememberSaveable { mutableStateOf<String?>(null) }
        var selectedMenuName by rememberSaveable { mutableStateOf<String?>(null) }
        var selectedSearchLocation by rememberSaveable { mutableStateOf<String?>(null) }
        var selectedSearchDate by rememberSaveable(stateSaver = LocalDateSaver) { mutableStateOf<kotlinx.datetime.LocalDate?>(null) }
        var selectedSearchGuests by rememberSaveable { mutableStateOf<String?>(null) }
        var selectedSearchAllergens by rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }
        var selectedSearchCuisine by rememberSaveable { mutableStateOf<String?>(null) }
        var selectedOrderId by rememberSaveable { mutableStateOf(if (initialNavigateToBookingSummary) "ichiraku_offer" else "") }
        
        // Hardcoded orders list (initial source)
        val initialOrders = listOf(
            DinerOrder(
                id = "1",
                chefName = "Chef Ichiraku",
                orderDate = "Dec 12, 2025",
                status = DinerOrderStatus.PENDING,
                totalPrice = "€102",
                itemCount = 3,
                timeAgo = "1h ago"
            ),
            DinerOrder(
                id = "2",
                chefName = "Chef Marco",
                orderDate = "Dec 11, 2025",
                status = DinerOrderStatus.COMPLETED,
                totalPrice = "€85",
                itemCount = 2,
                timeAgo = "1d ago"
            ),
            DinerOrder(
                id = "3",
                chefName = "Chef Elena",
                orderDate = "Dec 10, 2025",
                status = DinerOrderStatus.COMPLETED,
                totalPrice = "€120",
                itemCount = 4,
                timeAgo = "2d ago"
            )
        )

        var orders by remember { mutableStateOf(initialOrders) }

        // Refresh orders whenever the user navigates to the Orders tab
        androidx.compose.runtime.LaunchedEffect(currentDestination) {
            if (currentDestination == DinerDestinations.ORDERS) {
                orders = initialOrders
            }
        }
        
        val exitApp = rememberAppExitHandler()

        // Handle back button
        PlatformBackHandler(
            enabled = true,
            onBack = {
                when {
                    showPaymentSuccessfulScreen -> {
                        // On payment screen, go back to orders
                        showPaymentSuccessfulScreen = false
                    }
                    showChatScreen -> {
                        // On chat screen, restore previous destination/state if available
                        showChatScreen = false
                        if (previousDestination != null) {
                            currentDestination = previousDestination!!
                            // If we came from HOME, restore the home sub-state
                            if (previousDestination == DinerDestinations.HOME && previousHomeState != null) {
                                homeScreenState = previousHomeState!!
                            }
                            previousDestination = null
                            previousHomeState = null
                        } else {
                            // Fallback: go to chat history
                            currentDestination = DinerDestinations.CHAT
                        }
                    }
                    // If we're on Orders and a booking summary is open (selectedOrderId set),
                    // treat system back like the header back: close the summary and restore
                    // previous destination (e.g., chat) if available.
                    currentDestination == DinerDestinations.ORDERS && selectedOrderId.isNotEmpty() -> {
                        // Close booking summary
                        selectedOrderId = ""
                        if (previousDestination != null) {
                            val dest = previousDestination!!
                            previousDestination = null
                            if (dest == DinerDestinations.HOME && previousHomeState != null) {
                                currentDestination = DinerDestinations.HOME
                                homeScreenState = previousHomeState!!
                                previousHomeState = null
                            } else if (dest == DinerDestinations.CHAT) {
                                currentDestination = DinerDestinations.CHAT
                                showChatScreen = true
                            } else {
                                currentDestination = dest
                            }
                        } else {
                            // No previous destination recorded, stay on Orders list
                            currentDestination = DinerDestinations.ORDERS
                        }
                    }
                    currentDestination == DinerDestinations.HOME -> {
                        // Handle back navigation within home section
                        when (homeScreenState) {
                            HomeScreenState.SEARCH -> {
                                // At search page (base of home section), exit app
                                exitApp()
                            }
                            HomeScreenState.SEARCH_RESULTS -> {
                                // Go back to search
                                homeScreenState = HomeScreenState.SEARCH
                            }
                            HomeScreenState.MENU_LIST -> {
                                // Go back to search results
                                homeScreenState = HomeScreenState.SEARCH_RESULTS
                            }
                            HomeScreenState.MENU -> {
                                // Go back to menu list
                                homeScreenState = HomeScreenState.MENU_LIST
                            }
                        }
                    }
                    else -> {
                        // On any other section page, go back to home
                        currentDestination = DinerDestinations.HOME
                    }
                }
            }
        )

        if (showPaymentSuccessfulScreen) {
            // Payment successful screen is full-screen
            PaymentSuccessfulScreen(
                modifier = modifier,
                onDoneClick = {
                    showPaymentSuccessfulScreen = false
                    // Clear selected order and navigate to Orders list
                    selectedOrderId = ""
                    currentDestination = DinerDestinations.ORDERS
                }
            )
        } else if (showChatScreen) {
            // Chat screen is full-screen
            DinerChatScreen(
                chefName = chatChefName,
                modifier = modifier,
                onBackClick = {
                    // Close chat and restore previous destination/state if available
                    showChatScreen = false
                    if (previousDestination != null) {
                        currentDestination = previousDestination!!
                        if (previousDestination == DinerDestinations.HOME && previousHomeState != null) {
                            homeScreenState = previousHomeState!!
                        }
                        previousDestination = null
                        previousHomeState = null
                    } else {
                        currentDestination = DinerDestinations.CHAT
                    }
                },
                onBookingOfferClick = {
                    // Navigate to booking offer in Orders
                    // Remember we came from chat so we can restore it when closing booking
                    previousDestination = DinerDestinations.CHAT
                    previousHomeState = null
                    showChatScreen = false
                    selectedOrderId = "ichiraku_offer"
                    currentDestination = DinerDestinations.ORDERS
                }
            )
        } else {
            val colors = BesteChefThemeColors.current()
            
            Box(modifier = Modifier.fillMaxSize()) {
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
                        currentState = homeScreenState,
                        selectedChefName = selectedChefName,
                        selectedMenuName = selectedMenuName,
                        selectedSearchLocation = selectedSearchLocation,
                        selectedSearchDate = selectedSearchDate,
                        selectedSearchGuests = selectedSearchGuests,
                        selectedSearchAllergens = selectedSearchAllergens,
                        selectedSearchCuisine = selectedSearchCuisine,
                        onStateChange = { newState ->
                            homeScreenState = newState
                        },
                        onChefSelect = { chefName ->
                            selectedChefName = chefName
                        },
                        onMenuSelect = { menuName ->
                            selectedMenuName = menuName
                        },
                        onSearch = { location, date, guests, allergens, cuisine ->
                            selectedSearchLocation = location
                            selectedSearchDate = date
                            selectedSearchGuests = guests
                            selectedSearchAllergens = allergens
                            selectedSearchCuisine = cuisine
                        },
                        onChatClick = { chefName ->
                            // Navigate to chat section and open chat
                                // remember where we came from so we can restore later
                                previousDestination = currentDestination
                                previousHomeState = homeScreenState
                                chatChefName = chefName
                                currentDestination = DinerDestinations.CHAT
                                showChatScreen = true
                        },
                        onBookFromMenu = {
                            selectedOrderId = "1"
                            currentDestination = DinerDestinations.ORDERS
                        }
                    )
                    DinerDestinations.CHAT -> DinerChatHistoryScreen(
                        modifier = Modifier.padding(innerPadding),
                        onChatClick = { chefName ->
                            // remember where we came from
                            previousDestination = currentDestination
                            previousHomeState = homeScreenState
                            chatChefName = chefName
                            showChatScreen = true
                        }
                    )
                    DinerDestinations.ORDERS -> DinerOrdersScreen(
                        modifier = Modifier.padding(innerPadding),
                        orders = orders,
                        initialOrderId = selectedOrderId,
                        onOrderClick = { orderId ->
                            // If booking summary was closed (empty id), restore previous context
                            if (orderId.isEmpty()) {
                                if (previousDestination != null) {
                                    // If previous destination was chat, re-open chat
                                    val dest = previousDestination!!
                                    previousDestination = null
                                    if (dest == DinerDestinations.HOME && previousHomeState != null) {
                                        currentDestination = DinerDestinations.HOME
                                        homeScreenState = previousHomeState!!
                                        previousHomeState = null
                                    } else if (dest == DinerDestinations.CHAT) {
                                        currentDestination = DinerDestinations.CHAT
                                        showChatScreen = true
                                    } else {
                                        currentDestination = dest
                                    }
                                }
                            }
                        },
                        onBookAndPayClick = {
                            showPaymentSuccessfulScreen = true
                        },
                        onDeleteOrder = { orderId ->
                            // No longer deleting orders - they are marked as cancelled instead
                            // This callback is kept for compatibility but does nothing
                        },
                        onUpdateOrderStatus = { orderId, newStatus ->
                            // Update the order status in the mutable list
                            orders = orders.map { order ->
                                if (order.id == orderId) {
                                    order.copy(status = newStatus)
                                } else {
                                    order
                                }
                            }
                        }
                    )
                    DinerDestinations.PROFILE -> DinerProfileScreen(
                        modifier = Modifier.padding(innerPadding),
                        onNavigateToOrders = { orderId ->
                            selectedOrderId = orderId
                            currentDestination = DinerDestinations.ORDERS
                        },
                        onLogout = onLogout
                    )
                }
            }
                
                // Add in-app notification overlay on top of everything
                InAppNotificationOverlay()
            }
        }
    }
}