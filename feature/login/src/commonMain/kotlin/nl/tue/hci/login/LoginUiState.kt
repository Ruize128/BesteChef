package nl.tue.hci.login

import nl.tue.hci.core.model.UserRole

/**
 * UI State for Login Screen
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val selectedRole: UserRole? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val navigationEvent: LoginNavigationEvent? = null,
    val isSigningUp: Boolean = false,
    val isSigningIn: Boolean = false,
)

/**
 * Sealed class for navigation events
 * Used to trigger navigation actions from state holder
 */
sealed class LoginNavigationEvent {
    data class NavigateToRoleSelection(val email: String) : LoginNavigationEvent()
    data class NavigateWithGoogle(val email: String? = null) : LoginNavigationEvent()
    data class NavigateWithApple(val email: String? = null) : LoginNavigationEvent()
    data class NavigateToDinerMainPage(val role: UserRole) : LoginNavigationEvent()
    
    // Clear the event after it's been handled
    object Consumed : LoginNavigationEvent()
}

