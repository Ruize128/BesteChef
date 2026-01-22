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
    selectedSearchAllergens: Set<String> = emptySet(),
    selectedSearchCuisine: String? = null,
    onStateChange: (HomeScreenState) -> Unit = {},
    onChefSelect: (String) -> Unit = {},
    onMenuSelect: (String) -> Unit = {},
    onSearch: (location: String?, date: kotlinx.datetime.LocalDate?, guests: String, allergens: Set<String>, cuisine: String?) -> Unit = { _, _, _, _, _ -> },
    onChatClick: (String) -> Unit = {}, // Callback to navigate to chat section
    onBookFromMenu: () -> Unit = {}
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
                onChatClick = onChatClick,
                onBookClick = onBookFromMenu
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
                initialAllergens = selectedSearchAllergens,
                initialCuisine = selectedSearchCuisine,
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
                onSearchClick = { location, date, guests, allergens, cuisine ->
                    onSearch(location, date, guests, allergens, cuisine)
                    onStateChange(HomeScreenState.SEARCH_RESULTS)
                }
            )
        }
    }
}


