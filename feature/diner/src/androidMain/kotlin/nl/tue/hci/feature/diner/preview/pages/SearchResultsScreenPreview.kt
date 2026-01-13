package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.components.FilterModal
import nl.tue.hci.feature.diner.pages.SearchResultsScreen

@Preview
@Composable
fun SearchResultScreenPreview() {
    SearchResultsScreen(
        modifier = Modifier
    )
}

@Preview
@Composable
fun FilterModalPreview_onSearchResultsScreen() {
    FilterModal(
        onDismiss = {},
        onConfirm = {},
        selectedAllergens = emptySet(),
        onAllergensSelected = {},
        onOpenAllergensSelection = {},
        selectedCuisine = null,
        onCuisineSelected = {},
        onOpenCuisineSelection = {}
    )
}
