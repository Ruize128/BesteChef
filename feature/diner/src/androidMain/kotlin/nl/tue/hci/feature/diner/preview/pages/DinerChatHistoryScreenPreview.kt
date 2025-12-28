package nl.tue.hci.feature.diner.preview.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import nl.tue.hci.feature.diner.pages.DinerChatHistoryScreen

@Preview(showBackground = true, name = "Diner Chat History Screen")
@Composable
fun DinerChatHistoryScreenPreview() {
    DinerChatHistoryScreen(
        modifier = Modifier,
        onChatClick = {}
    )
}

