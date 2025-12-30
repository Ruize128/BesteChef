package nl.tue.hci.feature.diner.pages
import nl.tue.hci.core.ui.BesteChefThemeColors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.getImageNameFromTitle
import nl.tue.hci.core.ui.getCarouselImageNames
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.core.ui.icons.rememberIconPainter
import nl.tue.hci.feature.diner.MenuItem


@Composable
fun MenuScreen(
    chefName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {} // Callback to navigate to chat section
) {
    MenuContent(
        chefName = chefName,
        modifier = modifier,
        onBackClick = onBackClick,
        onChatClick = {
            onChatClick(chefName) // Pass chef name to navigate to chat
        }
    )
}

@Composable
private fun MenuContent(
    chefName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with back button and chat icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Text(
                text = chefName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
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
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        
        // Menu items list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuItems) { item ->
                MenuItemCard(
                    menuItem = item,
                    onAskClick = onChatClick
                )
            }
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    onAskClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        contentDescription = menuItem.title,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (imageName != null) {
                    // Single image
                    Image(
                        painter = rememberImagePainter(imageName),
                        contentDescription = menuItem.title,
                        modifier = Modifier.fillMaxSize(),
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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(24.dp)
                    ) {
                        val chatIconPainter = rememberIconPainter("chat_icon")
                        Icon(
                            painter = chatIconPainter,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Description
                Text(
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Serves and prep time
                Text(
                    text = "Serves ${menuItem.serves} · ${menuItem.prepTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Ask button
                    Button(
                        onClick = onAskClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        val chatIconPainter = rememberIconPainter("chat_icon")
                        Icon(
                            painter = chatIconPainter,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ask",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Book button
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.dinerPrimary,
                            contentColor = colors.textPrimary
                        )
                    ) {
                        Text(
                            text = "Book",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

