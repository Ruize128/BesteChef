package nl.tue.hci.feature.diner.preview.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import nl.tue.hci.feature.diner.components.DateDropdownMenu

@Preview
@Composable
fun DateDropdownMenuPreview() {
    MaterialTheme {
        DateDropdownMenu(
            expanded = true,
            onDismissRequest = {},
            selectedDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
            onDateSelected = {},
        )
    }
}
