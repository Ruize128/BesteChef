package nl.tue.hci.feature.diner.preview.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.ChefResult
import nl.tue.hci.feature.diner.components.ChefResultCard

@Preview
@Composable
fun ChefResultCardPreview() {
    val chef = ChefResult(
        name = "Chef Marius van Vlaanderen",
        rating = 5.0f,
        reviewCount = 2,
        eventCount = 13,
        canTravel = true,
        availableOnDate = true,
        quote = "Enhancing classic flavors with a touch of style",
        imageColor = Color(0xFFB2E5D4) // Light mint green
    )

    ChefResultCard(
        chef = chef,
        onButtonClick = {}
    )
}