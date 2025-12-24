package nl.tue.hci.feature.diner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
// Preview removed for multiplatform
import nl.tue.hci.feature.diner.ui.theme.BesteChefTheme

@Composable
fun DinerScreenPreview() {
    DinerScreen(
        modifier = Modifier
    )
}

@Composable
fun DinerScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    BesteChefTheme {
        var currentDestination by rememberSaveable { mutableStateOf(DinerDestinations.HOME) }

        DinerNavigationScaffold(
            currentDestination = currentDestination,
            onDestinationChange = { currentDestination = it },
            modifier = modifier
        ) { innerPadding ->
            when (currentDestination) {
                DinerDestinations.HOME -> DinerHomeScreen(
                    modifier = Modifier.padding(innerPadding)
                )
                DinerDestinations.FAVORITES -> DinerFavoritesScreen(
                    modifier = Modifier.padding(innerPadding)
                )
                DinerDestinations.PROFILE -> DinerProfileScreen(
                    modifier = Modifier.padding(innerPadding),
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
expect fun DinerNavigationScaffold(
    currentDestination: DinerDestinations,
    onDestinationChange: (DinerDestinations) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
)

enum class DinerDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

