package nl.tue.hci.feature.diner.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
fun CuisineSelectionModal(
    onDismiss: () -> Unit,
    selectedCuisine: String?,
    onCuisineSelected: (String?) -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    val cuisines = listOf(
        "Italian", "Japanese", "Chinese", "Indian", "French", 
        "Thai", "Mexican", "Mediterranean", "Spanish", "Korean",
        "Vietnamese", "Turkish", "Greek", "Portuguese", "Japanese Fusion"
    )
    
    var localSelection by rememberSaveable { mutableStateOf(selectedCuisine) }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Text(
                    text = "Select Cuisine",
                    style = typography.sectionTitle
                )
                
                // Cuisine items
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cuisines) { cuisine ->
                        val shape = RoundedCornerShape(20.dp)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple()
                                ) {
                                    localSelection = if (localSelection == cuisine) null else cuisine
                                },
                            shape = shape,
                            color = if (localSelection == cuisine) {
                                colors.dinerPrimary.copy(alpha = 0.15f)
                            } else {
                                colors.surface
                            },
                            shadowElevation = 0.dp,
                            tonalElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = localSelection == cuisine,
                                    onClick = {
                                        localSelection = if (localSelection == cuisine) null else cuisine
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = colors.dinerPrimary,
                                        unselectedColor = colors.textSecondary
                                    )
                                )
                                Text(
                                    text = cuisine,
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Buttons row
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
//                        border = BorderStroke(1.dp, colors.dinerPrimary),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.surfaceVariant,
                            contentColor = colors.textPrimary
                        ),
                    ) {
                        Text(
                            text = "Cancel",
                            style = typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Button(
                        onClick = {
                            onCuisineSelected(localSelection)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.dinerPrimary,
                            contentColor = colors.textPrimary
                        )
                    ) {
                        Text(
                            text = "Done",
                            style = typography.buttonText,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
