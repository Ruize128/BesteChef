package nl.tue.hci.feature.diner.components
import nl.tue.hci.core.ui.BesteChefThemeColors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
// Preview removed for multiplatform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.getChefCarouselImageNames
import nl.tue.hci.core.ui.getChefImageName
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.feature.diner.ChefResult
import nl.tue.hci.feature.diner.pages.ImageCarouselWithPager

@Composable
fun SearchParameterChip(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) {
            colors.surfaceVariant
        } else {
            colors.surface
        },
        onClick = { }
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            style = typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = colors.textPrimary
        )
    }
} 

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector? = null,
    iconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    modifier: Modifier = Modifier
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()

    Surface(
        modifier = modifier.height(24.dp),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        onClick = { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconPainter != null) {
                Icon(
                    painter = iconPainter,
                    contentDescription = text,
                    modifier = Modifier.size(16.dp),
                    tint = colors.textPrimary
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(16.dp),
                    tint = colors.textPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = typography.bodySmall.copy(fontSize = 11.sp),
                color = colors.textPrimary,
            )
        }
    }
} 

@Composable
fun ChefResultCard(
    chef: ChefResult,
    onButtonClick: () -> Unit = {}
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
            // Image - use carousel if available, otherwise single image or color placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                val carouselImages = remember(chef.name) { getChefCarouselImageNames(chef.name) }
                val singleImageName = remember(chef.name) { getChefImageName(chef.name) }
                var currentImageIndex by remember(chef.name) { mutableStateOf(0) }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    if (carouselImages != null && carouselImages.isNotEmpty()) {
                        // Use carousel for chefs with multiple images (e.g., Chef Marius)
                        ImageCarouselWithPager(
                            images = carouselImages,
                            currentIndex = currentImageIndex,
                            onIndexChange = { currentImageIndex = it },
                            contentDescription = chef.name,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (singleImageName != null) {
                        // Single image (e.g., Chef Example Two)
                        Image(
                            painter = rememberImagePainter(singleImageName),
                            contentDescription = chef.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Color placeholder for others
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(chef.imageColor)
                        ) {
                            // Image carousel indicators (placeholder)
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
                                            .background(colors.surface.copy(alpha = 0.6f))
                                    )
                                }
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
                // Chef name
                Text(
                    text = chef.name,
                    style = typography.cardTitle,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                
                // Rating and reviews
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(18.dp),
                        tint = colors.favoriteIcon
                    )
                    Text(
                        text = "${chef.rating}",
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                    Text(
                        text = "• ${chef.reviewCount} reviews / ${chef.eventCount} events",
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                
                // Availability info
                if (chef.canTravel) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Can travel",
                            modifier = Modifier.size(16.dp),
                            tint = colors.onlineIndicator
                        )
                        Text(
                            text = "Can travel to your location",
                            style = typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
                
                if (chef.availableOnDate) {
                    Text(
                        text = "Available on your selected date",
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                
                // Quote
                Text(
                    text = "\"${chef.quote}\"",
                    style = typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = colors.textSecondary.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // CTA Button
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.dinerPrimary,
                        contentColor = colors.textOnPrimary,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Chat & Request quote",
                        style = typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

