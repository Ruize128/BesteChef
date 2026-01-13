package nl.tue.hci.feature.diner.pages
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.ImagePreviewOverlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import nl.tue.hci.core.ui.PlatformBackHandler
import androidx.compose.ui.layout.ContentScale
import nl.tue.hci.core.ui.getImageNameFromTitle
import nl.tue.hci.core.ui.getCarouselImageNames
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.ui.components.QuantitySelector
import nl.tue.hci.core.ui.icons.rememberIconPainter
import nl.tue.hci.feature.diner.MenuItem


@Composable
fun MenuScreen(
    chefName: String,
    menuName: String = "",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {}, // Callback to navigate to chat section
    onBookClick: () -> Unit = {}
) {
    MenuContent(
        chefName = chefName,
        menuName = menuName,
        modifier = modifier,
        onBackClick = onBackClick,
        onChatClick = {
            onChatClick(chefName) // Pass chef name to navigate to chat
        },
        onBookClick = onBookClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuContent(
    chefName: String,
    menuName: String = "",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onBookClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    var imagePreviewShown by rememberSaveable { mutableStateOf(false) }
    var currentPreviewImage by rememberSaveable { mutableStateOf<String?>(null) }
    
    // Hardcoded menu items
    val menuItems = remember(colors) {
        listOf(
            MenuItem(
                title = "Grilled Mackerel with Miso",
                description = "Sea salt, spring onion, yuzu dressing.",
                serves = "2-3",
                prepTime = "45 min prep",
                imageColor = colors.imagePlaceholder1 // Light green
            ),
            MenuItem(
                title = "Yuzu Mousse (Dessert)",
                description = "Light citrus mousse with candied peel.",
                serves = "6",
                prepTime = "30 min prep",
                imageColor = colors.imagePlaceholder2 // Light orange/peach
            ),
            MenuItem(
                title = "Wagyu Beef Steak",
                description = "Premium wagyu with truffle butter and seasonal vegetables.",
                serves = "2",
                prepTime = "60 min prep",
                imageColor = colors.imagePlaceholder4 // Light beige
            ),
            MenuItem(
                title = "Sushi Platter",
                description = "Assorted fresh sushi with wasabi and pickled ginger.",
                serves = "4-5",
                prepTime = "90 min prep",
                imageColor = colors.imagePlaceholder1 // Light green
            )
        )
    }

    var showCartSheet by rememberSaveable { mutableStateOf(false) }
    var showBookConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf(menuItems.associate { it.title to 1 }.toMutableMap()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button and chat icon
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (imagePreviewShown) {
                                imagePreviewShown = false
                                currentPreviewImage = null
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
                    
                    Text(
                        text = chefName,
                        style = typography.sectionTitle,
                        color = colors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    
                    IconButton(
                        onClick = onChatClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        val chatIconPainter = rememberIconPainter("chat_icon")
                        Icon(
                            painter = chatIconPainter,
                            contentDescription = "Chat",
                            tint = colors.textPrimary
                        )
                    }
                }
            }
            
            // Menu items list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    MenuItemCard(
                        menuItem = item,
                        onAskClick = onChatClick,
                        onImageClick = { imageName ->
                            imagePreviewShown = true
                            currentPreviewImage = imageName
                        }
                    )
                }
            }
        }

        // Floating cart button
        FloatingActionButton(
            onClick = { showCartSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = colors.dinerPrimary,
            contentColor = colors.textPrimary
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart"
            )
        }

        // Cart bottom sheet
        if (showCartSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showCartSheet = false },
                sheetState = sheetState,
                containerColor = colors.surface,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Menu items",
                        style = typography.cardTitle,
                        color = colors.textPrimary
                    )

                    cartItems.entries.forEach { (title, qty) ->
                        val menuItem = menuItems.find { it.title == title }
                        val imageName = remember(title) { getImageNameFromTitle(title) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .alpha(if (qty == 0) 0.5f else 1f)
                            ) {
                                when {
                                    imageName != null -> {
                                        Image(
                                            painter = rememberImagePainter(imageName),
                                            contentDescription = title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    menuItem != null -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(menuItem.imageColor)
                                        )
                                    }
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .alpha(if (qty == 0) 0.5f else 1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = title,
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary
                                )
                                menuItem?.let {
                                    Text(
                                        text = it.description,
                                        style = typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            QuantitySelector(
                                quantity = qty,
                                onDecrease = {
                                    val current = cartItems[title] ?: 1
                                    if (current > 0) {
                                        cartItems = cartItems.toMutableMap().apply { this[title] = current - 1 }
                                    }
                                },
                                onIncrease = {
                                    val current = cartItems[title] ?: 1
                                    cartItems = cartItems.toMutableMap().apply { this[title] = current + 1 }
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                // Reset to defaults and close
                                cartItems = menuItems.associate { it.title to 1 }.toMutableMap()
                                showCartSheet = false
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceVariant,
                                contentColor = colors.textPrimary
                            )
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                showBookConfirmDialog = true
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
                            Text("Book")
                        }
                    }
                }
            }
        }

        if (showBookConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showBookConfirmDialog = false },
                title = {
                    Text(
                        text = "Confirm booking?",
                        style = typography.sectionTitle,
                        color = colors.textPrimary
                    )
                },
                text = {
                    Text(
                        text = "Proceed to book this menu and open booking summary?",
                        style = typography.bodyMedium,
                        color = colors.textSecondary
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showBookConfirmDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.surfaceVariant,
                                contentColor = colors.textPrimary
                            )
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                showBookConfirmDialog = false
                                showCartSheet = false
                                onBookClick()
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
                            Text("Confirm")
                        }
                    }
                },
                containerColor = colors.surface,
                titleContentColor = colors.textPrimary,
                textContentColor = colors.textSecondary
            )
        }

        // Handle back button when image preview is showing
        PlatformBackHandler(enabled = imagePreviewShown) {
            imagePreviewShown = false
            currentPreviewImage = null
        }

        // Image Preview Overlay
        ImagePreviewOverlay(
            showPreview = imagePreviewShown,
            imageName = currentPreviewImage,
            onDismiss = {
                imagePreviewShown = false
                currentPreviewImage = null
            }
        )
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    onAskClick: () -> Unit = {},
    onImageClick: ((String) -> Unit)? = null
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image - use real image if available, otherwise use color placeholder
            val imageName = remember(menuItem.title) { getImageNameFromTitle(menuItem.title) }
            val carouselImages = remember(menuItem.title) { getCarouselImageNames(menuItem.title) }
            var currentImageIndex by remember(menuItem.title) { mutableStateOf(0) }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (carouselImages != null && carouselImages.isNotEmpty()) {
                    // Use HorizontalPager for smooth carousel
                    ImageCarouselWithPager(
                        images = carouselImages,
                        currentIndex = currentImageIndex,
                        onIndexChange = { currentImageIndex = it },
                        onImageClick = onImageClick,
                        contentDescription = menuItem.title,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (imageName != null) {
                    // Single image
                    Image(
                        painter = rememberImagePainter(imageName),
                        contentDescription = menuItem.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (onImageClick != null) {
                                    Modifier.clickable { onImageClick(imageName) }
                                } else {
                                    Modifier
                                }
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // No image available, use color placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(menuItem.imageColor)
                    ) {
                        // Image carousel indicators (only show if using color placeholder)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(colors.textPrimary.copy(alpha = 0.6f))
                        )
                            }
                        }
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title with star icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = menuItem.title,
                        style = typography.cardTitle,
                        color = colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = onAskClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        val chatIconPainter = rememberIconPainter("chat_icon")
                        Icon(
                            painter = chatIconPainter,
                            contentDescription = "Chat",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Description
                Text(
                    text = menuItem.description,
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
                
                // Serves and prep time
                Text(
                    text = "Serves ${menuItem.serves} · ${menuItem.prepTime}",
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons removed; chat icon on title navigates to chat
            }
        }
    }
}

