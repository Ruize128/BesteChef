package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.pages.DinerChatScreen

@Preview
@Composable
fun DinerChatScreenPreview() {
    DinerChatScreen(
        chefName = "Chef Ichiraku",
        modifier = Modifier,
        onBackClick = {}
    )
}