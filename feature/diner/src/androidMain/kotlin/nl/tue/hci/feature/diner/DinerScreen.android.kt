package nl.tue.hci.feature.diner

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual @Composable
fun DinerNavigationScaffold(
    currentDestination: DinerDestinations,
    onDestinationChange: (DinerDestinations) -> Unit,
    modifier: Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            DinerDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { onDestinationChange(destination) }
                )
            }
        },
        modifier = modifier
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            content(innerPadding)
        }
    }
}

