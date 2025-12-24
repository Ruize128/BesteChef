package nl.tue.hci.feature.chef.model

import androidx.compose.ui.graphics.Color

data class ChefStats(
    val totalOrders: Int,
    val totalRevenue: String,
    val totalReviews: Int
)

data class ChefMenuItem(
    val id: String,
    val title: String,
    val description: String,
    val serves: String,
    val prepTime: String,
    val imageColor: Color
)

