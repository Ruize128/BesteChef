package nl.tue.hci.bestechef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import nl.tue.hci.bestechef.ui.theme.BesteChefTheme
import nl.tue.hci.core.data.UserSessionRepository
import nl.tue.hci.core.model.UserRole
import nl.tue.hci.feature.chef.ChefScreen
import nl.tue.hci.feature.diner.DinerScreen
import nl.tue.hci.login.LoginScreen

class MainActivity : ComponentActivity() {
    private lateinit var userSessionRepository: UserSessionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        userSessionRepository = UserSessionRepository(this)
        
        setContent {
            BesteChefTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BesteChefApp(userSessionRepository = userSessionRepository)
                }
            }
        }
    }
}

@Composable
fun BesteChefApp(userSessionRepository: UserSessionRepository) {
    val userRole by userSessionRepository.userRole.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    when (val role = userRole) {
        null -> {
            // Show login screen
            LoginScreen(
                onLogin = { selectedRole ->
                    // Save role and navigate (handled by state change)
                    coroutineScope.launch {
                        userSessionRepository.setUserRole(selectedRole)
                    }
                }
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
