package nl.tue.hci.feature.chef

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.Avatar
import nl.tue.hci.core.ui.components.FilterButton
import nl.tue.hci.core.ui.components.StatusBadge
import nl.tue.hci.feature.chef.model.BookingInquiry
import nl.tue.hci.feature.chef.model.BookingStatus
import nl.tue.hci.feature.chef.notification.sendChatNotification
import nl.tue.hci.core.utils.formatDate
import kotlinx.datetime.LocalDate

@Composable
fun ChefHomeScreen(
    modifier: Modifier = Modifier,
    onChatClick: (String) -> Unit = {}, // customerName
    onOrderClick: (String, String) -> Unit = { _, _ -> }, // bookingId, status
    hasShownNotification: Boolean = false,
    onNotificationShown: () -> Unit = {},
    unreadMessageCount: Int = 0
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Send notification only once per app session when home screen is first shown
    LaunchedEffect(Unit) {
        if (!hasShownNotification) {
            kotlinx.coroutines.delay(1000) // Delay 1 second to ensure heads-up appears
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                sendChatNotification("Sophie", "Question about dessert...") {
                    // Navigate to chat with Sophie when notification is clicked
                    onChatClick("Sophie")
                }
            }
            onNotificationShown()
        }
    }
    
    // Hardcoded data
    val bookings = 1
    val inquiries = 1
    
    // Read selected date from GlobalDatabase (set by diner), default to tomorrow if not found
    val selectedBookingDate = nl.tue.hci.core.data.GlobalDatabase.readString("diner_selected_date")?.let { dateString ->
        try {
            LocalDate.parse(dateString)
        } catch (e: Exception) {
            // If parsing fails, default to tomorrow
            LocalDate(2026, 1, 22) // Tomorrow from current date (2026-01-21)
        }
    } ?: LocalDate(2026, 1, 22) // Tomorrow from current date (2026-01-21)
    
    val bookingsList = listOf(
        BookingInquiry(
            id = "1",
            customerName = "Sophie",
            message = "Question about dessert...",
            date = formatDate(selectedBookingDate)?.take(6) ?: "Jan 22", // Take first 6 chars like "Dec 12"
            guests = 6,
            timeAgo = "2m",
            status = BookingStatus.NEW,
            statusLabel = "New"
        ),
        BookingInquiry(
            id = "2",
            customerName = "Liam",
            message = "Booking confirmed (Dec 10 • 6 guests)",
            date = "Dec 10",
            guests = 6,
            timeAgo = "3d",
            status = BookingStatus.CONFIRMED,
            statusLabel = "Confirmed"
        )
    )
    
    var selectedFilter by remember { mutableStateOf("All") }
    var isLoading by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(formatDate(selectedBookingDate)?.take(6) ?: "Jan 22") }
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    // Handle loading state when filter changes
    LaunchedEffect(selectedFilter) {
        isLoading = true
        kotlinx.coroutines.delay(300) // Mock loading delay
        isLoading = false
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {
        // Fixed header section with just avatar and title
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                Avatar(
                    text = "MC",
                    size = 40,
                    backgroundColor = colors.chefPrimary,
                    imageName = "ichiraku", // Chef's avatar image
                    modifier = Modifier
                        .padding(start = 0.dp)
                        .align(alignment = Alignment.CenterStart)
                )
                Text(
                    text = "Chef Dashboard",
                    style = typography.sectionTitle,
                    color = colors.textPrimary,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
        }
        
        // Today stats card - white container with 20dp corners on light gray background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            color = colors.surface,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Today",
                    style = typography.labelMedium,
                    color = colors.textSecondary,
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$bookings bookings • $inquiries inquiries",
                        style = typography.cardTitle,
                        color = colors.textPrimary
                    )
                    
                    Text(
                        text = "View calendar",
                        style = typography.bodySmall,
                        color = colors.chefPrimary,
                        modifier = Modifier
                            .width(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = { showCalendar = true })
                            .padding(vertical = 4.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // Filter buttons with gray background
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 48.dp, vertical = 12.dp),
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
                    hasUnreadMessage = booking.customerName == "Sophie" && unreadMessageCount > 0,
                    onClick = { onChatClick(booking.customerName) }
                )
            }
        }
        
        // Loading overlay
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = colors.surface,
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp,
                            color = colors.chefPrimary
                        )
                    }
                }
            }
        }
    }
    
    // Calendar modal
    if (showCalendar) {
        CalendarModal(
            onDismiss = { showCalendar = false },
            bookings = bookingsList,
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it },
            onOrderClick = { bookingId, status ->
                // Navigate to order edit screen with booking ID and status
                onOrderClick(bookingId, status)
                showCalendar = false
            }
        )
    }
}
@Composable
private fun BookingInquiryCard(
    booking: BookingInquiry,
    hasUnreadMessage: Boolean = false,
    onClick: () -> Unit = {}
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = colors.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
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
                    "Sophie" -> colors.imagePlaceholder3 // Light pink
                    else -> colors.imagePlaceholder1 // Light green
                },
                imageName = if (booking.customerName == "Sophie") "sophie" else null
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Red dot indicator for unread messages
                    if (hasUnreadMessage) {
                        Surface(
                            modifier = Modifier.size(6.dp),
                            shape = CircleShape,
                            color = colors.alert,
                        ) {}
                    }
                    
                    Text(
                        text = booking.customerName,
                        style = typography.cardTitle,
                        color = colors.textPrimary
                    )
                }
                
                Text(
                    text = booking.message,
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
                
                // Show date/guests separately if message doesn't already include it
                if (!booking.message.contains("(")) {
                    Text(
                        text = "(${booking.date} • ${booking.guests} guests)",
                        style = typography.bodySmall,
                        color = colors.textSecondary
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
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
                
                StatusBadge(
                    text = booking.statusLabel,
                    backgroundColor = when (booking.status) {
                        BookingStatus.NEW -> colors.statusNewBackground
                        BookingStatus.CONFIRMED -> colors.statusConfirmedBackground
                        BookingStatus.UNANSWERED -> colors.buttonBackground
                    },
                    textColor = when (booking.status) {
                        BookingStatus.NEW -> colors.statusNewText
                        BookingStatus.CONFIRMED -> colors.statusConfirmedText
                        BookingStatus.UNANSWERED -> colors.textPrimary
                    }
                )
            }
        }
    }
}
