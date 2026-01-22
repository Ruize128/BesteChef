package nl.tue.hci.feature.diner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import kotlin.math.roundToInt

// Non-uniform distance values: small increments at start, larger at end
private val distanceValues = listOf(
    1f, 2f, 3f, 5f, 7f, 10f, 15f, 20f, 30f, 50f, 75f, 100f
)

// Convert slider position (0-100) to distance value
private fun sliderPositionToDistance(position: Float): Float {
    if (position <= 0f) return distanceValues.first()
    if (position >= 100f) return distanceValues.last()
    
    val index = ((position / 100f) * (distanceValues.size - 1)).roundToInt()
    return distanceValues[index.coerceIn(0, distanceValues.size - 1)]
}

// Convert distance value to slider position (0-100)
private fun distanceToSliderPosition(distance: Float): Float {
    val index = distanceValues.indexOfFirst { it >= distance }
    if (index == -1) return 100f
    if (index == 0) return 0f
    
    val prevValue = distanceValues[index - 1]
    val nextValue = distanceValues[index]
    val ratio = (distance - prevValue) / (nextValue - prevValue)
    
    val prevPosition = (index - 1).toFloat() / (distanceValues.size - 1) * 100f
    val nextPosition = index.toFloat() / (distanceValues.size - 1) * 100f
    
    return prevPosition + (nextPosition - prevPosition) * ratio
}

@Composable
fun SortModal(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    selectedSortOption: String,
    onSortOptionChange: (String) -> Unit,
    selectedDistance: Float,
    onDistanceChange: (Float) -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()

    if (isOpen) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(24.dp)),
                color = colors.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Header
                    Text(
                        text = "Sort",
                        style = typography.titleLarge,
                        color = colors.textPrimary
                    )

                    // Sort options
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Sort by",
                            style = typography.bodyMedium,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Medium
                        )

                        val sortOptions = listOf(
                            "Relevance",
                            "Rating (High → Low)"
                        )

                        sortOptions.forEach { option ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clickable {
                                        onSortOptionChange(option)
                                    },
                                color = if (selectedSortOption == option) colors.dinerPrimary else colors.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option,
                                        style = typography.bodyMedium,
                                        color = if (selectedSortOption == option) colors.textOnPrimary else colors.textPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Distance filter
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Distance",
                                style = typography.bodyMedium,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = when {
                                    selectedDistance <= 1f -> "< 1km"
                                    selectedDistance >= 100f -> "> 100km"
                                    else -> "Within ${selectedDistance.toInt()} km"
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
                                value = distanceToSliderPosition(selectedDistance),
                                onValueChange = { sliderPos ->
                                    val distance = sliderPositionToDistance(sliderPos)
                                    onDistanceChange(distance)
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
                                                if (selectedDistance >= distance) colors.dinerPrimary
                                                else colors.surfaceVariant
                                            )
                                    )
                                }
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable { onDismiss() },
                            color = colors.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Cancel",
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable { onDismiss() },
                            color = colors.dinerPrimary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Apply",
                                    style = typography.bodyMedium,
                                    color = colors.textOnPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
