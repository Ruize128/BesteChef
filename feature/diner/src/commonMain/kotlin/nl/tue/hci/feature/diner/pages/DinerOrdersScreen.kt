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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.AppColors
import nl.tue.hci.core.ui.components.Avatar
import nl.tue.hci.core.ui.components.StatusBadge
import nl.tue.hci.feature.diner.DinerOrder
import nl.tue.hci.feature.diner.DinerOrderStatus

@Composable
fun DinerOrdersScreen(
    modifier: Modifier = Modifier,
    initialOrderId: String = "",
    onOrderClick: (String) -> Unit = {},
    onBookAndPayClick: () -> Unit = {}
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
        BookingSummaryScreen(
            orderId = selectedOrderId ?: "",
            modifier = modifier,
            onBackClick = {
                showBookingSummary = false
                selectedOrderId = null
                onOrderClick("") // Clear the order selection
            },
            onBookAndPayClick = {
                showBookingSummary = false
                onBookAndPayClick()
            }
        )
    } else {
    // Hardcoded orders for diner
    val orders = listOf(
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
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orders",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        shadowElevation = 2.dp
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
                    "Sophie" -> Color(0xFFFFB3BA) // Light pink
                    "Chef Marco" -> Color(0xFFB3FFBA) // Light green
                    else -> Color(0xFFB3BAFF) // Light blue
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = "${order.itemCount} items • ${order.orderDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
            }
            
            // Right side: status and price
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(
                    text = order.status.name.replace("_", " "),
                    backgroundColor = when (order.status) {
                        DinerOrderStatus.CONFIRMED -> AppColors.StatusConfirmedBackground
                        DinerOrderStatus.COMPLETED -> AppColors.StatusConfirmedBackground
                        DinerOrderStatus.PENDING -> AppColors.StatusNewBackground
                        else -> AppColors.ButtonGrey
                    },
                    textColor = when (order.status) {
                        DinerOrderStatus.CONFIRMED -> AppColors.StatusConfirmedText
                        DinerOrderStatus.COMPLETED -> AppColors.StatusConfirmedText
                        DinerOrderStatus.PENDING -> AppColors.StatusNewText
                        else -> AppColors.TextPrimary
                    }
                )
                Text(
                    text = order.totalPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = order.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}

