package nl.tue.hci.feature.diner.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
// Preview removed for multiplatform

/**
 * Navigation state for the home section
 */
enum class HomeScreenState {
    SEARCH,
    SEARCH_RESULTS,
    MENU
}

@Composable
fun DinerHomeScreen(
    modifier: Modifier = Modifier,
    currentState: HomeScreenState = HomeScreenState.SEARCH,
    selectedChefName: String? = null,
    onStateChange: (HomeScreenState) -> Unit = {},
    onChefSelect: (String) -> Unit = {},
    onChatClick: (String) -> Unit = {} // Callback to navigate to chat section
) {
    when (currentState) {
        HomeScreenState.MENU -> {
            MenuScreen(
                chefName = selectedChefName ?: "Chef",
                modifier = modifier,
                onBackClick = {
                    onStateChange(HomeScreenState.SEARCH_RESULTS)
                },
                onChatClick = onChatClick
            )
        }
        HomeScreenState.SEARCH_RESULTS -> {
            SearchResultsScreen(
                modifier = modifier,
                onBackClick = {
                    onStateChange(HomeScreenState.SEARCH)
                },
                onChefClick = { chefName ->
                    onChefSelect(chefName)
                    onStateChange(HomeScreenState.MENU)
                },
                onChatClick = onChatClick
            )
        }
        HomeScreenState.SEARCH -> {
            SearchScreen(
                modifier = modifier,
                onSearchClick = {
                    onStateChange(HomeScreenState.SEARCH_RESULTS)
                }
            )
        }
    }
}


