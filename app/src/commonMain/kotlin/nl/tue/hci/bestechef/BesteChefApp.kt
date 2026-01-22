package nl.tue.hci.bestechef

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import nl.tue.hci.core.ui.BesteChefTheme
import nl.tue.hci.core.data.createUserSessionRepository
import nl.tue.hci.core.data.GlobalDatabase
import nl.tue.hci.core.model.UserRole
import nl.tue.hci.feature.chef.ChefScreen
import nl.tue.hci.feature.diner.pages.DinerScreen
import nl.tue.hci.feature.login.LoginScreen
import nl.tue.hci.feature.login.LoginStateHolder

@Composable
fun BesteChefApp(
    initialNavigateToOrders: Boolean = false,
    initialNavigateToChat: Boolean = false,
    initialChatCustomerName: String? = null,
    initialNavigateToBookingSummary: Boolean = false
) {
    val userSessionRepository = remember { createUserSessionRepository() }
    val userRole by userSessionRepository.userRole.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    BesteChefTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val role = userRole) {
                null -> {
                    // Show login screen
                    val loginStateHolder = remember { LoginStateHolder(coroutineScope) }
                    LoginScreen(
                        onLogin = { selectedRole ->
                            // Save role and navigate (handled by state change)
                            coroutineScope.launch {
                                userSessionRepository.setUserRole(selectedRole)
                            }
                        },
                        stateHolder = loginStateHolder
                    )
                }
                UserRole.CHEF -> {
                    // Initialize unread message count on first chef login
                    LaunchedEffect(Unit) {
                        val existingCount = GlobalDatabase.readString("chef_unread_count")
                        if (existingCount == null) {
                            // First time opening chef side, set 1 unread message from Sophie
                            GlobalDatabase.writeString("chef_unread_count", "1")
                        }
                    }
                    
                    ChefScreen(
                        initialNavigateToOrders = initialNavigateToOrders,
                        initialNavigateToChat = initialNavigateToChat,
                        initialChatCustomerName = initialChatCustomerName,
                        onLogout = {
                            coroutineScope.launch {
                                userSessionRepository.clearSession()
                            }
                        }
                    )
                }
                UserRole.DINER -> {
                    DinerScreen(
                        initialNavigateToBookingSummary = initialNavigateToBookingSummary,
                        onLogout = {
                            coroutineScope.launch {
                                userSessionRepository.clearSession()
                            }
                        }
                    )
                }
            }
        }
    }
}

