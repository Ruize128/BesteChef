package nl.tue.hci.feature.diner.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import kotlin.math.roundToInt

// Non-uniform distance values for better granularity at lower distances
private val distanceValues = listOf(1f, 2f, 3f, 5f, 7f, 10f, 15f, 20f, 30f, 50f, 75f, 100f)

// Convert slider position (0-100) to distance value
private fun sliderPositionToDistance(position: Float): Float {
    val index = (position / 100f * (distanceValues.size - 1)).roundToInt().coerceIn(0, distanceValues.size - 1)
    return distanceValues[index]
}

// Convert distance value to slider position (0-100)
private fun distanceToSliderPosition(distance: Float): Float {
    val index = distanceValues.indexOfFirst { it >= distance }.takeIf { it >= 0 } ?: (distanceValues.size - 1)
    return (index.toFloat() / (distanceValues.size - 1)) * 100f
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortFilterModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    selectedSortOption: String,
    onSortOptionChange: (String) -> Unit,
    selectedDistance: Float,
    onDistanceChange: (Float) -> Unit,
    selectedAllergens: Set<String>,
    onAllergensSelected: (Set<String>) -> Unit,
    onOpenAllergensSelection: () -> Unit,
    selectedCuisine: String?,
    onCuisineSelected: (String?) -> Unit,
    onOpenCuisineSelection: () -> Unit,
    selectedMinRating: Float = 0f,
    onMinRatingChange: (Float) -> Unit = {},
    selectedMinPrice: Float = 0f,
    selectedMaxPrice: Float = 100f,
    onPriceRangeChange: (Float, Float) -> Unit = { _, _ -> }
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()

    // Temporary state for selections before confirming
    var tempSortOption by remember { mutableStateOf(selectedSortOption) }
    var tempDistance by remember { mutableStateOf(selectedDistance) }
    var tempAllergens by remember { mutableStateOf(selectedAllergens) }
    var tempCuisine by remember { mutableStateOf(selectedCuisine) }

    // Update temp state when modal opens
    LaunchedEffect(isOpen) {
        if (isOpen) {
            tempSortOption = selectedSortOption
            tempDistance = selectedDistance
            tempAllergens = selectedAllergens
            tempCuisine = selectedCuisine
        }
    }

    if (isOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState,
            containerColor = colors.surfaceContainer,
            tonalElevation = 4.dp
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Text(
                        text = "Filter & Sort",
                        style = typography.titleLarge,
                        color = colors.textPrimary
                    )
                }

                // remove it here, since we can not apply "Allergens" on chef search result
//                // Allergens filter section
//                item {
//                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                        val allergenShape = RoundedCornerShape(20.dp)
//                        Surface(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clip(allergenShape)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = ripple()
//                                ) { onOpenAllergensSelection() },
//                            shape = allergenShape,
//                            color = colors.surface,
//                            shadowElevation = 0.dp,
//                            tonalElevation = 0.dp
//                        ) {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Column(modifier = Modifier.weight(1f)) {
//                                    Text(
//                                        text = "Allergens (optional)",
//                                        style = typography.bodyMedium,
//                                        color = colors.textSecondary,
//                                        fontSize = 12.sp
//                                    )
//                                    if (tempAllergens.isNotEmpty()) {
//                                        Text(
//                                            text = tempAllergens.joinToString(", "),
//                                            style = typography.bodyMedium,
//                                            color = colors.textPrimary,
//                                            fontSize = 14.sp
//                                        )
//                                    } else {
//                                        Text(
//                                            text = "Select allergens to avoid...",
//                                            style = typography.bodyMedium,
//                                            color = colors.textSecondary,
//                                            fontSize = 14.sp
//                                        )
//                                    }
//                                }
//                                Icon(
//                                    imageVector = Icons.Default.KeyboardArrowDown,
//                                    contentDescription = null,
//                                    modifier = Modifier.size(20.dp),
//                                    tint = colors.textSecondary
//                                )
//                            }
//                        }
//                    }
//                }

                // Cuisine filter section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        val cuisineShape = RoundedCornerShape(20.dp)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(cuisineShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple()
                                ) { onOpenCuisineSelection() },
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
                                    Text(
                                        text = "Cuisine (optional)",
                                        style = typography.bodyMedium,
                                        color = colors.textSecondary,
                                        fontSize = 12.sp
                                    )
                                    val cuisineValue = tempCuisine
                                    if (cuisineValue != null) {
                                        Text(
                                            text = cuisineValue,
                                            style = typography.bodyMedium,
                                            color = colors.textPrimary,
                                            fontSize = 14.sp
                                        )
                                    } else {
                                        Text(
                                            text = "Select preferred cuisine...",
                                            style = typography.bodyMedium,
                                            color = colors.textSecondary,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = colors.textSecondary
                                )
                            }
                        }
                    }
                }

                // Divider
                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = colors.outline,
                        thickness = 1.dp
                    )
                }

                // Sort options section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Sort by",
                            style = typography.bodySmall,
                            color = colors.textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        
                        // Sort buttons in a row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Relevant option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(shape = RoundedCornerShape(20.dp))
                                    .clickable { tempSortOption = "Relevance" },
                                color = if (tempSortOption == "Relevance") colors.dinerPrimary else colors.surface,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (tempSortOption == "Relevance") colors.dinerPrimary else colors.outline
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Relevant",
                                        style = typography.bodySmall,
                                        color = if (tempSortOption == "Relevance") colors.textOnPrimary else colors.textPrimary
                                    )
                                }
                            }

                            // Rating option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(shape = RoundedCornerShape(20.dp))
                                    .clickable { tempSortOption = "Rating" },
                                color = if (tempSortOption == "Rating") colors.dinerPrimary else colors.surface,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (tempSortOption == "Rating") colors.dinerPrimary else colors.outline
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rating",
                                        style = typography.bodySmall,
                                        color = if (tempSortOption == "Rating") colors.textOnPrimary else colors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Distance filter section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Distance",
                                style = typography.bodySmall,
                                color = colors.textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = when {
                                    tempDistance <= 1f -> "< 1km"
                                    tempDistance >= 100f -> "> 100km"
                                    else -> "Within ${tempDistance.toInt()} km"
                                },
                                style = typography.bodySmall,
                                color = colors.dinerPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        // Distance slider with tick marks
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Slider(
                                value = distanceToSliderPosition(tempDistance),
                                onValueChange = { sliderPos ->
                                    val distance = sliderPositionToDistance(sliderPos)
                                    tempDistance = distance
                                },
                                valueRange = 0f..100f,
                                steps = distanceValues.size - 2, // Number of steps between min and max
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = colors.dinerPrimary,
                                    activeTrackColor = colors.dinerPrimary,
                                    inactiveTrackColor = colors.surfaceVariant
                                )
                            )
                            
                            // Tick marks
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                distanceValues.forEach { distance ->
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(8.dp)
                                            .background(
                                                if (tempDistance >= distance) colors.dinerPrimary
                                                else colors.surfaceVariant
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                // Cancel and Confirm buttons
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Cancel button
                        OutlinedButton(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                style = typography.labelLarge,
                                color = colors.textPrimary,
                            )
                        }

                        // Confirm button
                        Button(
                            onClick = {
                                // Apply all changes
                                onSortOptionChange(tempSortOption)
                                onDistanceChange(tempDistance)
                                onAllergensSelected(tempAllergens)
                                onCuisineSelected(tempCuisine)
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.dinerPrimary,
                                contentColor = colors.textOnPrimary,
                            )
                        ) {
                            Text(
                                text = "Confirm",
                                style = typography.labelLarge,
                            )
                        }
                    }
                }

                // Spacer at bottom
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
