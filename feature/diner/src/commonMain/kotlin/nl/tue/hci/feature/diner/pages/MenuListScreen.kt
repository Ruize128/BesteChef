package nl.tue.hci.feature.diner.pages

import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
// Outline star not available on all targets; use filled stars with tints
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.tue.hci.feature.diner.ChefMenu
import nl.tue.hci.core.ui.getCarouselImageNames
import nl.tue.hci.core.ui.getChefCarouselImageNames
import nl.tue.hci.core.ui.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


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
                dishCount = 4,
                priceRange = "€95-€125",
                imageColor = colors.imagePlaceholder1 // Light mint green
            ),
            ChefMenu(
                id = "menu2",
                name = "Fusion Delights",
                description = "East meets West in creative dishes",
                dishCount = 6,
                priceRange = "€60-€95",
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
                priceRange = "€30-€50",
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

        // Unified scroll: chef info, reviews, and menu list in one LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Top spacing below header
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Chef info header as a list item
            item {
                ChefInfoHeader(
                    chefName = chefName,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            // Reviews carousel as a list item
            item {
                ReviewsCarousel(
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            // Menu section header
            item {
                val colors = BesteChefThemeColors.current()
                val typography = BesteChefThemeTypography.current()
                Text(
                    text = "Menu",
                    style = typography.sectionTitle,
                    color = colors.textPrimary
                )
            }
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Menu items
            items(menus, key = { it.id }) { menu ->
                val demoCoverImages = mapOf(
                    "menu1" to "omakase_5_course",
                    "menu2" to "grilled_mackerel_with_miso",
                    "menu3" to "sushi_platter",
                    "menu4" to "seared_seabass"
                )

                val candidates = getCarouselImageNames(menu.name) ?: getChefCarouselImageNames(chefName) ?: listOf()
                val imageName = remember(menu.id) {
                    demoCoverImages[menu.id] ?: (candidates.firstOrNull() ?: "honey_nut_caramel")
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
private fun ChefInfoHeader(
    chefName: String,
    modifier: Modifier = Modifier
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()

    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = colors.surface,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top row: avatar + basic info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Image(
                    painter = rememberImagePainter("ichiraku"),
                    contentDescription = "$chefName avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = chefName,
                        style = typography.cardTitle,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "Seasonal Japanese Fusion • Omakase",
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            // Ratings row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RatingStars(rating = 4.7f)
                Text(
                    text = "5.0 (7 reviews)",
                    style = typography.labelSmall,
                    color = colors.textSecondary
                )
//                Divider(
//                    modifier = Modifier.height(12.dp).width(1.dp),
//                    color = colors.outline
//                )
//                Text(
//                    text = "42 events • 100% reliability",
//                    style = typography.labelSmall,
//                    color = colors.textSecondary
//                )
            }

            // Introduction paragraph
            Text(
                text = "I blend traditional Japanese techniques with seasonal European produce. My cooking focuses on balanced flavors, precise execution, and a warm, relaxed dining experience. Favorites include my Honey Nut & Caramel dessert and a 5‑course Omakase curated to your preferences.",
                style = typography.bodySmall,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
    max: Int = 5
) {
    val colors = BesteChefThemeColors.current()
    Row(modifier = modifier) {
        val fullStars = rating.toInt().coerceIn(0, max)
        val hasHalf = (rating - fullStars) >= 0.5f
        val emptyStars = max - fullStars - (if (hasHalf) 1 else 0)

        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = colors.dinerPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        if (hasHalf) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = colors.dinerPrimary.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
        }
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = colors.textTertiary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private data class Review(
    val author: String,
    val rating: Float,
    val date: String,
    val text: String
)

@Composable
private fun ReviewsCarousel(
    modifier: Modifier = Modifier
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    var expandedReview by remember { mutableStateOf<Review?>(null) }

    val reviews = listOf(
        Review(
            author = "Sophie",
            rating = 5.0f,
            date = "Dec 2025",
            text = "Absolutely loved the Omakase course. Every dish was thoughtful and perfectly balanced. Highly recommended!"
        ),
        Review(
            author = "Marco",
            rating = 5.0f,
            date = "Nov 2025",
            text = "Fantastic seasonal menu and great attention to dietary preferences. The Honey Nut & Caramel dessert was a standout."
        ),
        Review(
            author = "Elena",
            rating = 5.0f,
            date = "Oct 2025",
            text = "Chef Ichiraku’s fusion approach is refreshing. Presentation and flavors were top-notch."
        )
    )

    Column(
        modifier = modifier
    ) {
        Text(
            text = "Reviews",
            style = typography.sectionTitle,
            color = colors.textPrimary,
            modifier = Modifier.padding(horizontal = 0.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reviews) { review ->
                Surface(
                    modifier = Modifier
                        .width(240.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { expandedReview = review },
                    color = colors.surface,
                    shadowElevation = 2.dp,
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = review.author,
                                style = typography.cardTitle,
                                color = colors.textPrimary
                            )
                            RatingStars(rating = review.rating)
                            Text(
                                text = review.date,
                                style = typography.labelSmall,
                                color = colors.textSecondary
                            )
                        }
                        Text(
                            text = review.text,
                            style = typography.bodySmall,
                            color = colors.textSecondary,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        expandedReview?.let { review ->
            Dialog(onDismissRequest = { expandedReview = null }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = colors.surface,
                    tonalElevation = 4.dp,
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                    ) {
                        IconButton(
                            onClick = { expandedReview = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Close,
//                                contentDescription = "Close review",
//                                tint = colors.textSecondary
//                            )
                            Text(
                                text = "x",
                                fontSize = 20.sp,
                                color = colors.textSecondary,
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = review.author,
                                    style = typography.cardTitle,
                                    color = colors.textPrimary
                                )
                                RatingStars(rating = review.rating)
                                Text(
                                    text = review.date,
                                    style = typography.labelSmall,
                                    color = colors.textSecondary
                                )
                            }
                            Text(
                                text = review.text,
                                style = typography.bodyMedium,
                                color = colors.textPrimary
                            )
                        }
                    }
                }
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
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // View Menu button
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.dinerPrimary,
                    contentColor = colors.textOnPrimary,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "View Menu",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
            }
        }
    }
}
