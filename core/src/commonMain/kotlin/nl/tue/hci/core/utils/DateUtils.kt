package nl.tue.hci.core.utils

import kotlinx.datetime.LocalDate

// Helper function to format date for display
fun formatDate(date: LocalDate?): String {
    if (date == null) return ""
    val monthName = getMonthName(date.monthNumber).take(3)
    return "$monthName ${date.dayOfMonth}, ${date.year}"
}

// Helper function to get month name
fun getMonthName(monthNumber: Int): String {
    return when (monthNumber) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> "Unknown"
    }
}