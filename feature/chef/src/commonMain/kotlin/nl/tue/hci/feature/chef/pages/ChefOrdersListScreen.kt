package nl.tue.hci.feature.chef.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import nl.tue.hci.feature.chef.model.Order
import nl.tue.hci.feature.chef.model.OrderStatus

@Composable
fun ChefOrdersListScreen(
    modifier: Modifier = Modifier,
    sentOrderId: String? = null,
    onOrderClick: (String) -> Unit = {} // orderId
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Read order status from database for order 1
    val order1Status = remember(sentOrderId) {
        val dbStatus = nl.tue.hci.core.data.GlobalDatabase.readString("ichiraku_order_status")
        when (dbStatus) {
            "CANCELLED" -> OrderStatus.CANCELLED
            "COMPLETED" -> OrderStatus.COMPLETED
            "CONFIRMED" -> OrderStatus.CONFIRMED
            "ON_GOING" -> OrderStatus.CONFIRMED
            "PENDING" -> OrderStatus.CONFIRMED
            else -> if (sentOrderId == "1") OrderStatus.SENT else OrderStatus.DRAFT
        }
    }
    
    // Hardcoded orders with default mock data
    // When sentOrderId matches an order, that order's status becomes SENT
    val orders = listOf(
        Order(
            id = "1",
            customerName = "Sophie",
            orderDate = "Dec 12, 2025",
            status = order1Status,
            totalPrice = "€22",
            itemCount = 2,
            timeAgo = "2h ago"
        ),
        Order(
            id = "2",
            customerName = "Liam",
            orderDate = "Dec 12, 2025",
            status = OrderStatus.CONFIRMED,
            totalPrice = "€65",
            itemCount = 1,
            timeAgo = "1d ago"
        ),
        Order(
            id = "3",
            customerName = "Emma",
            orderDate = "Dec 11, 2025",
            status = OrderStatus.COMPLETED,
            totalPrice = "€36",
            itemCount = 3,
            timeAgo = "2d ago"
        )
    )
    
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
                    onClick = { onOrderClick(order.id) }
                )
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
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
            // Avatar
            Avatar(
                text = order.customerName.take(1).uppercase(),
                size = 48,
                backgroundColor = when (order.customerName) {
                    "Sophie" -> colors.imagePlaceholder3 // Light pink
                    "Liam" -> colors.imagePlaceholder1 // Light green
                    else -> colors.dinerSecondary // Light blue/cyan
                },
                imageName = if (order.customerName == "Sophie") "sophie" else null
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = order.customerName,
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
                        OrderStatus.DRAFT -> "Draft"
                        OrderStatus.SENT -> "Sent"
                        OrderStatus.CONFIRMED -> "Confirmed"
                        OrderStatus.COMPLETED -> "Completed"
                        OrderStatus.CANCELLED -> "Cancelled"
                    },
                    backgroundColor = when (order.status) {
                        OrderStatus.DRAFT -> colors.statusNewBackground
                        OrderStatus.SENT -> colors.statusOngoingBackground
                        OrderStatus.CONFIRMED -> colors.statusConfirmedBackground
                        OrderStatus.COMPLETED -> colors.statusConfirmedBackground
                        OrderStatus.CANCELLED -> colors.buttonBackground
                    },
                    textColor = when (order.status) {
                        OrderStatus.DRAFT -> colors.statusNewText
                        OrderStatus.SENT -> colors.statusOngoingText
                        OrderStatus.CONFIRMED -> colors.statusConfirmedText
                        OrderStatus.COMPLETED -> colors.statusConfirmedText
                        OrderStatus.CANCELLED -> colors.textPrimary
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

