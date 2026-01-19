package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.feature.diner.components.DateDropdownMenu
import nl.tue.hci.feature.diner.components.LocationDropdownMenu
import nl.tue.hci.feature.diner.components.formatDate
import nl.tue.hci.feature.diner.components.AllergensSelectionModal
import nl.tue.hci.feature.diner.components.CuisineSelectionModal

// Saver for LocalDate to make it work with rememberSaveable
private val LocalDateSaver = Saver<LocalDate?, String>(
    save = { it?.toString() ?: "" },
    restore = { if (it.isEmpty()) null else LocalDate.parse(it) }
)


@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onSearchClick: (selectedLocation: String?, selectedDate: LocalDate?, guests: String, selectedAllergens: Set<String>, selectedCuisine: String?) -> Unit = { _, _, _, _, _ -> }
) {
    // Static data for form fields
    val locationPlaceholder = "Where?"
    val datePlaceholder = "When?"
    val guestsPlaceholder = "Who?"
    val allergensPlaceholder = "Allergens (optional) — e.g. nuts, dairy"
    val cuisinePlaceholder = "Cuisine (optional) — e.g. Japanese"
    
    // Location state
    var selectedLocation by rememberSaveable { mutableStateOf<String?>("Amsterdam") }
    var isLocationDropdownOpen by rememberSaveable { mutableStateOf(false) }
    var locationSearchQuery by rememberSaveable { mutableStateOf("") }
    
    // Date state (default to today)
    var selectedDate by rememberSaveable(stateSaver = LocalDateSaver) { 
        mutableStateOf<LocalDate?>(Clock.System.todayIn(TimeZone.currentSystemDefault())) 
    }
    var isDateDropdownOpen by rememberSaveable { mutableStateOf(false) }
    
    // Guests state (default 6)
    var guestsNumber by rememberSaveable { mutableStateOf("6") }
    
    // Allergens state
    var selectedAllergens by rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }
    var isAllergensSelectionOpen by rememberSaveable { mutableStateOf(false) }
    
    // Cuisine state
    var selectedCuisine by rememberSaveable { mutableStateOf<String?>(null) }
    var isCuisineSelectionOpen by rememberSaveable { mutableStateOf(false) }

    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Headline
        Text(
            text = "Find a private chef for\nany occasion",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 40.dp)
        )
        
        // Primary search container
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location field
                Box(
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { isLocationDropdownOpen = true }
                    ) {
                        Text(
                            text = "Location",
                            style = typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Text(
                            text = selectedLocation ?: locationPlaceholder,
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedLocation != null) {
                                colors.textPrimary
                            } else {
                                colors.textSecondary.copy(alpha = 0.6f)
                            },
                            modifier = Modifier.padding(top = 4.dp),
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
                    modifier = Modifier.height(40.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                
                // Date field
                Box(
                    modifier = Modifier.weight(1.2f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { isDateDropdownOpen = true }
                    ) {
                        Text(
                            text = "Date",
                            style = typography.labelSmall,
                            color = colors.textSecondary
                        )
                        Text(
                            text = if (selectedDate != null) {
                                formatDate(selectedDate)
                            } else {
                                datePlaceholder
                            },
                            style = typography.bodyMedium,
                            color = if (selectedDate != null) {
                                colors.textPrimary
                            } else {
                                colors.textSecondary.copy(alpha = 0.6f)
                            },
                            modifier = Modifier.padding(top = 4.dp)
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
                    modifier = Modifier.height(40.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                
                // Guests field
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Guests",
                        style = typography.labelSmall,
                        color = colors.textSecondary
                    )
                    // Invisible TextField that looks like the other fields
                    BasicTextField(
                        value = guestsNumber,
                        onValueChange = { newValue ->
                            // Only allow digits
                            if (newValue.all { it.isDigit() }) {
                                guestsNumber = newValue
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textStyle = typography.bodyMedium.copy(
                            color = if (guestsNumber.isNotEmpty()) {
                                colors.textPrimary
                            } else {
                                colors.textSecondary.copy(alpha = 0.6f)
                            }
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (guestsNumber.isEmpty()) {
                                    Text(
                                        text = guestsPlaceholder,
                                        style = typography.bodyMedium,
                                        color = colors.textSecondary.copy(alpha = 0.6f)
                                    )
                                }
                                innerTextField()
                            }
                        },
                        singleLine = true
                    )
                }
                
                // Search button
                Button(
                    onClick = {
                        onSearchClick(selectedLocation, selectedDate, guestsNumber, selectedAllergens, selectedCuisine)
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.dinerPrimary,
                        contentColor = colors.textOnPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = colors.textOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // remove it here, since we can not apply "Allergens" on chef search result
//        // Allergens field
//        val allergenShape = RoundedCornerShape(16.dp)
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(allergenShape)
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = ripple()
//                ) { isAllergensSelectionOpen = true },
//            shape = allergenShape,
//            color = colors.surface,
//            shadowElevation = 0.dp,
//            tonalElevation = 0.dp
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    if (selectedAllergens.isNotEmpty()) {
//                        Text(
//                            text = selectedAllergens.joinToString(", "),
//                            style = typography.bodyMedium,
//                            color = colors.textPrimary
//                        )
//                    } else {
//                        Text(
//                            text = allergensPlaceholder,
//                            style = typography.bodyMedium,
//                            color = colors.textSecondary.copy(alpha = 0.6f)
//                        )
//                    }
//                }
//                Icon(
//                    imageVector = Icons.Default.KeyboardArrowDown,
//                    contentDescription = "Dropdown",
//                    tint = colors.textSecondary,
//                    modifier = Modifier.size(20.dp)
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
        
        // Cuisine field
        val cuisineShape = RoundedCornerShape(16.dp)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cuisineShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple()
                ) { isCuisineSelectionOpen = true },
            shape = cuisineShape,
            color = colors.surface,
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
                Column(modifier = Modifier.weight(1f)) {
                    val cuisine = selectedCuisine
                    if (cuisine != null) {
                        Text(
                            text = cuisine,
                            style = typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    } else {
                        Text(
                            text = cuisinePlaceholder,
                            style = typography.bodyMedium,
                            color = colors.textSecondary.copy(alpha = 0.6f)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Allergens selection modal
        if (isAllergensSelectionOpen) {
            AllergensSelectionModal(
                onDismiss = { isAllergensSelectionOpen = false },
                selectedAllergens = selectedAllergens,
                onAllergensSelected = { allergens ->
                    selectedAllergens = allergens
                }
            )
        }
        
        // Cuisine selection modal
        if (isCuisineSelectionOpen) {
            CuisineSelectionModal(
                onDismiss = { isCuisineSelectionOpen = false },
                selectedCuisine = selectedCuisine,
                onCuisineSelected = { cuisine ->
                    selectedCuisine = cuisine
                }
            )
        }
    }
}

