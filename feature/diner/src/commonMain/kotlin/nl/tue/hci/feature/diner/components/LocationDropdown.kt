package nl.tue.hci.feature.diner.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography


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
    
    if (expanded) {
        Popup(
            onDismissRequest = onDismissRequest,
            alignment = Alignment.TopCenter,
            offset = IntOffset(0, 100),
            properties = PopupProperties(focusable = true)
        ) {
            val colors = BesteChefThemeColors.current()
            val typography = BesteChefThemeTypography.current()

            Card(
                modifier = modifier
                    .width(340.dp)
                    .heightIn(max = 400.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = BorderStroke(1.dp, colors.outline)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    // Search input bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.surfaceVariant,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = colors.textSecondary.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = typography.bodyMedium.copy(color = colors.textPrimary),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Search cities...",
                                            style = typography.bodyMedium,
                                            color = colors.textSecondary.copy(alpha = 0.6f)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Cities list
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Current position option
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onLocationSelected(currentPositionCity) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Current position",
                                    tint = colors.chefPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = currentPositionCity,
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = colors.outline.copy(alpha = 0.3f)
                            )
                        }
                        
                        // Filtered cities
                        if (filteredCities.isEmpty()) {
                            item {
                                Text(
                                    text = "No cities found",
                                    style = typography.bodyMedium,
                                    color = colors.textSecondary.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        } else {
                            items(filteredCities) { city ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onLocationSelected(city) }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = city,
                                        tint = colors.textSecondary.copy(alpha = 0.6f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = city,
                                        style = typography.bodyMedium,
                                        color = colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
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

