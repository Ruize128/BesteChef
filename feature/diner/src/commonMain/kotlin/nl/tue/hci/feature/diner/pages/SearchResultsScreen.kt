package nl.tue.hci.feature.diner.pages
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.LocalDate
import nl.tue.hci.feature.diner.components.ActionButton
import nl.tue.hci.feature.diner.ChefResult
import nl.tue.hci.feature.diner.components.ChefResultCard
import nl.tue.hci.feature.diner.components.DateDropdownMenu
import nl.tue.hci.feature.diner.components.LocationDropdownMenu
import nl.tue.hci.feature.diner.components.formatDate
import nl.tue.hci.core.ui.icons.rememberIconPainter

// Saver for LocalDate to make it work with rememberSaveable
private val LocalDateSaver = Saver<LocalDate?, String>(
    save = { it?.toString() ?: "" },
    restore = { if (it.isEmpty()) null else LocalDate.parse(it) }
)




@Composable
fun SearchResultsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onChefClick: (String) -> Unit = {}, // Callback when chef is selected to show menu
    onChatClick: (String) -> Unit = {} // Callback to navigate to chat section
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Search parameters state
    var selectedLocation by rememberSaveable { mutableStateOf<String?>("Eindhoven") }
    var isLocationDropdownOpen by rememberSaveable { mutableStateOf(false) }
    var locationSearchQuery by rememberSaveable { mutableStateOf("") }
    
    var selectedDate by rememberSaveable(stateSaver = LocalDateSaver) { 
        mutableStateOf<LocalDate?>(LocalDate(2025, 12, 12)) 
    }
    var isDateDropdownOpen by rememberSaveable { mutableStateOf(false) }
    
    var guestsNumber by rememberSaveable { mutableStateOf("6") }
    
    // Filter modal state
    var isFilterModalOpen by rememberSaveable { mutableStateOf(false) }
    var selectedAllergens by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedCuisine by rememberSaveable { mutableStateOf<String?>(null) }
    
    val chefs = listOf(
            ChefResult(
                name = "Chef Ichiraku",
                rating = 5.0f,
                reviewCount = 2,
                eventCount = 13,
                canTravel = true,
                availableOnDate = true,
                quote = "Enhancing classic flavors with a touch of style",
                imageColor = colors.imagePlaceholder1 // Light mint green
            ),
            ChefResult(
                name = "Chef Verstappen",
                rating = 4.8f,
                reviewCount = 5,
                eventCount = 20,
                canTravel = true,
                availableOnDate = true,
                quote = "Creating memorable culinary experiences",
                imageColor = colors.imagePlaceholder2 // Light orange/peach
            ),
            ChefResult(
                name = "Chef Example Three",
                rating = 4.2f,
                reviewCount = 12,
                eventCount = 30,
                canTravel = false,
                availableOnDate = true,
                quote = "Creating memorable culinary experiences",
                imageColor = colors.imagePlaceholder1,
            )
        )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {
        // Title with back button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Search Result",
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search parameters row
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = colors.surface,
            border = BorderStroke(width = 1.dp, color = colors.outline),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location field
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .clickable { isLocationDropdownOpen = true }
                    ) {
                        Text(
                            text = selectedLocation ?: "Location",
                            style = typography.cardTitle,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Location dropdown menu
                    LocationDropdownMenu(
                        expanded = isLocationDropdownOpen,
                        onDismissRequest = {
                            isLocationDropdownOpen = false
                            locationSearchQuery = ""
                        },
                        searchQuery = locationSearchQuery,
                        onSearchQueryChange = { locationSearchQuery = it },
                        onLocationSelected = { location ->
                            selectedLocation = location
                            isLocationDropdownOpen = false
                            locationSearchQuery = ""
                        }
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(16.dp),
                    color = colors.outline
                )

                // Date field
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .clickable { isDateDropdownOpen = true }
                    ) {
                        Text(
                            text = if (selectedDate != null) {
                                formatDate(selectedDate)
                            } else {
                                "Date"
                            },
                            style = typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Date dropdown menu
                    DateDropdownMenu(
                        expanded = isDateDropdownOpen,
                        onDismissRequest = { isDateDropdownOpen = false },
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            selectedDate = date
                            isDateDropdownOpen = false
                        }
                    )
                }

                VerticalDivider(
                    modifier = Modifier.height(16.dp),
                    color = colors.outline
                )

                // Guests field
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = guestsNumber,
                                onValueChange = { newValue ->
                                    // Only allow digits
                                    if (newValue.all { it.isDigit() }) {
                                        guestsNumber = newValue
                                    }
                                },
                                modifier = Modifier
//                                    .wrapContentWidth(),
                                    .width(24.dp),
                                textStyle = typography.bodyMedium.copy(color = colors.textPrimary),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
//                                decorationBox = { innerTextField ->
//                                    Box {
//                                        if (guestsNumber.isEmpty()) {
//                                            Text(
//                                                text = "Guests",
//                                                style = typography.bodyMedium,
//                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
//                                                maxLines = 1
//                                            )
//                                        } else {
//                                            innerTextField()
//                                        }
//                                    }
//                                },
                                singleLine = true
                            )
//                            if (guestsNumber.isNotEmpty()) {
// //                               Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (guestsNumber.isEmpty()) "Guests" else "guests",
                                    style = typography.bodyMedium,
                                    color = colors.textSecondary,
                                    maxLines = 1
                                )
//                            }
                        }
                    }
                }
            }
        }
        
        // Action buttons row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter button with active state
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isFilterModalOpen || selectedAllergens != null || selectedCuisine != null) {
                    colors.dinerPrimary
                } else {
                    colors.surface
                },
                onClick = { isFilterModalOpen = true },
                shadowElevation = 0.dp,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val filterIconPainter = rememberIconPainter("filter_icon")
                    Icon(
                        painter = filterIconPainter,
                        contentDescription = "Filter",
                        modifier = Modifier.size(14.dp),
                        tint = if (isFilterModalOpen || selectedAllergens != null || selectedCuisine != null) {
                            colors.textOnPrimary
                        } else {
                            colors.textPrimary
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Filter",
                        style = typography.bodySmall.copy(fontSize = 12.sp),
                        color = if (isFilterModalOpen || selectedAllergens != null || selectedCuisine != null) {
                            colors.textOnPrimary
                        } else {
                            colors.textPrimary
                        }
                    )
                }
            }

            ActionButton(
                text = "Sort",
                iconPainter = rememberIconPainter("sort_icon"),
                modifier = Modifier.weight(1f)
            )
            ActionButton(
                text = "Search text",
                icon = Icons.Default.Search,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Filter modal
        if (isFilterModalOpen) {
            FilterModal(
                onDismiss = { isFilterModalOpen = false },
                onConfirm = {
                    isFilterModalOpen = false
                    // Filter logic can be added here
                },
                selectedAllergens = selectedAllergens,
                onAllergensSelected = { selectedAllergens = it },
                selectedCuisine = selectedCuisine,
                onCuisineSelected = { selectedCuisine = it }
            )
        }
        
        // Chef results list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(chefs) { chef ->
                ChefResultCard(
                    chef = chef,
                    onButtonClick = {
                        onChefClick(chef.name)
                    }
                )
            }
        }
    }
}


/**
 * Filter modal dialog component
 */
@Composable
fun FilterModal(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    selectedAllergens: String?,
    onAllergensSelected: (String?) -> Unit,
    selectedCuisine: String?,
    onCuisineSelected: (String?) -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = "Filters",
                    style = typography.sectionTitle
                )
                
                // Allergens field
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surface,
                    onClick = {
                        // Placeholder for allergens selection
                        onAllergensSelected("nuts")
                    },
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedAllergens?.let { "Allergens: $it" } ?: "Allergens (optional) — e.g. nuts, dairy",
                            style = typography.bodyMedium,
                            color = if (selectedAllergens != null) {
                                colors.textSecondary
                            } else {
                                colors.textSecondary.copy(alpha = 0.6f)
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Cuisine field
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.surface,
                    onClick = {
                        // Placeholder for cuisine selection
                        onCuisineSelected("Japanese Fusion")
                    },
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCuisine?.let { "Cuisine: $it" } ?: "Cuisine (optional) — e.g. Japanese",
                            style = typography.bodyMedium,
                            color = if (selectedCuisine != null) {
                                colors.textSecondary
                            } else {
                                colors.textSecondary.copy(alpha = 0.6f)
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Confirm button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.End),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.dinerPrimary
                    )
                ) {
                    Text(
                        text = "Confirm",
                        style = typography.buttonText,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

