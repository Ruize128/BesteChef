package nl.tue.hci.feature.chef

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.tue.hci.feature.chef.ui.theme.BesteChefTheme


@Preview
@Composable
fun ChefScreenPreview() {
    ChefScreen(
        modifier = Modifier
    )
}

@Composable
fun ChefScreen(
    modifier: Modifier = Modifier
) {
    BesteChefTheme {
        var currentDestination by rememberSaveable { mutableStateOf(ChefDestinations.HOME) }

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                ChefDestinations.entries.forEach {
                    item(
                        icon = {
                            Icon(
                                it.icon,
                                contentDescription = it.label
                            )
                        },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it }
                    )
                }
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                when (currentDestination) {
                    ChefDestinations.HOME -> ChefHomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                    ChefDestinations.ORDERS -> ChefOrdersScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                    ChefDestinations.PROFILE -> ChefProfileScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class ChefDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    ORDERS("Orders", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

@Composable
fun ChefHomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Chef Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Welcome, Chef! Manage your kitchen and orders here.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ChefOrdersScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Orders",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "View and manage incoming orders.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ChefProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Manage your chef profile and settings.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

