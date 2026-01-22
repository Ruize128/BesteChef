package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.pages.BookingSummaryScreen

@Preview(showBackground = true, name = "Booking Summary Screen")
@Composable
fun BookingSummaryScreenPreview() {
    BookingSummaryScreen(
        orderId = "1",
        modifier = Modifier,
        onBackClick = {},
        onBookAndPayClick = {}
    )
}

