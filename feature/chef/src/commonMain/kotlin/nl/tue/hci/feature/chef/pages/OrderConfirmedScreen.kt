package nl.tue.hci.feature.chef.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import nl.tue.hci.core.ui.AppColors
import nl.tue.hci.core.ui.components.StatusBadge
import nl.tue.hci.core.ui.getImageNameFromTitle
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.feature.chef.model.OrderDetails
import nl.tue.hci.feature.chef.model.OfferMenuItem

@Composable
fun OrderConfirmedScreen(
    orderDetails: OrderDetails,
    menuItems: List<OfferMenuItem>,
    bookingNumber: String = "12345",
    chefName: String = "Sophie",
    cuisineType: String = "Seasonal Japanese Fusion",
    modifier: Modifier = Modifier,
    onDoneClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
            .padding(top = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 88.dp) // Reserve space for Done button (40dp height + 32dp bottom + 16dp top)
        ) {
            // Top spacer - helps center content when menu is short
            Spacer(modifier = Modifier.weight(1f).heightIn(min = 20.dp))
            
            // Content area - centered vertically when menu is short
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success icon - teal-green circle with checkmark
                Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4DB6AC)), // Teal-green
                contentAlignment = Alignment.Center
            ) {
                // Outer lighter green circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF80CBC4)), // Lighter teal-green
                )
                
                // Checkmark icon
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirmed",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Confirmation text
                Text(
                    text = "Booking confirmed",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Chef name and cuisine
                Text(
                    text = "$chefName — $cuisineType",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppColors.TextSecondary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Booking details card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF5F5F5), // Light grey
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "${orderDetails.date} • ${orderDetails.time}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        
                        Text(
                            text = "${orderDetails.guests} guests • Booking #$bookingNumber",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AppColors.TextSecondary
                        )
                        
                        // Deposit received badge
                        StatusBadge(
                            text = "Deposit received",
                            backgroundColor = AppColors.StatusConfirmedBackground, // Light green
                            textColor = AppColors.StatusConfirmedText // Dark green
                        )
                        
                        Text(
                            text = "Venue: ${orderDetails.venue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Final menu section with scrollable LazyColumn
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false), // Constrain menu section to available space
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Final menu (summary)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // LazyColumn with constrained height for scrolling
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false), // Constrain LazyColumn to available space
                        contentPadding = PaddingValues(bottom = 24.dp), // Add bottom padding to prevent cutoff
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(menuItems) { item ->
                            FinalMenuItemCard(item = item)
                        }
                    }
                }
            }
            
            // Bottom spacer - helps center content when menu is short
            Spacer(modifier = Modifier.weight(1f).heightIn(min = 20.dp))
        }
        
        // Done button - fixed at bottom, outside the scrollable content
        Button(
            onClick = onDoneClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp, top = 16.dp)
                .height(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.ChefPrimary,
                contentColor = AppColors.TextPrimary
            ),
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text = "Done",
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = AppColors.TextPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FinalMenuItemCard(item: OfferMenuItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5), // Light grey
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
                    .size(60.dp)
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
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}
