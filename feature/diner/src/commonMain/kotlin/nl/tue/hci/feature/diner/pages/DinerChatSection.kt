package nl.tue.hci.feature.diner.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Wrapper component for the Chat section that manages navigation between
 * chat history list and individual chat screens.
 */
@Composable
fun DinerChatSection(
    modifier: Modifier = Modifier,
    initialChefName: String? = null,
    onInitialChatOpened: () -> Unit = {}
) {
    var showChatScreen by rememberSaveable { mutableStateOf(false) }
    var chatChefName by rememberSaveable { mutableStateOf("") }
    
    // Handle initial chef name when navigating from other sections
    LaunchedEffect(initialChefName) {
        if (initialChefName != null && !showChatScreen) {
            chatChefName = initialChefName
            showChatScreen = true
            onInitialChatOpened()
        }
    }
    
    if (showChatScreen) {
        DinerChatScreen(
            chefName = chatChefName,
            modifier = modifier,
            onBackClick = {
                showChatScreen = false
            }
        )
    } else {
        DinerChatHistoryScreen(
            modifier = modifier,
            onChatClick = { chefName ->
                chatChefName = chefName
                showChatScreen = true
            }
        )
    }
}

