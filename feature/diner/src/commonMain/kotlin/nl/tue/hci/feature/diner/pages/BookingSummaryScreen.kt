package nl.tue.hci.feature.diner.pages

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.remember
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.getImageNameFromTitle
import nl.tue.hci.core.ui.rememberImagePainter
import nl.tue.hci.feature.diner.BookingSummaryDetails
import nl.tue.hci.feature.diner.BookingSummaryMenuItem
import nl.tue.hci.feature.diner.BookingPriceSummary

@Composable
fun BookingSummaryScreen(
    orderId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onBookAndPayClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Hardcoded booking summary data
    val bookingDetails = remember {
        BookingSummaryDetails(
            location = "Eindhoven",
            date = "Dec 12, 2025",
            time = "19:00",
            guests = 6,
            venue = "123 Food Street, Eindhoven"
        )
    }
    
    val menuItems = remember(colors) {
        listOf(
            BookingSummaryMenuItem(
                id = "1",
                title = "Grilled Mackerel",
                description = "Serves 2-3 • Contains: Fish",
                price = "€45",
                imageColor = colors.imagePlaceholder1 // Light green
            ),
            BookingSummaryMenuItem(
                id = "2",
                title = "Yuzu Mousse",
                description = "Serves 6 • Can be nut-free",
                price = "€48",
                imageColor = colors.imagePlaceholder2 // Light orange
            )
        )
    }
    
    val priceSummary = remember {
        val subtotal = 93.0 // €45 + €48
        val serviceFee = 10.0
        val depositPercentage = 20
        val depositAmount = (subtotal + serviceFee) * depositPercentage / 100.0
        
        BookingPriceSummary(
            subtotal = "€${subtotal.toInt()}",
            serviceFee = "€${serviceFee.toInt()}",
            depositAmount = "€${depositAmount.toInt()}",
            depositPercentage = depositPercentage
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant) // Light gray background
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary
                    )
                }
                
                Text(
                    text = "Booking summary",
                    style = typography.sectionTitle,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.size(48.dp)) // Balance the back button
            }
        }
        
        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Overview card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = colors.surface, // Light grey
                    shadowElevation = 0.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "${bookingDetails.location} • ${bookingDetails.date} • ${bookingDetails.guests} guests",
                            style = typography.bodyMedium,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
            
            // Date & Time and Guests section (combined in one card)
            item {
                DateAndGuestsCard(
                    date = "${bookingDetails.date} • ${bookingDetails.time}",
                    guests = "${bookingDetails.guests} guests",
                    onDateEditClick = {},
                    onGuestsChangeClick = {}
                )
            }
            
            // Venue section
            item {
                BookingSection(
                    title = "Venue",
                    value = bookingDetails.venue,
                    onEditClick = {},
                    editButtonText = "Edit"
                )
            }
            
            // Menu summary section
            item {
                Text(
                    text = "Menu summary",
                    style = typography.cardTitle,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            items(menuItems) { item ->
                MenuItemCard(item = item)
            }
            
            // Price summary section
            item {
                PriceSummarySection(priceSummary = priceSummary)
            }
        }
        
        // Book & Pay button
        Button(
            onClick = onBookAndPayClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp, top = 16.dp)
                .height(40.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.dinerPrimary,
                contentColor = colors.textPrimary
            )
        ) {
            Text(
                text = "Book & Pay ${priceSummary.depositAmount} deposit (${priceSummary.depositPercentage}%)",
                style = typography.cardTitle,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun DateAndGuestsCard(
    date: String,
    guests: String,
    onDateEditClick: () -> Unit,
    onGuestsChangeClick: () -> Unit
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface, // Pure white card
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date & Time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Date & time",
                        style = typography.cardTitle,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = date,
                        style = typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                }
                
                TextButton(onClick = onDateEditClick) {
                    Text(
                        text = "Edit",
                        color = colors.dinerPrimary
                    )
                }
            }
            
            // Guests row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Guests",
                        style = typography.cardTitle,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = guests,
                        style = typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                }
                
                TextButton(onClick = onGuestsChangeClick) {
                    Text(
                        text = "Change",
                        color = colors.dinerPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingSection(
    title: String,
    value: String,
    onEditClick: () -> Unit,
    editButtonText: String = "Edit"
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface, // Pure white card
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = typography.cardTitle,
                    fontStyle = FontStyle.Italic,
                    color = colors.textPrimary,
                )
                Text(
                    text = value,
                    style = typography.bodyMedium,
                    color = colors.textSecondary,
                )
            }
            
            TextButton(onClick = onEditClick) {
                Text(
                    text = editButtonText,
                    color = colors.dinerPrimary
                )
            }
        }
    }
}

@Composable
private fun MenuItemCard(item: BookingSummaryMenuItem) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface, // Pure white card
        shadowElevation = 0.dp
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
                    .clip(RoundedCornerShape(12.dp))
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
                    style = typography.cardTitle.copy(fontSize = 14.sp),
                    color = colors.textPrimary
                )
                Text(
                    text = item.description,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            
            // Price
            Text(
                text = item.price,
                style = typography.cardTitle,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
private fun PriceSummarySection(priceSummary: BookingPriceSummary) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Price summary",
                style = typography.cardTitle,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = priceSummary.subtotal,
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Service fee",
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
                Text(
                    text = priceSummary.serviceFee,
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colors.textSecondary
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = colors.surfaceVariant,
                    )
            )
            
            // Deposit due now - highlighted
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Deposit due now",
                    style = typography.cardTitle,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = "${priceSummary.depositAmount} (${priceSummary.depositPercentage}%)",
                    style = typography.cardTitle,
                    fontWeight = FontWeight.Bold,
                    color = colors.dinerPrimary
                )
            }
        }
    }
}

