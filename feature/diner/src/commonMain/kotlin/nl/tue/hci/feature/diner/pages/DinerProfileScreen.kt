package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.Avatar


@Composable
fun DinerProfileScreen(
    modifier: Modifier = Modifier,
    onNavigateToOrders: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()

    var serviceAddress by remember { mutableStateOf("Keizersgracht 123, 1015 CJ Amsterdam") }
    var showAddressEditDialog by remember { mutableStateOf(false) }
    var tempAddress by remember { mutableStateOf(serviceAddress) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        
        // Avatar
        Avatar(
            text = "S",
            size = 120,
            backgroundColor = colors.dinerPrimary,
            textColor = colors.textPrimary,
            imageName = "sophie" // Ichiraku restaurant avatar
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // User Name
        Text(
            text = "Sophie",
            style = typography.sectionTitle,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Service Address section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = "My Address",
                style = typography.labelMedium,
                color = colors.textSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = serviceAddress,
                    style = typography.bodyLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "Edit",
                    style = typography.labelMedium,
                    color = colors.dinerPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { 
                            tempAddress = serviceAddress
                            showAddressEditDialog = true 
                        }
                        .padding(start = 16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 48.dp)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.dinerPrimary,
                contentColor = colors.textPrimary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Logout",
                style = typography.buttonText
            )
        }
    }
    
    // Address Edit Dialog
    if (showAddressEditDialog) {
        AlertDialog(
            onDismissRequest = { showAddressEditDialog = false },
            title = {
                Text(
                    text = "Edit Service Address",
                    style = typography.cardTitle,
                    color = colors.textPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = tempAddress,
                    onValueChange = { tempAddress = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedBorderColor = colors.dinerPrimary,
                        unfocusedBorderColor = colors.outline,
                    ),
                    placeholder = {
                        Text(
                            text = "Enter service address",
                            color = colors.textSecondary
                        )
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        serviceAddress = tempAddress
                        showAddressEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.dinerPrimary
                    )
                ) {
                    Text(
                        text = "Save",
                        color = colors.textPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        tempAddress = serviceAddress
                        showAddressEditDialog = false
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = colors.dinerPrimary
                    )
                }
            },
            containerColor = colors.surface,
            titleContentColor = colors.textPrimary,
            textContentColor = colors.textPrimary
        )
    }
}

