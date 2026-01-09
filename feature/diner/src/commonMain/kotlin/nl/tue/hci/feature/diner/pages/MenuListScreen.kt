package nl.tue.hci.feature.diner.pages

import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.tue.hci.feature.diner.ChefMenu
import nl.tue.hci.core.ui.getCarouselImageNames
import nl.tue.hci.core.ui.getChefCarouselImageNames
import nl.tue.hci.core.ui.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale


@Composable
fun MenuListScreen(
    chefName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onMenuClick: (String) -> Unit = {} // Callback when menu is selected
) {
    MenuListContent(
        chefName = chefName,
        modifier = modifier,
        onBackClick = onBackClick,
        onMenuClick = onMenuClick
    )
}

@Composable
private fun MenuListContent(
    chefName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onMenuClick: (String) -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Hardcoded menus for this chef
    val menus = remember(colors) {
        listOf(
            ChefMenu(
                id = "menu1",
                name = "Classic Japanese",
                description = "Traditional Japanese cuisine with fresh ingredients",
                dishCount = 5,
                priceRange = "€25-€45",
                imageColor = colors.imagePlaceholder1 // Light mint green
            ),
            ChefMenu(
                id = "menu2",
                name = "Fusion Delights",
                description = "East meets West in creative dishes",
                dishCount = 6,
                priceRange = "€30-€55",
                imageColor = colors.imagePlaceholder2 // Light orange/peach
            ),
            ChefMenu(
                id = "menu3",
                name = "Premium Selection",
                description = "Chef's special premium dishes",
                dishCount = 4,
                priceRange = "€45-€75",
                imageColor = colors.imagePlaceholder3 // Light blue
            ),
            ChefMenu(
                id = "menu4",
                name = "Seasonal Specials",
                description = "Fresh seasonal ingredients",
                dishCount = 7,
                priceRange = "€20-€40",
                imageColor = colors.imagePlaceholder4 // Light beige
            )
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {
        // Color the status bar area to match SearchResultsScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
        )

        // Header with back button
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
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
                    text = chefName,
                    style = typography.sectionTitle,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(menus, key = { it.id }) { menu ->
                val demoCoverImages = mapOf(
                    "menu1" to "omakase_5_course",
                    "menu2" to "grilled_mackerel_with_miso",
                    "menu3" to "sushi_platter",
                    "menu4" to "seared_seabass"
                )

                val candidates = getCarouselImageNames(menu.name) ?: getChefCarouselImageNames(chefName) ?: listOf()
                val imageName = remember(menu.id) {
                    demoCoverImages[menu.id] ?: (candidates.firstOrNull() ?: "yuzu_mousse")
                }

                MenuCard(
                    menu = menu,
                    imageName = imageName,
                    onClick = { onMenuClick(menu.name) }
                )
            }
        }
    }
}

@Composable
private fun MenuCard(
    menu: ChefMenu,
    imageName: String,
    onClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = colors.surface,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder
            Image(
                painter = rememberImagePainter(imageName),
                contentDescription = menu.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Menu info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = menu.name,
                    style = typography.cardTitle,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = menu.description,
                    style = typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${menu.dishCount} dishes",
                        style = typography.labelSmall,
                        color = colors.textTertiary,
                        maxLines = 1
                    )
                    
                    Text(
                        text = menu.priceRange,
                        style = typography.labelSmall,
                        color = colors.textTertiary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
