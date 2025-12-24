package nl.tue.hci.feature.diner.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
// PopupProperties removed for multiplatform compatibility


/**
 * A reusable location dropdown menu component with search functionality.
 * This component only contains the dropdown menu itself, not the field display.
 * 
 * @param expanded Whether the dropdown menu is currently open
 * @param onDismissRequest Callback when the dropdown should be closed
 * @param searchQuery The current search query text
 * @param onSearchQueryChange Callback when the search query changes
 * @param onLocationSelected Callback when a location is selected
 * @param currentPositionCity The city name for the "current position" option (default: "Amsterdam")
 * @param cities List of available cities to choose from
 * @param modifier Modifier for the dropdown menu
 */
@Composable
fun LocationDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLocationSelected: (String) -> Unit,
    currentPositionCity: String = "Amsterdam",
    cities: List<String> = defaultCities,
    modifier: Modifier = Modifier
) {
    // Filter cities based on search query
    val filteredCities = if (searchQuery.isBlank()) {
        cities
    } else {
        cities.filter { it.contains(searchQuery, ignoreCase = true) }
    }
    
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .fillMaxWidth(0.9f)
            .heightIn(max = 400.dp)
    ) {
        // Search input bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text("Search cities...") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
        
        Divider()
        
        // Current position option
        DropdownMenuItem(
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Current position",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = currentPositionCity,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            onClick = {
                onLocationSelected(currentPositionCity)
            }
        )
        
        Divider()
        
        // Filtered cities list
        if (filteredCities.isEmpty()) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "No cities found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                onClick = {}
            )
        } else {
            filteredCities.forEach { city ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = city,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = city,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    onClick = {
                        onLocationSelected(city)
                    }
                )
            }
        }
    }
}

// Default list of cities (hardcoded for UI prototype)
private val defaultCities = listOf(
    "Rotterdam", "The Hague", "Utrecht", "Eindhoven", "Groningen",
    "Tilburg", "Almere", "Breda", "Nijmegen", "Enschede",
    "Haarlem", "Arnhem", "Zaanstad", "Amersfoort", "Amsterdam", "Apeldoorn",
    "Hoofddorp", "Maastricht", "Leiden", "Dordrecht", "Zoetermeer"
)

