package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.pages.ChatScreen

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        chefName = "Chef Ichiraku",
        modifier = Modifier,
        onBackClick = {}
    )
}