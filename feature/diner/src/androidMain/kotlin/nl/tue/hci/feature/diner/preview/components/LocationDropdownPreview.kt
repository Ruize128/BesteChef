package nl.tue.hci.feature.diner.preview.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.components.LocationDropdownMenu

@Preview
@Composable
fun LocationDropdownMenuPreview() {
    LocationDropdownMenu(
        expanded = true,
        onDismissRequest = {},
        searchQuery = "",
        onSearchQueryChange = {},
        onLocationSelected = {},
        modifier = Modifier,
    )
}
