package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.pages.DinerOrdersScreen

@Preview(showBackground = true, name = "Diner Orders Screen")
@Composable
fun DinerOrdersScreenPreview() {
    DinerOrdersScreen(
        modifier = Modifier,
        onOrderClick = {}
    )
}

