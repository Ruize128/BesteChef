package nl.tue.hci.feature.diner.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography

@Composable
fun FilterModal(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    selectedAllergens: Set<String>,
    onAllergensSelected: (Set<String>) -> Unit,
    onOpenAllergensSelection: () -> Unit,
    selectedCuisine: String?,
    onCuisineSelected: (String?) -> Unit,
    onOpenCuisineSelection: () -> Unit
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
                    style = typography.titleLarge
                )
                
                // Allergens field
                val allergenShape = RoundedCornerShape(12.dp)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(allergenShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) { onOpenAllergensSelection() },
                    shape = allergenShape,
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
                                text = "Allergens (optional)",
                                style = typography.bodyMedium,
                                color = colors.textSecondary,
                                fontSize = 12.sp
                            )
                            if (selectedAllergens.isNotEmpty()) {
                                Text(
                                    text = selectedAllergens.joinToString(", "),
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary,
                                    fontSize = 14.sp
                                )
                            } else {
                                Text(
                                    text = "Select allergens to avoid...",
                                    style = typography.bodyMedium,
                                    color = colors.textSecondary.copy(alpha = 0.6f),
                                    fontSize = 14.sp
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
                
                // Cuisine field
                val cuisineShape = RoundedCornerShape(12.dp)
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
                            if (selectedCuisine != null) {
                                Text(
                                    text = selectedCuisine,
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary,
                                    fontSize = 14.sp
                                )
                            } else {
                                Text(
                                    text = "Select a cuisine...",
                                    style = typography.bodyMedium,
                                    color = colors.textSecondary.copy(alpha = 0.6f),
                                    fontSize = 14.sp
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
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Confirm button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.dinerPrimary
                    )
                ) {
                    Text(
                        text = "Confirm",
                        style = typography.labelLarge
                    )
                }
            }
        }
    }
}
