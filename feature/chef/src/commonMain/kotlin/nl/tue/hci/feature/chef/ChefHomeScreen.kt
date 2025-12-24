package nl.tue.hci.feature.chef

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.tue.hci.core.ui.AppColors
import nl.tue.hci.core.ui.components.Avatar
import nl.tue.hci.core.ui.components.EditButton
import nl.tue.hci.core.ui.components.StatCard
import nl.tue.hci.core.ui.components.Tag
import nl.tue.hci.feature.chef.model.ChefMenuItem
import nl.tue.hci.feature.chef.model.ChefStats

@Composable
fun ChefHomeScreen(modifier: Modifier = Modifier) {
    // Hardcoded data
    val chefName = "Chef Ichiraku"
    val chefRating = 5.0f
    val stats = ChefStats(
        totalOrders = 13,
        totalRevenue = "$1,234",
        totalReviews = 2
    )
    
    val menuItems = listOf(
        ChefMenuItem(
            id = "1",
            title = "Grilled Mackerel with Miso",
            description = "Sea salt, spring onion, yuzu dressing.",
            serves = "2-3",
            prepTime = "45 min prep",
            imageColor = Color(0xFFB2E5D4)
        ),
        ChefMenuItem(
            id = "2",
            title = "Yuzu Mousse (Dessert)",
            description = "Light citrus mousse with candied peel.",
            serves = "6",
            prepTime = "30 min prep",
            imageColor = Color(0xFFFFD4B2)
        ),
        ChefMenuItem(
            id = "3",
            title = "Wagyu Beef Steak",
            description = "Premium wagyu with truffle butter and seasonal vegetables.",
            serves = "2",
            prepTime = "60 min prep",
            imageColor = Color(0xFFE8D5C4)
        ),
        ChefMenuItem(
            id = "4",
            title = "Sushi Platter",
            description = "Assorted fresh sushi with wasabi and pickled ginger.",
            serves = "4-5",
            prepTime = "90 min prep",
            imageColor = Color(0xFFB2E5D4)
        )
    )
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top bar
        item {
            TopBar()
        }
        
        // Profile section
        item {
            ProfileSection(
                chefName = chefName,
                rating = chefRating,
                onEditClick = {}
            )
        }
        
        // Stats section
        item {
            StatsSection(stats = stats)
        }
        
        // My Menu section header
        item {
            MenuSectionHeader()
        }
        
        // Menu items
        items(menuItems) { item ->
            ChefMenuItemCard(
                menuItem = item,
                onEditClick = {}
            )
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Menu",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AppColors.TextPrimary
            )
        }
    }
}

@Composable
private fun ProfileSection(
    chefName: String,
    rating: Float,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                text = chefName.take(2).uppercase(),
                size = 64,
                backgroundColor = AppColors.ChefPrimary
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = chefName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFD700)
                    )
                    Text(
                        text = "$rating",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.TextPrimary
                    )
                }
            }
            
            EditButton(onClick = onEditClick)
        }
    }
}

@Composable
private fun StatsSection(stats: ChefStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Total Orders",
            value = stats.totalOrders.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Total Revenue",
            value = stats.totalRevenue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Total Reviews",
            value = stats.totalReviews.toString(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MenuSectionHeader() {
    Text(
        text = "My Menu",
        modifier = Modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = AppColors.TextPrimary
    )
}

@Composable
private fun ChefMenuItemCard(
    menuItem: ChefMenuItem,
    onEditClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(menuItem.imageColor)
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = menuItem.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Text(
                            text = menuItem.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    EditButton(onClick = onEditClick)
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Tag(
                        text = "Serves ${menuItem.serves}",
                        backgroundColor = AppColors.ButtonGrey
                    )
                    Tag(
                        text = menuItem.prepTime,
                        backgroundColor = AppColors.ButtonGrey
                    )
                }
            }
        }
    }
}

