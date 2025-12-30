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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.FilterButton
import nl.tue.hci.core.ui.components.Tag
import nl.tue.hci.core.ui.getImageNameFromTitle
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.feature.chef.model.MenuPickerItem
import nl.tue.hci.feature.chef.model.SelectedMenuItem

@Composable
fun MenuPickerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onItemSelected: (List<SelectedMenuItem>) -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Hardcoded menu items
    val allMenuItems = remember(colors) {
        listOf(
            MenuPickerItem(
                id = "1",
                title = "Yuzu mousse",
                description = "light citrus dessert.",
                price = "€8",
                imageColor = colors.imagePlaceholder1, // Light green
                dietaryTag = "Nut-free",
                dietaryTagColor = colors.chefPrimary,
                category = "Desserts"
            ),
            MenuPickerItem(
                id = "2",
                title = "Seared seabass",
                description = "miso glaze, seasonal veg.",
                price = "€14",
                imageColor = colors.imagePlaceholder2, // Light orange-beige
                dietaryTag = "Fish",
                dietaryTagColor = colors.dinerPrimary, // Light blue/cyan
                category = "Mains"
            ),
            MenuPickerItem(
                id = "3",
                title = "5-course Omakase",
                description = "chef's selection (per guest).",
                price = "€65",
                imageColor = colors.imagePlaceholder3, // Light pink
                dietaryTag = "Contains nuts",
                dietaryTagColor = null, // Plain text, no tag
                category = "Mains"
            ),
            MenuPickerItem(
                id = "4",
                title = "Grilled Mackerel with Miso",
                description = "Sea salt, spring onion, yuzu dressing.",
                price = "€12",
                imageColor = colors.imagePlaceholder1,
                dietaryTag = "Fish",
                dietaryTagColor = colors.dinerPrimary,
                category = "Mains"
            ),
            MenuPickerItem(
                id = "5",
                title = "Wagyu Beef Steak",
                description = "Premium wagyu with truffle butter.",
                price = "€45",
                imageColor = colors.imagePlaceholder4,
                dietaryTag = null,
                dietaryTagColor = null,
                category = "Mains"
            ),
            MenuPickerItem(
                id = "6",
                title = "Caesar Salad",
                description = "Fresh romaine, parmesan, croutons.",
                price = "€9",
                imageColor = colors.imagePlaceholder1,
                dietaryTag = "Vegetarian",
                dietaryTagColor = colors.statusConfirmedBackground, // Light green
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
            .background(colors.background)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                    text = "Menu picker",
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )
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
                    color = colors.textSecondary
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = colors.textSecondary
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.outline,
                unfocusedBorderColor = colors.outline,
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface
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
                    containerColor = colors.chefPrimary,
                    contentColor = colors.textPrimary,
                    disabledContainerColor = colors.chefSecondary,
                    disabledContentColor = colors.textSecondary,
                ),
                shape = RoundedCornerShape(20.dp),
                enabled = selectedItems.isNotEmpty(),
                onClick = {
                    onItemSelected(selectedItems.toList())
                    onBackClick()
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
                        style = typography.buttonText,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = "€${selectedItems.sumOf { 
                            val priceStr = it.menuItem.price.replace("€", "").replace(",", ".")
                            (priceStr.toDoubleOrNull()?.times(it.quantity) ?: 0.0).toInt()
                        }}",
                        style = typography.cardTitle,
                        color = colors.textPrimary,
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
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image - use real image if available, otherwise use color placeholder
            val imageName = remember(item.title) { getImageNameFromTitle(item.title) }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                if (imageName != null) {
                    Image(
                        painter = rememberImagePainter(imageName),
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // No image available, use color placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(item.imageColor)
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = item.description,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
                
                // Dietary tag or text
                if (item.dietaryTag != null) {
                    if (item.dietaryTagColor != null) {
                        Tag(
                            text = item.dietaryTag,
                            backgroundColor = item.dietaryTagColor,
                            textColor = colors.textPrimary
                        )
                    } else {
                        Text(
                            text = item.dietaryTag,
                            style = typography.bodySmall,
                            color = colors.textSecondary
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
                    color = colors.textPrimary
                )
                Button(
                    onClick = onAddClick,
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.chefSecondary,
                        contentColor = colors.textPrimary,
                    )
                ) {
                    Text(
                        text = "Add",
                        style = typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary,
                    )
                }
            }
        }
    }
}

