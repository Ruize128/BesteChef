package nl.tue.hci.bestechef

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import nl.tue.hci.core.ui.BesteChefTheme
import nl.tue.hci.core.data.createUserSessionRepository
import nl.tue.hci.core.model.UserRole
import nl.tue.hci.feature.chef.ChefScreen
import nl.tue.hci.feature.diner.pages.DinerScreen
import nl.tue.hci.login.LoginScreen
import nl.tue.hci.login.LoginStateHolder

@Composable
fun BesteChefApp() {
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
                    ChefScreen(
                        onLogout = {
                            coroutineScope.launch {
                                userSessionRepository.clearSession()
                            }
                        }
                    )
                }
                UserRole.DINER -> {
                    DinerScreen(
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

