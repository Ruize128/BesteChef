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
    MENU_LIST,
    MENU
}

@Composable
fun DinerHomeScreen(
    modifier: Modifier = Modifier,
    currentState: HomeScreenState = HomeScreenState.SEARCH,
    selectedChefName: String? = null,
    selectedMenuName: String? = null,
    selectedSearchLocation: String? = null,
    selectedSearchDate: kotlinx.datetime.LocalDate? = null,
    selectedSearchGuests: String? = null,
    onStateChange: (HomeScreenState) -> Unit = {},
    onChefSelect: (String) -> Unit = {},
    onMenuSelect: (String) -> Unit = {},
    onSearch: (location: String?, date: kotlinx.datetime.LocalDate?, guests: String) -> Unit = { _, _, _ -> },
    onChatClick: (String) -> Unit = {} // Callback to navigate to chat section
) {
    when (currentState) {
        HomeScreenState.MENU -> {
            MenuScreen(
                chefName = selectedChefName ?: "Chef",
                menuName = selectedMenuName ?: "",
                modifier = modifier,
                onBackClick = {
                    onStateChange(HomeScreenState.MENU_LIST)
                },
                onChatClick = onChatClick
            )
        }
        HomeScreenState.MENU_LIST -> {
            MenuListScreen(
                chefName = selectedChefName ?: "Chef",
                modifier = modifier,
                onBackClick = {
                    onStateChange(HomeScreenState.SEARCH_RESULTS)
                },
                onMenuClick = { menuName ->
                    // When a menu is selected, notify parent and navigate to the Menu screen
                    onMenuSelect(menuName)
                    onStateChange(HomeScreenState.MENU)
                }
            )
        }
        HomeScreenState.SEARCH_RESULTS -> {
            SearchResultsScreen(
                modifier = modifier,
                initialLocation = selectedSearchLocation,
                initialDate = selectedSearchDate,
                initialGuests = selectedSearchGuests,
                onBackClick = {
                    onStateChange(HomeScreenState.SEARCH)
                },
                onChefClick = { chefName ->
                    onChefSelect(chefName)
                    onStateChange(HomeScreenState.MENU_LIST)
                },
                onChatClick = onChatClick
            )
        }
        HomeScreenState.SEARCH -> {
            SearchScreen(
                modifier = modifier,
                onSearchClick = { location, date, guests ->
                    onSearch(location, date, guests)
                    onStateChange(HomeScreenState.SEARCH_RESULTS)
                }
            )
        }
    }
}


