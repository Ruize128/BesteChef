package nl.tue.hci.feature.diner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale



@Preview
@Composable
fun DateDropdownMenuPreview() {
    MaterialTheme {
        DateDropdownMenu(
            expanded = true,
            onDismissRequest = {},
            selectedDate = LocalDate.now(),
            onDateSelected = {},
        )
    }
}


/**
 * A reusable date dropdown menu component with a custom calendar.
 * This component only contains the dropdown menu itself, not the field display.
 * 
 * @param expanded Whether the dropdown menu is currently open
 * @param onDismissRequest Callback when the dropdown should be closed
 * @param selectedDate The currently selected date, or null if none selected
 * @param onDateSelected Callback when a date is selected
 * @param modifier Modifier for the dropdown menu
 */
@Composable
fun DateDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    if (expanded) {
        Popup(
            onDismissRequest = onDismissRequest,
            alignment = Alignment.TopCenter,
            offset = IntOffset(0, 100), // Offset from top center
            properties = PopupProperties(focusable = true)
        ) {
            Card(
                modifier = modifier.width(340.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                CustomCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        onDateSelected(date)
                        onDismissRequest()
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CustomCalendar(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(selectedDate ?: today) }
    
    val firstDayOfMonth = currentMonth.withDayOfMonth(1)
    val lastDayOfMonth = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth())
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 = Monday, 7 = Sunday
    val daysInMonth = currentMonth.lengthOfMonth()
    
    Column(modifier = modifier) {
        // Month/Year header with navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { currentMonth = currentMonth.minusMonths(1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val dayAbbreviations = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
            dayAbbreviations.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        Column {
            var dayCounter = 1
            var weekCount = 0
            
            while (dayCounter <= daysInMonth || weekCount == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (dayOfWeek in 1..7) {
                        if (weekCount == 0 && dayOfWeek < firstDayOfWeek) {
                            // Empty cells before the first day of the month
                            Spacer(modifier = Modifier.width(36.dp).height(36.dp))
                        } else if (dayCounter <= daysInMonth) {
                            val date = firstDayOfMonth.plusDays((dayCounter - 1).toLong())
                            val isPast = date.isBefore(today)
                            val isSelected = selectedDate?.equals(date) == true
                            
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(36.dp)
                                    .then(
                                        if (!isPast) {
                                            Modifier.clickable { onDateSelected(date) }
                                        } else {
                                            Modifier
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(16.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayCounter.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Text(
                                        text = dayCounter.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isPast) {
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isPast) FontWeight.Normal else FontWeight.Bold
                                    )
                                }
                            }
                            dayCounter++
                        } else {
                            // Empty cells after the last day of the month
                            Spacer(modifier = Modifier.width(36.dp).height(36.dp))
                        }
                    }
                }
                weekCount++
                if (dayCounter > daysInMonth && weekCount >= 5) break
            }
        }
    }
}

// Helper function to format date for display
fun formatDate(date: LocalDate?): String {
    if (date == null) return ""
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return date.format(formatter)
}


