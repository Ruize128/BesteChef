package nl.tue.hci.feature.chef.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import nl.tue.hci.core.ui.BesteChefColors
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.PlatformBackHandler
import nl.tue.hci.core.ui.components.QuantitySelector
import nl.tue.hci.core.ui.getImageNameFromTitle
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.data.GlobalDatabase
import nl.tue.hci.feature.chef.model.OrderDetails
import nl.tue.hci.feature.chef.model.OrderStatus
import nl.tue.hci.feature.chef.model.OfferMenuItem
import nl.tue.hci.feature.chef.model.PriceSummary
import nl.tue.hci.feature.chef.model.SelectedMenuItem
import nl.tue.hci.feature.chef.notification.sendBookingConfirmedNotification
import kotlinx.coroutines.launch

@Composable
fun EditOrderScreen(
    orderId: String,
    orderStatus: OrderStatus = OrderStatus.DRAFT,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAddDishClick: () -> Unit = {},
    itemsToAdd: List<SelectedMenuItem>? = null,
    onItemsAdded: () -> Unit = {},
    onSendOfferClick: (OrderDetails, List<OfferMenuItem>) -> Unit = { _, _ -> },
    onCancelClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    var showCancelDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Hardcoded booking details
    val orderDetails = remember {
        OrderDetails(
            date = "Dec 12, 2025",
            time = "7:00 PM",
            guests = 6,
            venue = "xxxxxx",
            status = orderStatus
        )
    }
    
    // Default menu items - reset when screen is opened
    val defaultMenuItems = remember(colors) {
        listOf(
            OfferMenuItem(
                id = "1",
                title = "Grilled Mackerel with Miso",
                description = "Sea salt, spring onion, yuzu dressing.",
                price = "€45",
                imageColor = colors.imagePlaceholder1, // Light green
                quantity = 1
            ),
            OfferMenuItem(
                id = "2",
                title = "Honey Nut & Caramel",
                description = "Roasted nuts, salted caramel glaze.",
                price = "€12",
                imageColor = colors.imagePlaceholder2, // Light orange-beige
                quantity = 1
            ),
            OfferMenuItem(
                id = "3",
                title = "Wagyu Beef Steak",
                description = "Premium wagyu with truffle butter.",
                price = "€24",
                imageColor = colors.imagePlaceholder4,
                quantity = 1
            ),
            OfferMenuItem(
                id = "4",
                title = "Sushi Platter",
                description = "Assorted fresh sushi with wasabi and ginger.",
                price = "€40",
                imageColor = colors.imagePlaceholder1,
                quantity = 1
            )
        )
    }
    
    // Menu items with quantities - reset to default when screen is opened
    val menuItems = remember(orderId) { mutableStateListOf<OfferMenuItem>() }

    // Track a key that changes when quantities change to trigger price recalculation
    var priceCalculationKey by remember(orderId) { mutableStateOf(0) }

    // Load persisted menu if available; otherwise use defaults
    LaunchedEffect(orderId) {
        val stored = GlobalDatabase.readString("chef_order_menu_items")
        val decoded = stored?.let { decodeMenuItems(it, colors) }
        menuItems.clear()
        if (decoded != null && decoded.isNotEmpty()) {
            menuItems.addAll(decoded)
        } else {
            menuItems.addAll(defaultMenuItems)
        }
        priceCalculationKey++
    }
    
    // Handle items added from MenuPickerScreen
    androidx.compose.runtime.LaunchedEffect(itemsToAdd) {
        itemsToAdd?.let { selectedItems ->
            selectedItems.forEach { selectedItem ->
                val existingIndex = menuItems.indexOfFirst { it.id == selectedItem.menuItem.id }
                if (existingIndex >= 0) {
                    // Item already exists, increase quantity
                    menuItems[existingIndex] = menuItems[existingIndex].copy(
                        quantity = menuItems[existingIndex].quantity + selectedItem.quantity
                    )
                } else {
                    // New item, add it
                    menuItems.add(
                        OfferMenuItem(
                            id = selectedItem.menuItem.id,
                            title = selectedItem.menuItem.title,
                            description = selectedItem.menuItem.description,
                            price = selectedItem.menuItem.price,
                            imageColor = selectedItem.menuItem.imageColor,
                            quantity = selectedItem.quantity
                        )
                    )
                }
            }
            priceCalculationKey++
            onItemsAdded()
        }
    }
    
    // Calculate price summary - recalculate whenever menuItems or quantities change
    val priceSummary = remember(menuItems.size, priceCalculationKey) {
        val subtotal = menuItems.sumOf { item ->
            val priceStr = item.price.replace("€", "").replace(",", ".")
            (priceStr.toDoubleOrNull() ?: 0.0) * item.quantity
        }
        val serviceFee = 15.0
        val depositPercentage = 20
        val depositAmount = (subtotal + serviceFee) * depositPercentage / 100.0
        val total = subtotal + serviceFee
        
        PriceSummary(
            subtotal = "€${subtotal.toInt()}",
            serviceFee = "€${serviceFee.toInt()}",
            depositAmount = "€${depositAmount.toInt()}",
            depositPercentage = depositPercentage,
            total = "€${total.toInt()}"
        )
    }

    // Persist menu items whenever they change
    LaunchedEffect(menuItems.size, priceCalculationKey) {
        GlobalDatabase.writeString("chef_order_menu_items", encodeMenuItems(menuItems))
    }
    
    // Handle system back gesture
    PlatformBackHandler(
        enabled = true,
        onBack = onBackClick
    )
    
    // Determine if this order is editable (only DRAFT status allows editing)
    val isEditable = orderDetails.status == OrderStatus.DRAFT
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Compose Offer",
                    style = typography.sectionTitle,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                
                // Cancel button (only for DRAFT and SENT orders)
                if (orderStatus == OrderStatus.DRAFT || orderStatus == OrderStatus.SENT) {
                    Button(
                        onClick = { showCancelDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.alert,
                            contentColor = colors.onAlert
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Spacer for other order statuses
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        }
        
        // Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Booking section
            item {
                BookingSection(
                    orderDetails = orderDetails,
                    onEditClick = {}
                )
            }
            
            // Menu section
            item {
                Text(
                    text = "Menu Preview",
                    style = typography.cardTitle,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(menuItems) { item ->
                OfferMenuItemCard(
                    item = item,
                    isEditable = isEditable,
                    onQuantityDecrease = {
                        val index = menuItems.indexOf(item)
                        if (menuItems[index].quantity > 1) {
                            menuItems[index] = menuItems[index].copy(quantity = menuItems[index].quantity - 1)
                        } else {
                            menuItems.removeAt(index)
                        }
                        priceCalculationKey++
                    },
                    onQuantityIncrease = {
                        val index = menuItems.indexOf(item)
                        menuItems[index] = menuItems[index].copy(quantity = menuItems[index].quantity + 1)
                        priceCalculationKey++
                    }
                )
            }
            
            // Add another dish button (editable menu style)
            if (isEditable) {
                item {
                    OutlinedButton(
                        onClick = onAddDishClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.statusConfirmedText
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(colors.statusConfirmedText)
                        )
                    ) {
                        Text(
                            text = "+ Add another dish",
                            style = typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Price summary section
            item {
                PriceSummarySection(priceSummary = priceSummary, orderStatus = orderDetails.status)
            }
        }
        
        // Send Offer button (only show for DRAFT status)
        if (isEditable) {
            Button(
                onClick = {
                    onSendOfferClick(orderDetails, menuItems.toList())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.chefPrimary,
                    contentColor = colors.textPrimary,
                ),
                contentPadding = PaddingValues(0.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Send Offer",
                        style = typography.cardTitle,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = priceSummary.total,
                        style = typography.cardTitle,
                        color = colors.textPrimary,
                    )
                }
            }
        }
    }
    
    // Cancel booking dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = "Cancel booking?",
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to cancel this booking? This action cannot be undone.",
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showCancelDialog = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surfaceVariant,
                            contentColor = colors.textPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Keep booking",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    
                    Button(
                        onClick = {
                            showCancelDialog = false
                            // Write CANCELLED status to database
                            nl.tue.hci.core.data.GlobalDatabase.writeString("ichiraku_order_status", "CANCELLED")
                            scope.launch {
                                snackbarHostState.showSnackbar("Delete order complete")
                            }
                            onCancelClick()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.alert,
                            contentColor = colors.onAlert
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Confirm cancel",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        )
    }
}

// Encode menu items for persistence (simple pipe-separated format)
private fun encodeMenuItems(items: List<OfferMenuItem>): String {
    return items.joinToString(separator = "||") { item ->
        listOf(
            item.id,
            item.title,
            item.description,
            item.price,
            item.quantity.toString()
        ).joinToString(separator = "|")
    }
}

// Decode menu items; colors are recreated using a single placeholder so layout remains consistent
private fun decodeMenuItems(data: String, colors: BesteChefColors): List<OfferMenuItem> {
    if (data.isBlank()) return emptyList()
    return data.split("||").mapNotNull { encoded ->
        val parts = encoded.split("|")
        if (parts.size < 5) return@mapNotNull null
        OfferMenuItem(
            id = parts[0],
            title = parts[1],
            description = parts[2],
            price = parts[3],
            imageColor = colors.imagePlaceholder1,
            quantity = parts[4].toIntOrNull() ?: 1
        )
    }
}

@Composable
private fun BookingSection(
    orderDetails: OrderDetails,
    onEditClick: () -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Booking",
                    style = typography.cardTitle,
                    color = colors.textPrimary
                )

                // remove this because the UI is too complicated for a UI prototype
//                OutlinedButton(
//                    onClick = onEditClick,
//                    modifier = Modifier.height(40.dp),
//                    shape = RoundedCornerShape(20.dp),
//                    colors = ButtonDefaults.outlinedButtonColors(
//                        contentColor = colors.chefPrimary
//                    ),
//                    border = ButtonDefaults.outlinedButtonBorder.copy(
//                        brush = SolidColor(colors.chefPrimary)
//                    )
//                ) {
//                    Text(
//                        text = "Edit",
//                        style = typography.labelMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
            }
            
            Text(
                text = "${orderDetails.date} • ${orderDetails.time} • ${orderDetails.guests} guests",
                style = typography.bodyMedium,
                color = colors.textPrimary
            )
            
            Text(
                text = "Venue: ${orderDetails.venue}",
                style = typography.bodyMedium,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun OfferMenuItemCard(
    item: OfferMenuItem,
    isEditable: Boolean = true,
    onQuantityDecrease: () -> Unit,
    onQuantityIncrease: () -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image - use real image if available, otherwise use color placeholder
            val imageName = remember(item.title) { getImageNameFromTitle(item.title) }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (imageName != null) {
                    Image(
                        painter = rememberImagePainter(imageName),
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // No image available, use color placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(item.imageColor)
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = typography.cardTitle,
                    color = colors.textPrimary
                )
                Text(
                    text = item.description,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            
            // Price and quantity selector
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.price,
                    style = typography.cardTitle,
                    color = colors.textPrimary
                )
                if (isEditable) {
                    QuantitySelector(
                        quantity = item.quantity,
                        onDecrease = onQuantityDecrease,
                        onIncrease = onQuantityIncrease
                    )
                } else {
                    // Show quantity as simple text for non-editable orders
                    Text(
                        text = "x${item.quantity}",
                        style = typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceSummarySection(priceSummary: PriceSummary, orderStatus: OrderStatus = OrderStatus.DRAFT) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Calculate total (subtotal + service fee)
    val subtotalValue = priceSummary.subtotal.replace("€", "").replace(",", ".").toDoubleOrNull() ?: 0.0
    val serviceFeeValue = priceSummary.serviceFee.replace("€", "").replace(",", ".").toDoubleOrNull() ?: 0.0
    val totalValue = subtotalValue + serviceFeeValue
    val totalText = "€${totalValue.toInt()}"
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Price summary",
            style = typography.cardTitle,
            color = colors.textPrimary
        )
        
        // Subtotal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Subtotal",
                style = typography.bodyMedium,
                color = colors.textPrimary
            )
            Text(
                text = priceSummary.subtotal,
                style = typography.labelMedium,
                color = colors.textPrimary
            )
        }
        
        // Service fee
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Service fee",
                style = typography.bodyMedium,
                color = colors.textPrimary
            )
            Text(
                text = priceSummary.serviceFee,
                style = typography.labelMedium,
                color = colors.textPrimary
            )
        }
        
        // Total (subtotal + service fee) - bold
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                style = typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = totalText,
                style = typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
        
        // Deposit information - conditional based on status
        if (orderStatus != OrderStatus.COMPLETED) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = colors.statusConfirmedBackground // Light green
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (orderStatus == OrderStatus.CONFIRMED) "Deposit paid" else "Deposit due now",
                        style = typography.labelMedium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "${priceSummary.depositAmount} (${priceSummary.depositPercentage}%)",
                        style = typography.labelMedium,
                        color = colors.chefPrimary
                    )
                }
            }
        }
    }
}

