package nl.tue.hci.feature.chef

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.chef.model.OfferMenuItem
import nl.tue.hci.feature.chef.model.OrderDetails
import nl.tue.hci.feature.chef.pages.OrderConfirmedScreen
import nl.tue.hci.feature.chef.ui.theme.BesteChefTheme

@Preview(showBackground = true, name = "Order Confirmed Screen")
@Composable
fun OrderConfirmedScreenPreview() {
    BesteChefTheme {
        // Mocked order details
        val orderDetails = OrderDetails(
            date = "Dec 12, 2025",
            time = "7:00 PM",
            guests = 6,
            venue = "xxxxxx"
        )
        
        // Mocked menu items
        val menuItems = listOf(
            OfferMenuItem(
                id = "1",
                title = "5-course Omakase",
                description = "per guest • €65",
                price = "€65",
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
                id = "5",
                title = "Caesar Salad",
                description = "Fresh romaine, parmesan, croutons",
                price = "€9",
                imageColor = Color(0xFFB2E5D4),
                quantity = 2
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
