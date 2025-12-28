package nl.tue.hci.feature.chef.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.AppColors
import nl.tue.hci.core.ui.components.EditButton
import nl.tue.hci.core.ui.components.QuantitySelector
import nl.tue.hci.feature.chef.model.BookingDetails
import nl.tue.hci.feature.chef.model.OfferMenuItem
import nl.tue.hci.feature.chef.model.PriceSummary
import nl.tue.hci.feature.chef.model.SelectedMenuItem
import nl.tue.hci.feature.chef.model.MenuPickerItem

@Composable
fun ComposeOfferScreen(
    orderId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAddDishClick: () -> Unit = {},
    itemsToAdd: List<SelectedMenuItem>? = null,
    onItemsAdded: () -> Unit = {}
) {
    // Hardcoded booking details
    val bookingDetails = remember {
        BookingDetails(
            date = "Dec 12, 2025",
            time = "7:00 PM",
            guests = 6,
            venue = "xxxxxx"
        )
    }
    
    // Default menu items - reset when screen is opened
    val defaultMenuItems = remember {
        listOf(
            OfferMenuItem(
                id = "1",
                title = "Yuzu mousse",
                description = "nut-free (substitute)",
                price = "€8",
                imageColor = Color(0xFFB2E5D4), // Light green
                quantity = 2
            ),
            OfferMenuItem(
                id = "2",
                title = "Seared seabass",
                description = "miso glaze",
                price = "€14",
                imageColor = Color(0xFFFFD4B2), // Light orange-beige
                quantity = 1
            )
        )
    }
    
    // Menu items with quantities - reset to default when screen is opened
    val menuItems = remember(orderId) {
        mutableStateListOf<OfferMenuItem>().apply {
            addAll(defaultMenuItems)
        }
    }
    
    // Track a key that changes when quantities change to trigger price recalculation
    var priceCalculationKey by remember(orderId) { mutableStateOf(0) }
    
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
        val serviceFee = 10.0
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
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBackClick) {
                    Text(
                        text = "Back",
                        color = AppColors.TextPrimary
                    )
                }
                
                Text(
                    text = "Compose Offer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = AppColors.TextPrimary
                )
                
                // Spacer for centering
                Spacer(modifier = Modifier.width(60.dp))
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
                    bookingDetails = bookingDetails,
                    onEditClick = {}
                )
            }
            
            // Menu section
            item {
                Text(
                    text = "Menu (editable)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            items(menuItems) { item ->
                OfferMenuItemCard(
                    item = item,
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
            
            // Add another dish button
            item {
                TextButton(
                    onClick = onAddDishClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "+ Add another dish",
                        color = AppColors.StatusConfirmedText, // Green
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Price summary section
            item {
                PriceSummarySection(priceSummary = priceSummary)
            }
        }
        
        // Send Offer button
        Button(
            onClick = {
                // TODO: Implement send offer functionality
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.ChefPrimary,
                contentColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Send Offer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = priceSummary.total,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun BookingSection(
    bookingDetails: BookingDetails,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        shadowElevation = 2.dp
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                
                TextButton(onClick = onEditClick) {
                    Text(
                        text = "Edit",
                        color = AppColors.ChefPrimary
                    )
                }
            }
            
            Text(
                text = "${bookingDetails.date} • ${bookingDetails.time} • ${bookingDetails.guests} guests",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary
            )
            
            Text(
                text = "Venue: ${bookingDetails.venue}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary
            )
        }
    }
}

@Composable
private fun OfferMenuItemCard(
    item: OfferMenuItem,
    onQuantityDecrease: () -> Unit,
    onQuantityIncrease: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(item.imageColor)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
            
            // Price and quantity selector
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.price,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                QuantitySelector(
                    quantity = item.quantity,
                    onDecrease = onQuantityDecrease,
                    onIncrease = onQuantityIncrease
                )
            }
        }
    }
}

@Composable
private fun PriceSummarySection(priceSummary: PriceSummary) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Price summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        
        // Subtotal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Subtotal",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary
            )
            Text(
                text = priceSummary.subtotal,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )
        }
        
        // Service fee
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Service fee",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextPrimary
            )
            Text(
                text = priceSummary.serviceFee,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextPrimary
            )
        }
        
        // Deposit due now (highlighted)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = AppColors.StatusConfirmedBackground // Light green
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Deposit due now",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = "${priceSummary.depositAmount} (${priceSummary.depositPercentage}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.ChefPrimary
                )
            }
        }
    }
}

