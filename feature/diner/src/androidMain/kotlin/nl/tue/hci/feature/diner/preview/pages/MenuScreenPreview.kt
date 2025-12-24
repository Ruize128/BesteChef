package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.pages.MenuScreen

@Preview
@Composable
fun MenuScreenPreview() {
    MenuScreen(
        chefName = "Chef Marius van Vlaanderen",
        modifier = Modifier,
        onBackClick = {}
    )
}
