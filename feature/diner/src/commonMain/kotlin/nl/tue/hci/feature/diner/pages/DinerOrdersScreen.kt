package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.Avatar
import nl.tue.hci.core.ui.components.StatusBadge
import nl.tue.hci.feature.diner.DinerOrder
import nl.tue.hci.feature.diner.DinerOrderStatus

@Composable
fun DinerOrdersScreen(
    modifier: Modifier = Modifier,
    orders: List<DinerOrder> = emptyList(),
    initialOrderId: String = "",
    onOrderClick: (String) -> Unit = {},
    onBookAndPayClick: () -> Unit = {},
    onDeleteOrder: (String) -> Unit = {},
    onUpdateOrderStatus: (String, DinerOrderStatus) -> Unit = { _, _ -> }
) {
    var showBookingSummary by rememberSaveable { mutableStateOf(initialOrderId.isNotEmpty()) }
    var selectedOrderId by rememberSaveable { mutableStateOf(if (initialOrderId.isNotEmpty()) initialOrderId else null) }
    
    // Update selectedOrderId when initialOrderId changes
    androidx.compose.runtime.LaunchedEffect(initialOrderId) {
        if (initialOrderId.isNotEmpty() && selectedOrderId != initialOrderId) {
            selectedOrderId = initialOrderId
            showBookingSummary = true
        }
    }
    
    if (showBookingSummary && selectedOrderId != null) {
        // Find the order in the list
        val selectedOrder = orders.find { it.id == selectedOrderId }
        
        BookingSummaryScreen(
            orderId = selectedOrderId ?: "",
            order = selectedOrder,
            modifier = modifier,
            onBackClick = {
                showBookingSummary = false
                selectedOrderId = null
                onOrderClick("") // Clear the order selection
            },
            onBookAndPayClick = {
                // Update order status to CONFIRMED (ongoing)
                selectedOrderId?.let { orderId ->
                    onUpdateOrderStatus(orderId, DinerOrderStatus.CONFIRMED)
                }
                showBookingSummary = false
                onBookAndPayClick()
            },
            onCancelClick = {
                // Update order status to CANCELLED and go back to orders list
                selectedOrderId?.let { orderId ->
                    onUpdateOrderStatus(orderId, DinerOrderStatus.CANCELLED)
                }
                showBookingSummary = false
                selectedOrderId = null
            },
            onPayRemainingClick = {
                // Update order status to COMPLETED
                selectedOrderId?.let { orderId ->
                    onUpdateOrderStatus(orderId, DinerOrderStatus.COMPLETED)
                }
                showBookingSummary = false
                onBookAndPayClick()
            }
        )
    } else {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
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
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orders",
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )
            }
        }
        
        // Orders list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderCard(
                    order = order,
                    onClick = {
                        selectedOrderId = order.id
                        showBookingSummary = true
                        onOrderClick(order.id)
                    }
                )
            }
        }
    }
    }
}

@Composable
private fun OrderCard(
    order: DinerOrder,
    onClick: () -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar (chef avatar)
            Avatar(
                text = order.chefName.take(1).uppercase(),
                size = 48,
                backgroundColor = when (order.chefName) {
                    "Sophie" -> colors.imagePlaceholder3 // Light pink
                    "Chef Marco" -> colors.imagePlaceholder1 // Light green
                    else -> colors.dinerSecondary // Light blue/cyan
                },
                imageName = if (order.chefName == "Chef Ichiraku") "ichiraku" else null
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = order.chefName,
                    style = typography.cardTitle,
                    color = colors.textPrimary
                )
                Text(
                    text = "${order.itemCount} items • ${order.orderDate}",
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
            
            // Right side: status and price
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(
                    text = when (order.status) {
                        DinerOrderStatus.PENDING -> "Pending"
                        DinerOrderStatus.CONFIRMED -> "Ongoing"
                        DinerOrderStatus.IN_PROGRESS -> "Ongoing"
                        DinerOrderStatus.COMPLETED -> "Completed"
                        DinerOrderStatus.CANCELLED -> "Cancelled"
                    },
                    backgroundColor = when (order.status) {
                        DinerOrderStatus.CONFIRMED -> colors.statusOngoingBackground
                        DinerOrderStatus.IN_PROGRESS -> colors.statusOngoingBackground
                        DinerOrderStatus.COMPLETED -> colors.statusConfirmedBackground
                        DinerOrderStatus.PENDING -> colors.statusNewBackground
                        else -> colors.buttonBackground
                    },
                    textColor = when (order.status) {
                        DinerOrderStatus.CONFIRMED -> colors.statusOngoingText
                        DinerOrderStatus.IN_PROGRESS -> colors.statusOngoingText
                        DinerOrderStatus.COMPLETED -> colors.statusConfirmedText
                        DinerOrderStatus.PENDING -> colors.statusNewText
                        else -> colors.textPrimary
                    }
                )
                Text(
                    text = order.totalPrice,
                    style = typography.cardTitle,
                    color = colors.textPrimary
                )
                Text(
                    text = order.timeAgo,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

