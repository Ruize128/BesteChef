package nl.tue.hci.feature.chef

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import nl.tue.hci.core.ui.BesteChefThemeColors
import nl.tue.hci.core.ui.BesteChefThemeTypography
import nl.tue.hci.core.ui.components.StatusBadge
import nl.tue.hci.feature.chef.model.BookingInquiry
import nl.tue.hci.feature.chef.model.BookingStatus

@Composable
fun CalendarModal(
    onDismiss: () -> Unit,
    bookings: List<BookingInquiry>,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onOrderClick: (String, String) -> Unit = { _, _ -> }
) {
    val colors = BesteChefThemeColors.current()
    val typography = BesteChefThemeTypography.current()
    var weekOffset by remember { mutableStateOf(0) }
    
    // Generate weeks starting from Dec 8, 2025
    val weekStartDates = listOf(
        listOf("Dec 8" to "Mon", "Dec 9" to "Tue", "Dec 10" to "Wed", "Dec 11" to "Thu", "Dec 12" to "Fri", "Dec 13" to "Sat", "Dec 14" to "Sun"),
        listOf("Dec 15" to "Mon", "Dec 16" to "Tue", "Dec 17" to "Wed", "Dec 18" to "Thu", "Dec 19" to "Fri", "Dec 20" to "Sat", "Dec 21" to "Sun"),
        listOf("Dec 22" to "Mon", "Dec 23" to "Tue", "Dec 24" to "Wed", "Dec 25" to "Thu", "Dec 26" to "Fri", "Dec 27" to "Sat", "Dec 28" to "Sun"),
        listOf("Dec 29" to "Mon", "Dec 30" to "Tue", "Dec 31" to "Wed", "Jan 1" to "Thu", "Jan 2" to "Fri", "Jan 3" to "Sat", "Jan 4" to "Sun")
    )
    val currentWeekIndex = weekOffset.coerceIn(0, weekStartDates.size - 1)
    val weekDates = weekStartDates[currentWeekIndex]
    val filteredBookings = bookings.filter { it.date == selectedDate }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        when {
                            dragAmount > 100 && currentWeekIndex > 0 -> weekOffset--
                            dragAmount < -100 && currentWeekIndex < weekStartDates.size - 1 -> weekOffset++
                        }
                    }
                },
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Calendar",
                        style = typography.titleLarge,
                        color = colors.textPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentWeekIndex > 0) weekOffset-- },
                        modifier = Modifier.size(36.dp),
                        enabled = currentWeekIndex > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous week",
                            tint = if (currentWeekIndex > 0) colors.chefPrimary else colors.textSecondary
                        )
                    }
                    Text(
                        text = "December 2025",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    IconButton(
                        onClick = { if (currentWeekIndex < weekStartDates.size - 1) weekOffset++ },
                        modifier = Modifier.size(36.dp),
                        enabled = currentWeekIndex < weekStartDates.size - 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next week",
                            tint = if (currentWeekIndex < weekStartDates.size - 1) colors.chefPrimary else colors.textSecondary
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekDates.forEach { (_, dayOfWeek) ->
                            Text(
                                text = dayOfWeek,
                                style = typography.bodySmall,
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekDates.forEach { (label, _) ->
                            val month = label.split(" ").firstOrNull() ?: ""
                            val dayNumber = label.split(" ").getOrNull(1) ?: label
                            val hasBooking = bookings.any { it.status == BookingStatus.CONFIRMED && it.date == label }
                            val isSelected = selectedDate == label
                            val bgColor = when {
                                isSelected -> colors.chefPrimary
                                hasBooking -> colors.surfaceVariant
                                else -> colors.surface
                            }
                            val textColor = if (isSelected) colors.textOnPrimary else colors.textPrimary

                            // Check if this date has any bookings and get the most important status
                            val dateBookings = bookings.filter { it.date == label }
                            val hasBookings = dateBookings.isNotEmpty()
                            // Priority: NEW > CONFIRMED > UNANSWERED
                            val primaryStatus = dateBookings.minByOrNull {
                                when (it.status) {
                                    BookingStatus.NEW -> 0
                                    BookingStatus.CONFIRMED -> 1
                                    BookingStatus.UNANSWERED -> 2
                                }
                            }?.status
                            val indicatorColor = when (primaryStatus) {
                                BookingStatus.NEW -> colors.statusNewText
                                BookingStatus.CONFIRMED -> colors.statusConfirmedText
                                BookingStatus.UNANSWERED -> colors.statusOngoingText
                                null -> colors.statusNewText
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onDateSelected(label) },
                                shape = RoundedCornerShape(8.dp),
                                color = bgColor,
                                shadowElevation = 0.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = dayNumber,
                                        style = typography.bodyMedium,
                                        color = textColor,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = month,
                                        style = typography.bodySmall,
                                        color = textColor.copy(alpha = 0.8f)
                                    )
                                    
                                    // Indicator for dates with bookings
                                    if (hasBookings) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 10.dp, height = 4.dp)
                                                .background(
                                                    color = indicatorColor,
                                                    shape = RoundedCornerShape(2.dp)
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = colors.surfaceVariant
                )

                Text(
                    text = "Reservations",
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBookings) { booking ->
                        val interactionSource = remember { MutableInteractionSource() }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .indication(interactionSource, androidx.compose.material3.ripple(color = colors.chefPrimary, radius = 12.dp))
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onOrderClick(booking.id, booking.status.name) }
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = colors.surfaceVariant,
                            shadowElevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = colors.chefPrimary
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = booking.date.split(" ").getOrNull(1) ?: "",
                                            style = typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textOnPrimary,
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = booking.date.split(" ").firstOrNull() ?: "",
                                            style = typography.bodySmall,
                                            color = colors.textOnPrimary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = booking.customerName,
                                        style = typography.titleMedium,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "${booking.guests} guests • 19:00",
                                        style = typography.bodySmall,
                                        color = colors.textSecondary
                                    )
                                }

                                StatusBadge(
                                    text = booking.statusLabel,
                                    backgroundColor = when (booking.status) {
                                        BookingStatus.NEW -> colors.statusNewBackground
                                        BookingStatus.CONFIRMED -> colors.statusConfirmedBackground
                                        BookingStatus.UNANSWERED -> colors.statusOngoingBackground
                                    },
                                    textColor = when (booking.status) {
                                        BookingStatus.NEW -> colors.statusNewText
                                        BookingStatus.CONFIRMED -> colors.statusConfirmedText
                                        BookingStatus.UNANSWERED -> colors.statusOngoingText
                                    }
                                )
                            }
                        }
                    }

                    if (filteredBookings.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No reservations",
                                    style = typography.bodyMedium,
                                    color = colors.textSecondary,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
