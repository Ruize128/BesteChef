package nl.tue.hci.feature.diner.components

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
                        style = typography.sectionTitle,
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

                        Slider(
                            value = selectedDistance,
                            onValueChange = { onDistanceChange(it) },
                            valueRange = 1f..100f,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = colors.dinerPrimary,
                                activeTrackColor = colors.dinerPrimary,
                                inactiveTrackColor = colors.surfaceVariant
                            )
                        )
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
