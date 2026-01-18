package nl.tue.hci.feature.chef

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.chef.model.OfferMenuItem
import nl.tue.hci.feature.chef.model.OrderDetails
import nl.tue.hci.feature.chef.model.OrderStatus
import nl.tue.hci.feature.chef.pages.OrderConfirmedScreen
import nl.tue.hci.core.ui.BesteChefTheme

@Preview(showBackground = true, name = "Order Confirmed Screen")
@Composable
fun OrderConfirmedScreenPreview() {
    BesteChefTheme {
        // Mocked order details
        val orderDetails = OrderDetails(
            date = "Dec 12, 2025",
            time = "7:00 PM",
            guests = 6,
            venue = "xxxxxx",
            status = OrderStatus.DRAFT // Provide default status
        )
        
        // Mocked menu items
        val menuItems = listOf(
            OfferMenuItem(
                id = "1",
                title = "Grilled Mackerel with Miso",
                description = "Sea salt, spring onion, yuzu dressing.",
                price = "€45",
                imageColor = Color(0xFFB2E5D4), // Light green
                quantity = 1
            ),

            OfferMenuItem(
                id = "2",
                title = "Yuzu mousse (sub)",
                description = "nut-free (confirmed)",
                price = "€8",
                imageColor = Color(0xFFFFB3BA), // Light pink
                quantity = 2
            ),
            OfferMenuItem(
                id = "3",
                title = "Wagyu Beef Steak",
                description = "Premium wagyu with truffle butter.",
                price = "€90",
                imageColor = Color(0xFFE0D4C4),
                quantity = 1
            ),
            OfferMenuItem(
                id = "4",
                title = "Sushi Platter",
                description = "Assorted fresh sushi with wasabi and ginger.",
                price = "€70",
                imageColor = Color(0xFFB2E5D4),
                quantity = 1
            )
        )
        
        OrderConfirmedScreen(
            orderDetails = orderDetails,
            menuItems = menuItems,
            bookingNumber = "12345",
            chefName = "Sophie",
            cuisineType = "Seasonal Japanese Fusion",
            modifier = Modifier,
            onDoneClick = {}
        )
    }
}
