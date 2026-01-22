package nl.tue.hci.feature.diner.pages
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.ui.components.ImagePreviewOverlay
import nl.tue.hci.core.data.GlobalDatabase

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.Image
import nl.tue.hci.core.ui.PlatformBackHandler
import androidx.compose.ui.layout.ContentScale
import nl.tue.hci.core.ui.getImageNameFromTitle
import nl.tue.hci.core.ui.getCarouselImageNames
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.ui.components.QuantitySelector
import nl.tue.hci.core.ui.icons.rememberIconPainter
import nl.tue.hci.feature.diner.MenuItem
import nl.tue.hci.feature.diner.components.formatDate
import kotlinx.datetime.LocalDate


@Composable
fun MenuScreen(
    chefName: String,
    menuName: String = "",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {}, // Callback to navigate to chat section
    onBookClick: () -> Unit = {}
) {
    MenuContent(
        chefName = chefName,
        menuName = menuName,
        modifier = modifier,
        onBackClick = onBackClick,
        onChatClick = {
            onChatClick(chefName) // Pass chef name to navigate to chat
        },
        onBookClick = onBookClick
    )
}

@Composable
private fun QuantityBadge(quantity: Int, modifier: Modifier = Modifier) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    val fontSize = with(LocalDensity.current) { 12.dp.toSp() }

    Box(
        modifier = modifier
            .background(
                color = colors.textPrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = "${quantity}x",
            style = typography.bodySmall.copy(fontSize = fontSize),
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuContent(
    chefName: String,
    menuName: String = "",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onBookClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    var imagePreviewShown by rememberSaveable { mutableStateOf(false) }
    var currentPreviewImage by rememberSaveable { mutableStateOf<String?>(null) }
    var serviceAddress by rememberSaveable { mutableStateOf("Keizersgracht 123, 1015 CJ Amsterdam") }
    var showAddressEditDialog by rememberSaveable { mutableStateOf(false) }
    var tempAddress by rememberSaveable { mutableStateOf(serviceAddress) }
    var serviceTime by rememberSaveable { mutableStateOf("19:00") }
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var selectedHour by rememberSaveable { mutableStateOf(19) }
    var selectedMinute by rememberSaveable { mutableStateOf(0) }
    
    var isHourSnapping by remember { mutableStateOf(false) }
    var isMinuteSnapping by remember { mutableStateOf(false) }
    var centerHourIndex by remember { mutableStateOf(69) }
    var centerMinuteIndex by remember { mutableStateOf(102) }
    
    // Hardcoded menu items
    val menuItems = remember(colors) {
        listOf(
            MenuItem(
                title = "Grilled Mackerel with Miso",
                description = "Sea salt, spring onion, yuzu dressing.",
                serves = "2-3",
                prepTime = "45 min prep",
                imageColor = colors.imagePlaceholder1, // Light green
                defaultNumber = 2,
                price = "€45"
            ),
            MenuItem(
                title = "Honey Nut & Caramel",
                description = "Roasted nuts with salted caramel glaze.",
                serves = "6",
                prepTime = "30 min prep",
                imageColor = colors.imagePlaceholder2, // Light orange/peach
                defaultNumber = 1,
                price = "€12"
            ),
            MenuItem(
                title = "Wagyu Beef Steak",
                description = "Premium wagyu with truffle butter and seasonal vegetables.",
                serves = "2",
                prepTime = "60 min prep",
                imageColor = colors.imagePlaceholder4, // Light beige
                defaultNumber = 2,
                price = "€24"
            ),
            MenuItem(
                title = "Sushi Platter",
                description = "Assorted fresh sushi with wasabi and pickled ginger.",
                serves = "4-5",
                prepTime = "90 min prep",
                imageColor = colors.imagePlaceholder1, // Light green
                defaultNumber = 1,
                price = "€40"
            )
        )
    }

    var showCartSheet by rememberSaveable { mutableStateOf(false) }
    var showBookConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf(menuItems.associate { it.title to it.defaultNumber }.toMutableMap()) }

    // Load cart from database on open
    LaunchedEffect(Unit) {
        val storedCart = GlobalDatabase.readString("diner_order_menu_items")
        if (!storedCart.isNullOrBlank()) {
            val loadedCart = mutableMapOf<String, Int>()
            storedCart.split("||").forEach { itemStr ->
                val parts = itemStr.split("|")
                if (parts.size >= 5) {
                    val title = parts[0]
                    val qty = parts[4].toIntOrNull() ?: 0
                    if (qty > 0) {
                        loadedCart[title] = qty
                    }
                }
            }
            if (loadedCart.isNotEmpty()) {
                cartItems = loadedCart
            }
        }

        // Auto-open cart if flag is set
        if (GlobalDatabase.readString("diner_open_cart") == "true") {
            showCartSheet = true
            GlobalDatabase.writeString("diner_open_cart", "false")
        }
    }

    // Save cart to database whenever it changes
    LaunchedEffect(cartItems) {
        val itemsData = cartItems.entries.filter { it.value > 0 }.joinToString("||") { (title, qty) ->
            val item = menuItems.find { it.title == title }
            "$title|${item?.description ?: ""}|${item?.price ?: ""}|${item?.serves ?: ""}|$qty"
        }
        GlobalDatabase.writeString("diner_order_menu_items", itemsData)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button and chat icon
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = {
                            if (imagePreviewShown) {
                                imagePreviewShown = false
                                currentPreviewImage = null
                            } else {
                                onBackClick()
                            }
                        },
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
                        text = if (menuName.isNotEmpty()) menuName else chefName,
                        style = typography.titleLarge,
                        color = colors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onChatClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        val chatIconPainter = rememberImagePainter("comments_light")
                        Icon(
                            painter = chatIconPainter,
                            contentDescription = "Chat",
                            tint = colors.textPrimary
                        )
                    }
                }
            }
            
            // Menu items list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    val qty = cartItems[item.title] ?: 1
                    MenuItemCard(
                        menuItem = item,
                        quantity = qty,
                        onDecrease = {
                            val current = cartItems[item.title] ?: 1
                            if (current > 0) {
                                cartItems = cartItems.toMutableMap().apply { this[item.title] = current - 1 }
                            }
                        },
                        onIncrease = {
                            val current = cartItems[item.title] ?: 1
                            cartItems = cartItems.toMutableMap().apply { this[item.title] = current + 1 }
                        },
                        onImageClick = { imageName ->
                            imagePreviewShown = true
                            currentPreviewImage = imageName
                        }
                    )
                }
            }
        }

        // Floating cart button
        FloatingActionButton(
            onClick = { showCartSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = colors.dinerPrimary,
            contentColor = colors.textOnPrimary
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart"
            )
        }

        // Cart bottom sheet
        if (showCartSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showCartSheet = false },
                sheetState = sheetState,
                containerColor = colors.surface,
                contentColor = colors.surface,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colors.surface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Menu items",
                        style = typography.titleMedium,
                        color = colors.textPrimary
                    )

                    cartItems.entries.forEach { (title, qty) ->
                        val menuItem = menuItems.find { it.title == title }
                        val imageName = remember(title) { getImageNameFromTitle(title) }
                        val isZero = qty == 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                when {
                                    imageName != null -> {
                                        Image(
                                            painter = rememberImagePainter(imageName),
                                            contentDescription = title,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable {
                                                    currentPreviewImage = imageName
                                                    imagePreviewShown = true
                                                },
                                            contentScale = ContentScale.Crop,
                                            alpha = if (isZero) 0.5f else 1f
                                        )
                                    }
                                    menuItem != null -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(menuItem.imageColor)
                                                .alpha(if (isZero) 0.5f else 1f)
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = title,
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary,
                                    modifier = Modifier.alpha(if (isZero) 0.5f else 1f)
                                )
                                menuItem?.let {
                                    Text(
                                        text = it.description,
                                        style = typography.bodySmall,
                                        color = colors.textSecondary,
                                        modifier = Modifier.alpha(if (isZero) 0.5f else 1f)
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                menuItem?.let {
                                    if (it.price.isNotEmpty()) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
//                                            QuantityBadge(
//                                                quantity = qty,
//                                                modifier = Modifier.alpha(if (isZero) 0.5f else 1f)
//                                            )
                                            Text(
                                                text = it.price,
                                                style = typography.bodyMedium,
                                                color = colors.textPrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.alpha(if (isZero) 0.5f else 1f)
                                            )
                                        }
                                    }
                                }
                                
                                QuantitySelector(
                                    quantity = qty,
                                    onDecrease = {
                                        val current = cartItems[title] ?: 1
                                        if (current > 0) {
                                            cartItems = cartItems.toMutableMap().apply { this[title] = current - 1 }
                                        }
                                    },
                                    onIncrease = {
                                        val current = cartItems[title] ?: 1
                                        cartItems = cartItems.toMutableMap().apply { this[title] = current + 1 }
                                    }
                                )
                            }
                        }
                    }

                    // Price summary bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surfaceVariant,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Time section
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Service Time",
                                        style = typography.labelSmall,
                                        color = colors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    // Get selected date from database
                                    val selectedDateStr = GlobalDatabase.readString("diner_selected_date")
                                    val selectedDate = if (selectedDateStr != null) {
                                        try {
                                            LocalDate.parse(selectedDateStr)
                                        } catch (e: Exception) {
                                            null
                                        }
                                    } else null
                                    
                                    val dateDisplay = if (selectedDate != null) {
                                        formatDate(selectedDate)
                                    } else {
                                        "Today"
                                    }
                                    
                                    Text(
                                        text = "$dateDisplay $serviceTime",
                                        style = typography.bodyMedium,
                                        color = colors.textPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "edit",
                                    style = typography.labelMedium,
                                    color = colors.dinerPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .clickable { showTimePickerDialog = true }
                                )
                            }
                            
                            // Address section
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Service Address",
                                        style = typography.labelSmall,
                                        color = colors.textSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = serviceAddress,
                                        style = typography.bodyMedium,
                                        color = colors.textPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "edit",
                                    style = typography.labelMedium,
                                    color = colors.dinerPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .clickable { showAddressEditDialog = true }
                                )
                            }
                            
                            Divider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = colors.textSecondary.copy(alpha = 0.2f)
                            )
                            
                            // Price details
                            val subtotal = cartItems.entries.sumOf { (title, qty) ->
                                val menuItem = menuItems.find { it.title == title }
                                val priceValue = menuItem?.price?.replace("€", "")?.toDoubleOrNull() ?: 0.0
                                priceValue * qty
                            }
                            val serviceFee = 15.0
                            val total = subtotal + serviceFee
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Price",
                                    style = typography.bodyMedium,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "€${subtotal.toInt()}",
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Service Fee",
                                    style = typography.bodyMedium,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "€${serviceFee.toInt()}",
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total",
                                    style = typography.titleMedium,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "€${total.toInt()}",
                                    style = typography.titleMedium,
                                    color = colors.dinerPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                // Reset to defaults and close
                                cartItems = menuItems.associate { it.title to 1 }.toMutableMap()
                                showCartSheet = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceVariant,
                                contentColor = colors.textPrimary
                            )
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                showBookConfirmDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.dinerPrimary,
                                contentColor = colors.textOnPrimary
                            )
                        ) {
                            Text("Book")
                        }
                    }
                }
            }
        }

        if (showBookConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showBookConfirmDialog = false },
                title = {
                    Text(
                        text = "Confirm booking?",
                        style = typography.titleLarge,
                        color = colors.textPrimary
                    )
                },
                text = {
                    Text(
                        text = "Proceed to book this menu and open booking summary?",
                        style = typography.bodyMedium,
                        color = colors.textSecondary
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showBookConfirmDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceVariant,
                                contentColor = colors.textPrimary
                            )
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                showBookConfirmDialog = false
                                showCartSheet = false
                                // Write order status and items to database
                                GlobalDatabase.writeString("diner_booking_status", "PENDING")
                                
                                // Encode and save menu items with quantities
                                val itemsData = menuItems.mapNotNull { item ->
                                    val qty = cartItems[item.title] ?: 0
                                    if (qty > 0) {
                                        "${item.title}|${item.description}|${item.price}|${item.serves}|$qty"
                                    } else null
                                }.joinToString("||")
                                GlobalDatabase.writeString("diner_order_menu_items", itemsData)
                                
                                // Save service address to database
                                GlobalDatabase.writeString("diner_service_address", serviceAddress)
                                
                                // Save service time to database
                                GlobalDatabase.writeString("diner_service_time", serviceTime)
                                
                                onBookClick()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.dinerPrimary,
                                contentColor = colors.textOnPrimary
                            )
                        ) {
                            Text("Confirm")
                        }
                    }
                },
                containerColor = colors.surface,
                titleContentColor = colors.textPrimary,
                textContentColor = colors.textSecondary
            )
        }

        // Handle back button when image preview is showing
        PlatformBackHandler(enabled = imagePreviewShown) {
            imagePreviewShown = false
            currentPreviewImage = null
        }

        // Image Preview Overlay
        ImagePreviewOverlay(
            showPreview = imagePreviewShown,
            imageName = currentPreviewImage,
            onDismiss = {
                imagePreviewShown = false
                currentPreviewImage = null
            }
        )
        
        // Address Edit Dialog
        if (showAddressEditDialog) {
            AlertDialog(
                onDismissRequest = { showAddressEditDialog = false },
                title = {
                    Text(
                        text = "Edit Delivery Address",
                        style = typography.titleMedium,
                        color = colors.textPrimary
                    )
                },
                text = {
                    OutlinedTextField(
                        value = tempAddress,
                        onValueChange = { tempAddress = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colors.surface,
                            unfocusedContainerColor = colors.surface,
                            focusedBorderColor = colors.dinerPrimary,
                            unfocusedBorderColor = colors.outline,
                        ),
                        placeholder = {
                            Text(
                                text = "Enter delivery address",
                                color = colors.textSecondary
                            )
                        }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            serviceAddress = tempAddress
                            showAddressEditDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.dinerPrimary,
                            contentColor = colors.textOnPrimary
                        )
                    ) {
                        Text(
                            text = "Save",
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            tempAddress = serviceAddress
                            showAddressEditDialog = false
                        }
                    ) {
                        Text(
                            text = "Cancel",
                            color = colors.dinerPrimary
                        )
                    }
                },
                containerColor = colors.surface,
                titleContentColor = colors.textPrimary,
                textContentColor = colors.textPrimary
            )
        }
        
        // Time Picker Dialog
        if (showTimePickerDialog) {
            TimePickerDialog(
                showDialog = showTimePickerDialog,
                onDismiss = { showTimePickerDialog = false },
                onSave = { 
                    serviceTime = "${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}"
                    showTimePickerDialog = false
                },
                selectedHour = selectedHour,
                onHourChange = { selectedHour = it },
                centerHourIndex = centerHourIndex,
                onCenterHourIndexChange = { centerHourIndex = it },
                isHourSnapping = isHourSnapping,
                onHourSnappingChange = { isHourSnapping = it },
                selectedMinute = selectedMinute,
                onMinuteChange = { selectedMinute = it },
                centerMinuteIndex = centerMinuteIndex,
                onCenterMinuteIndexChange = { centerMinuteIndex = it },
                isMinuteSnapping = isMinuteSnapping,
                onMinuteSnappingChange = { isMinuteSnapping = it }
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    selectedHour: Int,
    onHourChange: (Int) -> Unit,
    centerHourIndex: Int,
    onCenterHourIndexChange: (Int) -> Unit,
    isHourSnapping: Boolean,
    onHourSnappingChange: (Boolean) -> Unit,
    selectedMinute: Int,
    onMinuteChange: (Int) -> Unit,
    centerMinuteIndex: Int,
    onCenterMinuteIndexChange: (Int) -> Unit,
    isMinuteSnapping: Boolean,
    onMinuteSnappingChange: (Boolean) -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    val hourRange = (0..23).toList()
    val hours = (1..5).flatMap { hourRange } // 120 items: 5 copies of 0-23
    val minutes = List(100) { if (it % 2 == 0) 0 else 30 } + listOf(0, 30) + List(100) { if (it % 2 == 0) 0 else 30 } // 202 items: alternating 00,30 repeated
    
    val hourScrollState = rememberLazyListState(initialFirstVisibleItemIndex = 69) // Center 19 in third copy: 48 + 19 + 2 = 69
    val minuteScrollState = rememberLazyListState(initialFirstVisibleItemIndex = 102) // Center the first original 00: 100 + 2 = 102
    
    // Initialize selection when dialog opens
    LaunchedEffect(Unit) {
        // Select the center item (index 2 in the visible viewport)
        val firstVisibleIndex = hourScrollState.firstVisibleItemIndex
        val firstVisibleOffset = hourScrollState.firstVisibleItemScrollOffset
        
        val itemsFromTop = (100 - firstVisibleOffset) / 40f
        val centerItemIndex = firstVisibleIndex + itemsFromTop.toInt()
        val actualHourIndex = (centerItemIndex - 2) % 24
        
        onHourChange(actualHourIndex)
        onCenterHourIndexChange(centerItemIndex)
        
        val minuteFirstVisibleIndex = minuteScrollState.firstVisibleItemIndex
        val minuteFirstVisibleOffset = minuteScrollState.firstVisibleItemScrollOffset
        
        val minuteItemsFromTop = (100 - minuteFirstVisibleOffset) / 40f
        val minuteCenterItemIndex = minuteFirstVisibleIndex + minuteItemsFromTop.toInt()
        val actualMinute = minutes.getOrNull(minuteCenterItemIndex - 2) ?: 0
        
        onMinuteChange(actualMinute)
        onCenterMinuteIndexChange(minuteCenterItemIndex)
    }
    
    // Continuously track scroll position and update center selection in real-time
    LaunchedEffect(hourScrollState) {
        snapshotFlow { 
            hourScrollState.firstVisibleItemIndex to hourScrollState.firstVisibleItemScrollOffset 
        }.collect { (firstVisibleIndex, firstVisibleOffset) ->
            val itemsFromTop = (100 - firstVisibleOffset) / 40f
            val centerItemIndex = firstVisibleIndex + itemsFromTop.toInt()
            val actualHourIndex = (centerItemIndex - 2) % 24
            
            onHourChange(actualHourIndex)
            onCenterHourIndexChange(centerItemIndex)
        }
    }
    
    LaunchedEffect(minuteScrollState) {
        snapshotFlow { 
            minuteScrollState.firstVisibleItemIndex to minuteScrollState.firstVisibleItemScrollOffset 
        }.collect { (firstVisibleIndex, firstVisibleOffset) ->
            val itemsFromTop = (100 - firstVisibleOffset) / 40f
            val centerItemIndex = firstVisibleIndex + itemsFromTop.toInt()
            val actualMinute = minutes.getOrNull(centerItemIndex - 2) ?: 0
            
            onMinuteChange(actualMinute)
            onCenterMinuteIndexChange(centerItemIndex)
        }
    }
    
    // Track when user stops scrolling and snap to center
    LaunchedEffect(hourScrollState.isScrollInProgress) {
        snapshotFlow { hourScrollState.isScrollInProgress }
            .collect { isScrolling ->
                if (!isScrolling && !isHourSnapping) {
                    // Calculate center item: viewport is 200dp, each item is 40dp
                    // Center is at 100dp (item index 2 when accounting for padding)
                    val firstVisibleIndex = hourScrollState.firstVisibleItemIndex
                    val firstVisibleOffset = hourScrollState.firstVisibleItemScrollOffset
                    
                    // Determine which item is closest to center
                    val itemsFromTop = (100 - firstVisibleOffset) / 40f
                    val centerItemIndex = firstVisibleIndex + itemsFromTop.toInt()
                    
                    // Adjust for the 2 padding items at the top
                    val actualHourIndex = (centerItemIndex - 2) % 24
                    
                    onHourChange(actualHourIndex)
                    onCenterHourIndexChange(centerItemIndex)
                    
                    // Snap to center position in the same copy
                    val copyIndex = (centerItemIndex - 2) / 24
                    val targetIndex = copyIndex * 24 + actualHourIndex + 2
                    onHourSnappingChange(true)
                    hourScrollState.animateScrollToItem(targetIndex, scrollOffset = 0)
                    onHourSnappingChange(false)
                }
            }
    }
    
    LaunchedEffect(minuteScrollState.isScrollInProgress) {
        snapshotFlow { minuteScrollState.isScrollInProgress }
            .collect { isScrolling ->
                if (!isScrolling && !isMinuteSnapping) {
                    val firstVisibleIndex = minuteScrollState.firstVisibleItemIndex
                    val firstVisibleOffset = minuteScrollState.firstVisibleItemScrollOffset
                    
                    val itemsFromTop = (100 - firstVisibleOffset) / 40f
                    val centerItemIndex = firstVisibleIndex + itemsFromTop.toInt()
                    
                    val actualMinute = minutes.getOrNull(centerItemIndex - 2) ?: 0
                    
                    onMinuteChange(actualMinute)
                    onCenterMinuteIndexChange(centerItemIndex)
                    
                    onMinuteSnappingChange(true)
                    minuteScrollState.animateScrollToItem(centerItemIndex, scrollOffset = 0)
                    onMinuteSnappingChange(false)
                }
            }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Service Time",
                style = typography.titleMedium,
                color = colors.textPrimary
            )
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour wheel picker
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Background highlight for selected item (center position)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(40.dp)
                            .background(
                                color = colors.dinerPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    
                    LazyColumn(
                        state = hourScrollState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top padding (2 items × 40dp = 80dp to center the first real item)
                        items(2) {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                        
                        items(hours.size) { hourIndex ->
                            val hour = hours[hourIndex]
                            Text(
                                text = hour.toString().padStart(2, '0'),
                                style = typography.bodyLarge,
                                color = colors.textPrimary,
                                fontWeight = if (hourIndex + 2 == centerHourIndex) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .alpha(if (hourIndex + 2 == centerHourIndex) 1f else 0.4f)
                                    .wrapContentHeight(Alignment.CenterVertically),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Bottom padding
                        items(2) {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
                
                // Separator
                Text(
                    text = ":",
                    style = typography.bodyLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                // Minute wheel picker
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Background highlight for selected item (center position)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(40.dp)
                            .background(
                                color = colors.dinerPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    
                    LazyColumn(
                        state = minuteScrollState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top padding
                        items(2) {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                        
                        items(minutes.size) { minuteIndex ->
                            val minute = minutes[minuteIndex]
                            Text(
                                text = minute.toString().padStart(2, '0'),
                                style = typography.bodyLarge,
                                color = colors.textPrimary,
                                fontWeight = if (minuteIndex + 2 == centerMinuteIndex) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .alpha(if (minuteIndex + 2 == centerMinuteIndex) 1f else 0.4f)
                                    .wrapContentHeight(Alignment.CenterVertically),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Bottom padding
                        items(2) {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.dinerPrimary,
                    contentColor = colors.textOnPrimary
                )
            ) {
                Text(
                    text = "Save",
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Cancel",
                    color = colors.dinerPrimary
                )
            }
        },
        containerColor = colors.surface,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textPrimary
    )
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    quantity: Int = 1,
    onDecrease: () -> Unit = {},
    onIncrease: () -> Unit = {},
    onImageClick: ((String) -> Unit)? = null
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    val isZero = quantity == 0

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image - use real image if available, otherwise use color placeholder
            val imageName = remember(menuItem.title) { getImageNameFromTitle(menuItem.title) }
            val carouselImages = remember(menuItem.title) { getCarouselImageNames(menuItem.title) }
            var currentImageIndex by remember(menuItem.title) { mutableStateOf(0) }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .alpha(if (isZero) 0.5f else 1f)
            ) {
                if (carouselImages != null && carouselImages.isNotEmpty()) {
                    // Use HorizontalPager for smooth carousel
                    ImageCarouselWithPager(
                        images = carouselImages,
                        currentIndex = currentImageIndex,
                        onIndexChange = { currentImageIndex = it },
                        onImageClick = onImageClick,
                        contentDescription = menuItem.title,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (imageName != null) {
                    // Single image
                    Image(
                        painter = rememberImagePainter(imageName),
                        contentDescription = menuItem.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (onImageClick != null) {
                                    Modifier.clickable { onImageClick(imageName) }
                                } else {
                                    Modifier
                                }
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // No image available, use color placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(menuItem.imageColor)
                    ) {
                        // Image carousel indicators (only show if using color placeholder)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(colors.textPrimary.copy(alpha = 0.6f))
                        )
                            }
                        }
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title with star icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = menuItem.title,
                        style = typography.titleMedium,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f).alpha(if (isZero) 0.5f else 1f)
                    )

                    // Quantity selector in-place (keeps in sync with cart)
                    QuantitySelector(
                        quantity = quantity,
                        onDecrease = onDecrease,
                        onIncrease = onIncrease
                    )
                }
                
                // Description
                Text(
                    text = menuItem.description,
                    style = typography.bodyMedium,
                    color = colors.textSecondary,
                    modifier = Modifier.alpha(if (isZero) 0.5f else 1f)
                )
                
                // Serves and prep time with price on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Serves ${menuItem.serves} · ${menuItem.prepTime}",
                        style = typography.bodySmall,
                        color = colors.textSecondary,
                        modifier = Modifier.alpha(if (isZero) 0.5f else 1f)
                    )
                    
                    if (menuItem.price.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            QuantityBadge(
//                                quantity = quantity,
//                                modifier = Modifier.alpha(if (isZero) 0.5f else 1f)
//                            )
                            Text(
                                text = menuItem.price,
                                style = typography.bodyMedium,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.alpha(if (isZero) 0.5f else 1f)
                            )
                        }
                    }
                }
                
                //Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons removed; chat icon on title navigates to chat
            }
        }
    }
}

