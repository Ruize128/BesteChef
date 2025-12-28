package nl.tue.hci.feature.chef.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.AppColors
import nl.tue.hci.core.ui.components.FilterButton
import nl.tue.hci.core.ui.components.Tag
import nl.tue.hci.feature.chef.model.MenuPickerItem
import nl.tue.hci.feature.chef.model.SelectedMenuItem

@Composable
fun MenuPickerScreen(
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
    onItemSelected: (List<SelectedMenuItem>) -> Unit = {}
) {
    // Hardcoded menu items
    val allMenuItems = remember {
        listOf(
            MenuPickerItem(
                id = "1",
                title = "Yuzu mousse",
                description = "light citrus dessert.",
                price = "€8",
                imageColor = Color(0xFFB2E5D4), // Light green
                dietaryTag = "Nut-free",
                dietaryTagColor = AppColors.ChefPrimary,
                category = "Desserts"
            ),
            MenuPickerItem(
                id = "2",
                title = "Seared seabass",
                description = "miso glaze, seasonal veg.",
                price = "€14",
                imageColor = Color(0xFFFFD4B2), // Light orange-beige
                dietaryTag = "Fish",
                dietaryTagColor = Color(0xFFB3E5FC), // Light blue
                category = "Mains"
            ),
            MenuPickerItem(
                id = "3",
                title = "5-course Omakase",
                description = "chef's selection (per guest).",
                price = "€65",
                imageColor = Color(0xFFFFB3BA), // Light pink
                dietaryTag = "Contains nuts",
                dietaryTagColor = null, // Plain text, no tag
                category = "Mains"
            ),
            MenuPickerItem(
                id = "4",
                title = "Grilled Mackerel with Miso",
                description = "Sea salt, spring onion, yuzu dressing.",
                price = "€12",
                imageColor = Color(0xFFB2E5D4),
                dietaryTag = "Fish",
                dietaryTagColor = Color(0xFFB3E5FC),
                category = "Mains"
            ),
            MenuPickerItem(
                id = "5",
                title = "Wagyu Beef Steak",
                description = "Premium wagyu with truffle butter.",
                price = "€45",
                imageColor = Color(0xFFE8D5C4),
                dietaryTag = null,
                dietaryTagColor = null,
                category = "Mains"
            ),
            MenuPickerItem(
                id = "6",
                title = "Caesar Salad",
                description = "Fresh romaine, parmesan, croutons.",
                price = "€9",
                imageColor = Color(0xFFB2E5D4),
                dietaryTag = "Vegetarian",
                dietaryTagColor = Color(0xFFC8E6C9), // Light green
                category = "Starters"
            )
        )
    }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val selectedItems = remember { mutableStateListOf<SelectedMenuItem>() }
    
    // Filter menu items
    val filteredItems = allMenuItems.filter { item ->
        (selectedCategory == "All" || item.category == selectedCategory) &&
        (searchQuery.isEmpty() || item.title.contains(searchQuery, ignoreCase = true) ||
         item.description.contains(searchQuery, ignoreCase = true))
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Menu picker",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                
                TextButton(onClick = onClose) {
                    Text(
                        text = "Close",
                        color = AppColors.TextPrimary
                    )
                }
            }
        }
        
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = {
                Text(
                    text = "Search dishes or ingredients",
                    color = AppColors.TextSecondary
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AppColors.TextSecondary
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.OutlineLight,
                unfocusedBorderColor = AppColors.OutlineLight,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )
        
        // Category filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterButton(
                text = "All",
                isSelected = selectedCategory == "All",
                onClick = { selectedCategory = "All" }
            )
            FilterButton(
                text = "Starters",
                isSelected = selectedCategory == "Starters",
                onClick = { selectedCategory = "Starters" }
            )
            FilterButton(
                text = "Mains",
                isSelected = selectedCategory == "Mains",
                onClick = { selectedCategory = "Mains" }
            )
            FilterButton(
                text = "Desserts",
                isSelected = selectedCategory == "Desserts",
                onClick = { selectedCategory = "Desserts" }
            )
        }
        
        // Menu items list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredItems) { item ->
                MenuPickerItemCard(
                    item = item,
                    onAddClick = {
                        val existing = selectedItems.find { it.menuItem.id == item.id }
                        if (existing != null) {
                            selectedItems.remove(existing)
                            selectedItems.add(existing.copy(quantity = existing.quantity + 1))
                        } else {
                            selectedItems.add(SelectedMenuItem(menuItem = item, quantity = 1))
                        }
                    }
                )
            }
        }
        
        // Bottom bar with selected items summary
//        if (selectedItems.isNotEmpty()) {
            Button(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonColors(
                    containerColor = AppColors.ChefPrimary,
                    contentColor = AppColors.TextPrimary,
                    disabledContainerColor = AppColors.ChefSecondary,
                    disabledContentColor = AppColors.TextSecondary,
                ),
                shape = RoundedCornerShape(20.dp),
                enabled = selectedItems.isNotEmpty(),
                onClick = {
                    onItemSelected(selectedItems.toList())
                    onClose()
                },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Insert ${selectedItems.sumOf { it.quantity }} item${if (selectedItems.sumOf { it.quantity } > 1) "s" else ""}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic,
                        color = AppColors.TextPrimary,
                    )
                    Text(
                        text = "€${selectedItems.sumOf { 
                            val priceStr = it.menuItem.price.replace("€", "").replace(",", ".")
                            (priceStr.toDoubleOrNull()?.times(it.quantity) ?: 0.0).toInt()
                        }}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = AppColors.TextPrimary,
                    )
                }
            }
//        }
    }
}

@Composable
private fun MenuPickerItemCard(
    item: MenuPickerItem,
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = AppColors.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(item.imageColor)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                
                // Dietary tag or text
                if (item.dietaryTag != null) {
                    if (item.dietaryTagColor != null) {
                        Tag(
                            text = item.dietaryTag,
                            backgroundColor = item.dietaryTagColor,
                            textColor = AppColors.TextPrimary
                        )
                    } else {
                        Text(
                            text = item.dietaryTag,
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                }
            }
            
            // Price and Add button
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.price,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Button(
                    onClick = onAddClick,
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.ChefSecondary,
                        contentColor = AppColors.TextPrimary,
                    )
                ) {
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary,
                    )
                }
            }
        }
    }
}

