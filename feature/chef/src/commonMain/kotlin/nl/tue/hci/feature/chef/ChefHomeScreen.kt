package nl.tue.hci.feature.chef

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.tue.hci.core.ui.AppColors
import nl.tue.hci.core.ui.components.Avatar
import nl.tue.hci.core.ui.components.FilterButton
import nl.tue.hci.core.ui.components.StatusBadge
import nl.tue.hci.feature.chef.model.BookingInquiry
import nl.tue.hci.feature.chef.model.BookingStatus
import nl.tue.hci.feature.chef.ui.theme.BesteChefTheme


@Composable
fun ChefHomeScreen(
    modifier: Modifier = Modifier,
    onChatClick: (String) -> Unit = {} // customerName
) {
    // Hardcoded data
    val bookings = 1
    val inquiries = 1
    
    val bookingsList = listOf(
        BookingInquiry(
            id = "1",
            customerName = "Sophie",
            message = "Question about dessert...",
            date = "Dec 12",
            guests = 6,
            timeAgo = "2m",
            status = BookingStatus.NEW,
            statusLabel = "New"
        ),
        BookingInquiry(
            id = "2",
            customerName = "Liam",
            message = "Booking confirmed (Dec 12 • 6 guests)",
            date = "Dec 12",
            guests = 6,
            timeAgo = "3d",
            status = BookingStatus.CONFIRMED,
            statusLabel = "Confirmed"
        )
    )
    
    var selectedFilter by remember { mutableStateOf("All") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Fixed header section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            // Header with avatar and title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                Avatar(
                    text = "MC",
                    size = 40,
                    backgroundColor = AppColors.ChefPrimary,
                    imageName = "ichiraku", // Chef's avatar image
                    modifier = Modifier
                        .padding(start = 0.dp)
                        .align(alignment = Alignment.CenterStart)
                )
                Text(
                    text = "Chef Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
            
            // Today stats card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = AppColors.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Normal,
                        color = AppColors.TextSecondary,
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$bookings bookings • $inquiries inquiries",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = AppColors.TextPrimary
                        )
                        
                        Text(
                            text = "View calendar",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.ChefPrimary,
                            modifier = Modifier
                                .width(64.dp)
                                .clickable(onClick = {}),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }


            // Filter buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterButton(
                    text = "All",
                    isSelected = selectedFilter == "All",
                    onClick = { selectedFilter = "All" },
                    modifier = Modifier.weight(1f)
                )
                FilterButton(
                    text = "Unanswered",
                    isSelected = selectedFilter == "Unanswered",
                    onClick = { selectedFilter = "Unanswered" },
                    modifier = Modifier.weight(1f)
                )
                FilterButton(
                    text = "Today",
                    isSelected = selectedFilter == "Today",
                    onClick = { selectedFilter = "Today" },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Scrollable list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bookingsList) { booking ->
                BookingInquiryCard(
                    booking = booking,
                    onClick = { onChatClick(booking.customerName) }
                )
            }
        }
    }
}

@Composable
private fun BookingInquiryCard(
    booking: BookingInquiry,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = AppColors.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar
            Avatar(
                text = booking.customerName.take(1).uppercase(),
                size = 48,
                backgroundColor = when (booking.customerName) {
                    "Sophie" -> Color(0xFFFFB3BA) // Light pink
                    else -> Color(0xFFB3FFBA) // Light green
                },
                imageName = if (booking.customerName == "Sophie") "sophie" else null
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = booking.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = AppColors.TextPrimary
                )
                
                Text(
                    text = booking.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary
                )
                
                // Show date/guests separately if message doesn't already include it
                if (!booking.message.contains("(")) {
                    Text(
                        text = "(${booking.date} • ${booking.guests} guests)",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.TextSecondary
                    )
                }
            }
            
            // Right side: time and status
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = booking.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextSecondary
                )
                
                StatusBadge(
                    text = booking.statusLabel,
                    backgroundColor = when (booking.status) {
                        BookingStatus.NEW -> AppColors.StatusNewBackground
                        BookingStatus.CONFIRMED -> AppColors.StatusConfirmedBackground
                        BookingStatus.UNANSWERED -> AppColors.ButtonGrey
                    },
                    textColor = when (booking.status) {
                        BookingStatus.NEW -> AppColors.StatusNewText
                        BookingStatus.CONFIRMED -> AppColors.StatusConfirmedText
                        BookingStatus.UNANSWERED -> AppColors.TextPrimary
                    }
                )
            }
        }
    }
}
