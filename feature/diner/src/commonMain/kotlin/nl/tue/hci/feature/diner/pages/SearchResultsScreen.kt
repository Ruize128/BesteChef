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
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import nl.tue.hci.feature.diner.components.ActionButton
import nl.tue.hci.feature.diner.ChefResult
import nl.tue.hci.feature.diner.components.ChefResultCard
import nl.tue.hci.feature.diner.components.DateDropdownMenu
import nl.tue.hci.feature.diner.components.FilterModal
import nl.tue.hci.feature.diner.components.LocationDropdownMenu
import nl.tue.hci.feature.diner.components.formatDate
import nl.tue.hci.core.ui.components.ImagePreviewOverlay
import nl.tue.hci.core.ui.PlatformBackHandler
import nl.tue.hci.feature.diner.components.AllergensSelectionModal
import nl.tue.hci.feature.diner.components.CuisineSelectionModal
import nl.tue.hci.core.ui.icons.rememberIconPainter
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.delay

// Saver for LocalDate to make it work with rememberSaveable
private val LocalDateSaver = Saver<LocalDate?, String>(
    save = { it?.toString() ?: "" },
    restore = { if (it.isEmpty()) null else LocalDate.parse(it) }
)





@Composable
fun SearchResultsScreen(
    modifier: Modifier = Modifier,
    initialLocation: String? = null,
    initialDate: kotlinx.datetime.LocalDate? = null,
    initialGuests: String? = null,
    initialAllergens: Set<String> = emptySet(),
    initialCuisine: String? = null,
    onBackClick: () -> Unit = {},
    onChefClick: (String) -> Unit = {}, // Callback when chef is selected to show menu
    onChatClick: (String) -> Unit = {} // Callback to navigate to chat section
) {
    // Delegate navigation handling to parent; show only search results content here
    SearchResultsContent(
        modifier = modifier,
        initialLocation = initialLocation,
        initialDate = initialDate,
        initialGuests = initialGuests,
        initialAllergens = initialAllergens,
        initialCuisine = initialCuisine,
        onBackClick = onBackClick,
        onChefClick = onChefClick,
        onChatClick = onChatClick
    )
}

@Composable
private fun SearchResultsContent(
    modifier: Modifier = Modifier,
    initialLocation: String? = null,
    initialDate: kotlinx.datetime.LocalDate? = null,
    initialGuests: String? = null,
    initialAllergens: Set<String> = emptySet(),
    initialCuisine: String? = null,
    onBackClick: () -> Unit = {},
    onChefClick: (String) -> Unit = {},
    onChatClick: (String) -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Search parameters state
    var selectedLocation by rememberSaveable { mutableStateOf<String?>(initialLocation ?: "Eindhoven") }
    var isLocationDropdownOpen by rememberSaveable { mutableStateOf(false) }
    var locationSearchQuery by rememberSaveable { mutableStateOf("") }
    
    var selectedDate by rememberSaveable(stateSaver = LocalDateSaver) { 
        mutableStateOf<LocalDate?>(initialDate ?: Clock.System.todayIn(TimeZone.currentSystemDefault())) 
    }
    var isDateDropdownOpen by rememberSaveable { mutableStateOf(false) }
    
    var guestsNumber by rememberSaveable { mutableStateOf(initialGuests ?: "6") }
    
    var selectedAllergens by rememberSaveable { mutableStateOf<Set<String>>(initialAllergens) }
    var selectedCuisine by rememberSaveable { mutableStateOf<String?>(initialCuisine) }
    
    // Filter modal state
    var isFilterModalOpen by rememberSaveable { mutableStateOf(false) }
    // Sort menu state
    var isSortMenuOpen by rememberSaveable { mutableStateOf(false) }
    var selectedSortOption by rememberSaveable { mutableStateOf("Relevance") }
    
    // Sub-modals for filter selections
    var isAllergensSelectionOpen by rememberSaveable { mutableStateOf(false) }
    var isCuisineSelectionOpen by rememberSaveable { mutableStateOf(false) }
    
    // Full-screen image preview state
    var showImagePreview by rememberSaveable { mutableStateOf(false) }
    var previewImageName by rememberSaveable { mutableStateOf<String?>(null) }
    
    // Loading states:
    // - isInitialLoading: when navigating from SearchScreen -> SearchResults (list hidden)
    // - isTransientLoading: when user edits search fields or filters (list visible, show faded overlay)
    var isInitialLoading by remember { mutableStateOf(true) }
    var isTransientLoading by remember { mutableStateOf(false) }

    // Initial navigation loading (incoming params changed)
    LaunchedEffect(initialLocation, initialDate, initialGuests) {
        isInitialLoading = true
        delay(500)
        isInitialLoading = false
    }

    // Transient loading when user updates any in-page search/filter fields
    LaunchedEffect(selectedLocation, selectedDate, guestsNumber, selectedAllergens, selectedCuisine) {
        // If initial loading is active, skip transient to avoid overlap
        if (!isInitialLoading) {
            isTransientLoading = true
            delay(500)
            isTransientLoading = false
        }
    }

    // Trigger mock refresh when sort option changes
    LaunchedEffect(selectedSortOption) {
        if (!isInitialLoading) {
            isTransientLoading = true
            delay(400)
            isTransientLoading = false
        }
    }

    val chefs = listOf(
            ChefResult(
                name = "Chef Ichiraku",
                rating = 5.0f,
                reviewCount = 7,
                eventCount = 13,
                canTravel = true,
                availableOnDate = true,
                quote = "Enhancing classic flavors with a touch of style",
                imageColor = colors.imagePlaceholder1, // Light mint green
                avatarImageName = "ichiraku"
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
                    onClick = {
                        if (showImagePreview) {
                            showImagePreview = false
                            previewImageName = null
                        } else {
                            onBackClick()
                        }
                    },
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
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filter button with active state
            Surface(
                modifier = Modifier
                    .height(24.dp)
                    .wrapContentWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isFilterModalOpen || selectedAllergens.isNotEmpty() || selectedCuisine != null) {
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
                        .wrapContentWidth()
                        .padding(horizontal = 24.dp, vertical = 0.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val filterIconPainter = rememberIconPainter("filter_icon")
                    Icon(
                        painter = filterIconPainter,
                        contentDescription = "Filter",
                        modifier = Modifier.size(14.dp),
                        tint = if (isFilterModalOpen || selectedAllergens.isNotEmpty() || selectedCuisine != null) {
                            colors.textOnPrimary
                        } else {
                            colors.textPrimary
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Filter",
                        style = typography.bodySmall.copy(fontSize = 12.sp),
                        color = if (isFilterModalOpen || selectedAllergens.isNotEmpty() || selectedCuisine != null) {
                            colors.textOnPrimary
                        } else {
                            colors.textPrimary
                        }
                    )
                }
            }

            // Sort button with dropdown
            Box(modifier = Modifier.wrapContentWidth()) {
                Surface(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(24.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedSortOption.contains("Price") || selectedSortOption.contains("Rating")) colors.dinerPrimary else colors.surface,
                    onClick = { isSortMenuOpen = !isSortMenuOpen },
                    shadowElevation = 0.dp,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(horizontal = 24.dp, vertical = 0.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sortIconPainter = rememberIconPainter("sort_icon")
                        Icon(
                            painter = sortIconPainter,
                            contentDescription = "Sort",
                            modifier = Modifier.size(14.dp),
                            tint = if (selectedSortOption.contains("Price") || selectedSortOption.contains("Rating")) colors.textOnPrimary else colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedSortOption,
                            style = typography.bodySmall.copy(fontSize = 12.sp),
                            color = if (selectedSortOption.contains("Price") || selectedSortOption.contains("Rating")) colors.textOnPrimary else colors.textPrimary
                        )
                    }
                }

                DropdownMenu(
                expanded = isSortMenuOpen,
                onDismissRequest = { isSortMenuOpen = false },
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surface),
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 0.dp,
                border = BorderStroke(1.dp, colors.outline)
            ) {
                    val sortOptions = listOf(
                        "Relevance",
                        "Rating (High → Low)",
                        "Price (Low → High)",
                        "Price (High → Low)"
                    )
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = colors.textPrimary) },
                            onClick = {
                                selectedSortOption = option
                                isSortMenuOpen = false
                            }
                        )
                    }
                }
            }
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
                onOpenAllergensSelection = { isAllergensSelectionOpen = true },
                selectedCuisine = selectedCuisine,
                onCuisineSelected = { selectedCuisine = it },
                onOpenCuisineSelection = { isCuisineSelectionOpen = true }
            )
        }
        
        // Allergens selection modal
        if (isAllergensSelectionOpen) {
            AllergensSelectionModal(
                onDismiss = { isAllergensSelectionOpen = false },
                selectedAllergens = selectedAllergens,
                onAllergensSelected = { selectedAllergens = it }
            )
        }
        
        // Cuisine selection modal
        if (isCuisineSelectionOpen) {
            CuisineSelectionModal(
                onDismiss = { isCuisineSelectionOpen = false },
                selectedCuisine = selectedCuisine,
                onCuisineSelected = { selectedCuisine = it }
            )
        }
        
        // Chef results list (initial load hides list; transient edits show faded overlay)
        Box(modifier = Modifier.fillMaxSize()) {
            if (isInitialLoading) {
                // Hide list completely and show spinner
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.dinerPrimary)
                }
            } else {
                // Render list
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
                            },
                            onImageClick = { imageName ->
                                previewImageName = imageName
                                showImagePreview = true
                            }
                        )
                    }
                }

                // If transient loading (user edits), show faded overlay with spinner
                if (isTransientLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.dinerPrimary)
                    }
                }
            }
        }
    }

    // Full-screen image preview overlay
    PlatformBackHandler(enabled = showImagePreview) {
        showImagePreview = false
        previewImageName = null
    }
    ImagePreviewOverlay(
        showPreview = showImagePreview,
        imageName = previewImageName,
        onDismiss = {
            showImagePreview = false
            previewImageName = null
        }
    )
}


/**
 * Filter modal dialog component
 */

