package nl.tue.hci.feature.diner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun DinerHomeScreenPreview() {
    DinerHomeScreen(
        modifier = Modifier
    )
}


@Composable
fun DinerHomeScreen(modifier: Modifier = Modifier) {
    var showSearchResults by rememberSaveable { mutableStateOf(false) }
    
    if (showSearchResults) {
        SearchResultsScreen(
            modifier = modifier,
            onBackClick = { showSearchResults = false }
        )
    } else {
        SearchScreen(
            modifier = modifier,
            onSearchClick = { showSearchResults = true }
        )
    }
}

