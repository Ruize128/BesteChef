package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp

@Composable
fun DinerFavoritesScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "View your favorite dishes and restaurants.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

