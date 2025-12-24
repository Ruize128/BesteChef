package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import nl.tue.hci.feature.diner.components.DateDropdownMenu
import nl.tue.hci.feature.diner.components.LocationDropdownMenu
import nl.tue.hci.feature.diner.pages.SearchScreen

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(
        modifier = Modifier
    )
}

@Preview
@Composable
fun LocationDropdownMenuPreview_onSearchScreen() {
    LocationDropdownMenu(
        expanded = true,
        onDismissRequest = {},
        searchQuery = "Am",
        onSearchQueryChange = {},
        onLocationSelected = {},
    )
}

@Preview
@Composable
fun DateDropdownPreview_onSearchScreen() {
    DateDropdownMenu(
        expanded = true,
        onDismissRequest = {},
        selectedDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        onDateSelected = {},
    )
}


