package nl.tue.hci.feature.diner.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
// Preview removed for multiplatform

@Composable
fun DinerHomeScreen(
    modifier: Modifier = Modifier,
    onChatClick: (String) -> Unit = {} // Callback to navigate to chat section
) {
    var showSearchResults by rememberSaveable { mutableStateOf(false) }
    
    if (showSearchResults) {
        SearchResultsScreen(
            modifier = modifier,
            onBackClick = { showSearchResults = false },
            onChatClick = onChatClick
        )
    } else {
        SearchScreen(
            modifier = modifier,
            onSearchClick = { showSearchResults = true }
        )
    }
}

