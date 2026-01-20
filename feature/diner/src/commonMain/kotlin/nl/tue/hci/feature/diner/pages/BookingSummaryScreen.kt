package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import nl.tue.hci.core.data.GlobalDatabase
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.getImageNameFromTitle
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.ui.PlatformBackHandler
import nl.tue.hci.feature.diner.BookingSummaryDetails
import nl.tue.hci.feature.diner.BookingSummaryMenuItem
import nl.tue.hci.feature.diner.BookingPriceSummary
import nl.tue.hci.feature.diner.DinerOrder
import nl.tue.hci.feature.diner.DinerOrderStatus

@Composable
fun BookingSummaryScreen(
    orderId: String,
    modifier: Modifier = Modifier,
    order: DinerOrder? = null,
    onBackClick: () -> Unit = {},
    onBookAndPayClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onPayRemainingClick: (() -> Unit)? = null
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    var isProcessing by rememberSaveable { mutableStateOf(false) }
    var showCancelDialog by rememberSaveable { mutableStateOf(false) }
    var showPaymentConfirmDialog by rememberSaveable { mutableStateOf(false) }
    
    // Determine order status based on order object or fallback to database
    val orderStatus = if (order != null) {
        when (order.status) {
            DinerOrderStatus.PENDING -> "PENDING"
            DinerOrderStatus.CONFIRMED -> "ONGOING"
            DinerOrderStatus.IN_PROGRESS -> "ONGOING"
            DinerOrderStatus.COMPLETED -> "COMPLETED"
            DinerOrderStatus.CANCELLED -> "CANCELLED"
        }
    } else {
        // Read status from database if order is not provided
        GlobalDatabase.readString("ichiraku_order_status") ?: "PENDING"
    }
    
    // Handle processing delay
    LaunchedEffect(isProcessing) {
        if (isProcessing) {
            kotlinx.coroutines.delay(1000) // 1 second delay
            
            // Update database based on order status
            when (orderStatus) {
                "PENDING" -> {
                    // Book & Pay clicked - change from PENDING to ONGOING
                    GlobalDatabase.writeString("ichiraku_order_status", "ONGOING")
                    onBookAndPayClick()
                }
                "ONGOING" -> {
                    // Pay remaining clicked - change from ONGOING to COMPLETED
                    GlobalDatabase.writeString("ichiraku_order_status", "COMPLETED")
                    onPayRemainingClick?.invoke()
                }
                else -> onBookAndPayClick()
            }
            isProcessing = false
        }
    }
    
    // Hardcoded booking summary data
    val bookingDetails = remember {
        BookingSummaryDetails(
            location = "Eindhoven",
            date = "Dec 12, 2025",
            time = "19:00",
            guests = 6,
            venue = "123 Food Street, Eindhoven"
        )
    }
    
    // Read service address from database
    val serviceAddress = remember {
        GlobalDatabase.readString("diner_service_address") ?: "Keizersgracht 123, 1015 CJ Amsterdam"
    }
    
    // Read service time from database
    val serviceTime = remember {
        GlobalDatabase.readString("diner_service_time") ?: "19:00"
    }
    
    val menuItems = remember(colors) {
        // Read menu items from database
        val stored = GlobalDatabase.readString("diner_order_menu_items")
        if (stored.isNullOrBlank()) {
            // Default fallback items if nothing in database
            listOf(
                BookingSummaryMenuItem(
                    id = "1",
                    title = "Grilled Mackerel with Miso",
                    description = "Serves 2-3 • Contains: Fish",
                    price = "€45",
                    imageColor = colors.imagePlaceholder1,
                    quantity = 2
                ),
                BookingSummaryMenuItem(
                    id = "2",
                    title = "Yuzu Mousse",
                    description = "Serves 6 • Can be nut-free",
                    price = "€12",
                    imageColor = colors.imagePlaceholder2,
                    quantity = 1
                ),
                BookingSummaryMenuItem(
                    id = "3",
                    title = "Wagyu Beef Steak",
                    description = "Serves 2 • Premium cut with truffle butter",
                    price = "€24",
                    imageColor = colors.imagePlaceholder4,
                    quantity = 2
                ),
                BookingSummaryMenuItem(
                    id = "4",
                    title = "Sushi Platter",
                    description = "Serves 4-5 • Assorted fresh nigiri and maki",
                    price = "€40",
                    imageColor = colors.imagePlaceholder1,
                    quantity = 1
                )
            )
        } else {
            // Decode items from database
            stored.split("||").mapIndexed { index, encoded ->
                val parts = encoded.split("|")
                if (parts.size >= 5) {
                    val colorIndex = index % 4
                    val itemColor = when (colorIndex) {
                        0 -> colors.imagePlaceholder1
                        1 -> colors.imagePlaceholder2
                        2 -> colors.imagePlaceholder4
                        else -> colors.imagePlaceholder1
                    }
                    BookingSummaryMenuItem(
                        id = (index + 1).toString(),
                        title = parts[0],
                        description = "Serves ${parts[3]} • ${parts[1]}",
                        price = parts[2],
                        imageColor = itemColor,
                        quantity = parts[4].toIntOrNull() ?: 1
                    )
                } else {
                    null
                }
            }.filterNotNull()
        }
    }
    
    val priceSummary = remember(orderStatus, menuItems) {
        // Calculate subtotal from menu items
        val subtotal = menuItems.sumOf { item ->
            val priceStr = item.price.replace("€", "").replace(",", ".")
            (priceStr.toDoubleOrNull() ?: 0.0) * item.quantity
        }
        val serviceFee = 15.0
        val total = subtotal + serviceFee
        
        val depositPercentage = when (orderStatus) {
            "PENDING" -> 20 // 20% deposit for pending
            "ON_GOING" -> 0 // No deposit for ongoing, show remaining
            "COMPLETED" -> 0 // No deposit for completed, show total
            "CANCELLED" -> 0 // No deposit for cancelled, show total
            else -> 20
        }
        
        val depositAmount = when (orderStatus) {
            "PENDING" -> total * 20 / 100.0 // 20% deposit
            "ON_GOING" -> total * 80 / 100.0 // 80% remaining to pay
            "COMPLETED" -> total // Show total price
            "CANCELLED" -> total // Show total price
            else -> total * 20 / 100.0
        }
        
        BookingPriceSummary(
            subtotal = "€${subtotal.toInt()}",
            serviceFee = "€${serviceFee.toInt()}",
            depositAmount = "€${depositAmount.toInt()}",
            depositPercentage = depositPercentage
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant) // Light gray background
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary
                    )
                }
                
                Text(
                    text = "Booking summary",
                    style = typography.sectionTitle,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                
                // Cancel button (only for PENDING and ON_GOING orders)
                if (orderStatus == "PENDING" || orderStatus == "ON_GOING") {
                    Button(
                        onClick = { showCancelDialog = true },
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.alert,
                            contentColor = colors.onAlert,
                            disabledContainerColor = colors.buttonBackgroundDisabled,
                            disabledContentColor = colors.textSecondary
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
                    // Spacer for completed orders
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        }
        
        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // redundant infomation
//            // Overview card
//            item {
//                Surface(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(40.dp),
//                    shape = RoundedCornerShape(20.dp),
//                    color = colors.surface, // Light grey
//                    shadowElevation = 0.dp,
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(horizontal = 12.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "${bookingDetails.location} • ${bookingDetails.date} • ${bookingDetails.guests} guests",
//                            style = typography.bodyMedium,
//                            color = colors.textSecondary,
//                            textAlign = TextAlign.Start,
//                        )
//                    }
//                }
//            }
            
            // Date & Time and Guests section (combined in one card)
            item {
                DateAndGuestsCard(
                    date = "${bookingDetails.date} • ${bookingDetails.time}",
                    guests = "${bookingDetails.guests} guests",
                    onDateEditClick = {},
                    onGuestsChangeClick = {}
                )
            }

            // Address section
            item {
                BookingSection(
                    title = "Service Address",
                    value = serviceAddress,
                    onEditClick = {},
                    editButtonText = "Edit"
                )
            }
            
//            // Service Time section
//            item {
//                BookingSection(
//                    title = "Service Time",
//                    value = "Today $serviceTime",
//                    onEditClick = {},
//                    editButtonText = "Edit"
//                )
//            }
            
            // Menu summary section
            item {
                Text(
                    text = "Menu summary",
                    style = typography.cardTitle,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            items(menuItems) { item ->
                MenuItemCard(item = item)
            }
            
            // Price summary section
            item {
                PriceSummarySection(priceSummary = priceSummary, orderStatus = orderStatus)
            }
        }
        
        // Payment button - different text based on status
        if (orderStatus == "PENDING" || orderStatus == "ONGOING") {
            Button(
                onClick = {
                    if (orderStatus == "ONGOING") {
                        showPaymentConfirmDialog = true
                    } else {
                        isProcessing = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp, top = 16.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.dinerPrimary,
                    contentColor = colors.textPrimary
                ),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Processing...",
                        style = typography.cardTitle,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                } else {
                    val buttonText = when (orderStatus) {
                        "PENDING" -> "Book & Pay ${priceSummary.depositAmount} deposit (${priceSummary.depositPercentage}%)"
                        "ONGOING" -> "Pay remaining balance ${priceSummary.depositAmount}"
                        else -> "Book & Pay ${priceSummary.depositAmount}"
                    }
                    Text(
                        text = buttonText,
                        style = typography.cardTitle,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
            }
        } else {
            // Completed order - no action buttons
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Handle system back gesture - same behavior as back arrow
    PlatformBackHandler {
        onBackClick()
    }
    
    // Cancel confirmation dialog
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
                                textAlign = TextAlign.Center
                            )
                    }
                    
                    Button(
                        onClick = {
                            showCancelDialog = false
                            // Write CANCELLED status to database
                            GlobalDatabase.writeString("ichiraku_order_status", "CANCELLED")
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
                                textAlign = TextAlign.Center
                            )
                    }
                }
            },
            containerColor = colors.surface,
            titleContentColor = colors.textPrimary,
            textContentColor = colors.textSecondary
        )
    }
    
    // Payment confirmation dialog for remaining balance
    if (showPaymentConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentConfirmDialog = false },
            title = {
                Text(
                    text = "Pay remaining balance?",
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Please make sure the service has finished before proceeding with the payment.",
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
                        onClick = { showPaymentConfirmDialog = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surfaceVariant,
                            contentColor = colors.textPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Not\nyet",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Button(
                        onClick = {
                            showPaymentConfirmDialog = false
                            isProcessing = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.dinerPrimary,
                            contentColor = colors.textPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Confirm payment",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            containerColor = colors.surface,
            titleContentColor = colors.textPrimary,
            textContentColor = colors.textSecondary
        )
    }
}

@Composable
private fun DateAndGuestsCard(
    date: String,
    guests: String,
    onDateEditClick: () -> Unit,
    onGuestsChangeClick: () -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface, // Pure white card
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date & Time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Date & time",
                        style = typography.cardTitle,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = date,
                        style = typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                }

                // Remove here because it is not the responsibility of this iteration
//                TextButton(onClick = onDateEditClick) {
//                    Text(
//                        text = "Edit",
//                        color = colors.dinerPrimary
//                    )
//                }
            }
            
            // Guests row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Guests",
                        style = typography.cardTitle,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = guests,
                        style = typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                }

                // Remove here because it is not the responsibility of this iteration
//                TextButton(onClick = onGuestsChangeClick) {
//                    Text(
//                        text = "Change",
//                        color = colors.dinerPrimary
//                    )
//                }
            }
        }
    }
}

@Composable
private fun BookingSection(
    title: String,
    value: String,
    onEditClick: () -> Unit,
    editButtonText: String = "Edit"
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface, // Pure white card
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = typography.cardTitle,
                    fontStyle = FontStyle.Italic,
                    color = colors.textPrimary,
                )
                Text(
                    text = value,
                    style = typography.bodyMedium,
                    color = colors.textSecondary,
                )
            }
            // Remove here because it is not the responsibility of this iteration
//            TextButton(onClick = onEditClick) {
//                Text(
//                    text = editButtonText,
//                    color = colors.dinerPrimary
//                )
//            }
        }
    }
}

@Composable
private fun MenuItemCard(item: BookingSummaryMenuItem) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface, // Pure white card
        shadowElevation = 0.dp
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
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = typography.cardTitle.copy(fontSize = 14.sp),
                        color = colors.textPrimary
                    )
                }
                Text(
                    text = item.description,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            
            // Price
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantityBadge(quantity = item.quantity)
                Text(
                    text = item.price,
                    style = typography.cardTitle,
                    color = colors.textPrimary
                )
            }
        }
    }
}

@Composable
private fun QuantityBadge(quantity: Int) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()

    Box(
        modifier = Modifier
            .background(
                color = colors.textPrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = "${quantity}x",
            style = typography.bodySmall,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PriceSummarySection(priceSummary: BookingPriceSummary, orderStatus: String) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Price summary",
                style = typography.cardTitle,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = priceSummary.subtotal,
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Service fee",
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = priceSummary.serviceFee,
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = colors.surfaceVariant,
                    )
            )
            
            // Last line - different text based on status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (orderStatus) {
                        "PENDING" -> "Deposit due now"
                        "ON_GOING" -> "Remaining to pay"
                        "COMPLETED" -> "Total paid"
                        "CANCELLED" -> "Total price"
                        else -> "Deposit due now"
                    },
                    style = typography.cardTitle,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = when (orderStatus) {
                        "PENDING" -> "${priceSummary.depositAmount} (${priceSummary.depositPercentage}%)"
                        "ON_GOING" -> priceSummary.depositAmount
                        "COMPLETED" -> priceSummary.depositAmount
                        "CANCELLED" -> priceSummary.depositAmount
                        else -> "${priceSummary.depositAmount} (${priceSummary.depositPercentage}%)"
                    },
                    style = typography.cardTitle,
                    fontWeight = FontWeight.Bold,
                    color = colors.dinerPrimary
                )
            }
        }
    }
}

