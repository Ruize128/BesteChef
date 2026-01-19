package nl.tue.hci.feature.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import nl.tue.hci.core.model.UserRole

/**
 * State holder for LoginScreen (replaces ViewModel for multiplatform)
 * Manages the state and business logic for the login screen
 */
class LoginStateHolder(
    private val coroutineScope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    /**
     * Update email value
     */
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    /**
     * Update password value
     */
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null, validationAttempted = false) }
    }

    /**
     * Update confirm password value
     */
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null, validationAttempted = false) }
    }

    /**
     * Toggle password visibility
     */
    fun togglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible, confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    /**
     * Toggle confirm password visibility
     */
    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible, confirmPasswordVisible = !it.confirmPasswordVisible) }
    }

    /**
     * Select user role
     */
    fun selectRole(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role, errorMessage = null) }
    }

    /**
     * Enable sign up mode
     */
    fun enableSignUp() {
        _uiState.update {
            it.copy(isSigningUp = true, isSigningIn = false)
        }
    }
    /**
     * Reset to initial state (go back to sign in mode)
     */
    fun resetToSignIn() {
        _uiState.update {
            it.copy(
                isSigningIn = false,
                isSigningUp = false,
                email = "",
                password = "",
                confirmPassword = "",
                selectedRole = null,
                errorMessage = null,
                passwordVisible = false,
                confirmPasswordVisible = false,
                isLoading = false,
                validationAttempted = false
            )
        }
    }
    fun enableSignIn() {
        _uiState.update {
            it.copy(isSigningIn = true, isSigningUp = false)
        }
    }

    /**
     * Called when the email input loses focus: immediately re-evaluate whether
     * the UI should be in sign-in or sign-up mode.
     */
    fun onEmailFocusLost() {
        val currentState = _uiState.value
        // Only run when currently in sign-in or sign-up mode
        if (!currentState.isSigningUp && !currentState.isSigningIn) return
        // Show mock loading state while re-checking
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        coroutineScope.launch {
            try {
                // Reuse the same simulated delay as onContinueClick
                delay(400)
                val email = _uiState.value.email.trim()
                if (!isValidEmail(email)) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@launch
                }

                when (email) {
                    "diner@domain.com", "chef@domain.com" -> switchToSignInMode()
                    else -> switchToSignUpMode()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "An error occurred while checking email."
                    )
                }
            }
        }
    }

    private fun switchToSignInMode() {
        _uiState.update {
            it.copy(
                isSigningIn = true,
                isSigningUp = false,
                errorMessage = null,
                password = "",
                confirmPassword = "",
                selectedRole = null,
                passwordVisible = false,
                confirmPasswordVisible = false,
                isLoading = false
            )
        }
    }

    private fun switchToSignUpMode() {
        _uiState.update {
            it.copy(
                isSigningIn = false,
                isSigningUp = true,
                errorMessage = null,
                password = "",
                confirmPassword = "",
                selectedRole = it.selectedRole ?: UserRole.DINER,
                passwordVisible = false,
                confirmPasswordVisible = false,
                isLoading = false
            )
        }
    }

    /**
     * Handle Continue button click with email
     */
    fun onContinueClick() {
        val email = _uiState.value.email.trim()
        val currentState = _uiState.value
        
        // Mark that validation has been attempted
        _uiState.update { it.copy(validationAttempted = true) }
        
        if (email.isEmpty()) {
            _uiState.update { 
                it.copy(errorMessage = "Please enter your email address") 
            }
            return
        }
        
        if (!isValidEmail(email)) {
            _uiState.update { 
                it.copy(errorMessage = "Please enter a valid email address") 
            }
            return
        }
        
        // Check password match when signing up
        if (currentState.isSigningUp && currentState.password != currentState.confirmPassword) {
            _uiState.update { 
                it.copy(errorMessage = "Passwords do not match") 
            }
            return
        }

        // Set loading state
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        coroutineScope.launch {
            try {
                // Simulate API call or validation
                // TODO: Replace with actual authentication logic
                delay(500) // Simulate network delay

                if (email == "diner@domain.com") {
                    // Auto-login as DINER
                    val currentState = _uiState.value
                    
                    // If isSigningIn == true, jump to Diner Main Page directly
                    if (currentState.isSigningIn) {
                        // mock check password
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                navigationEvent = LoginNavigationEvent.NavigateToDinerMainPage(UserRole.DINER)
                            ) 
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                isSigningIn = true,
                                navigationEvent = LoginNavigationEvent.NavigateToRoleSelection(email)
                            ) 
                        }
                    }
                    return@launch
                } else if (email == "chef@domain.com") {
                    // Auto-login as CHEF
                    val currentState = _uiState.value

                    // If isSigningIn == true, jump to Chef Main Page directly
                    if (currentState.isSigningIn) {
                        // mock check password
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                navigationEvent = LoginNavigationEvent.NavigateToChefMainPage(UserRole.CHEF)
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSigningIn = true,
                                navigationEvent = LoginNavigationEvent.NavigateToRoleSelection(email)
                            )
                        }
                    }
                    return@launch
                } else {
                    // Enable sign up mode and set default role to CHEF
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSigningUp = true,
                            selectedRole = UserRole.DINER
                        )
                    }
                    return@launch
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "An error occurred. Please try again."
                    ) 
                }
            }
        }
    }
    
    /**
     * Handle sign up completion - navigate based on selected role
     */
    fun completeSignUp() {
        val currentState = _uiState.value
        
        // Navigate based on role
        when (currentState.selectedRole) {
            UserRole.CHEF -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigationEvent = LoginNavigationEvent.NavigateToChefMainPage(UserRole.CHEF)
                    )
                }
            }
            UserRole.DINER -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigationEvent = LoginNavigationEvent.NavigateToDinerProfileSetup
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Please select a role"
                    )
                }
            }
        }
    }

    /**
     * Handle Google login button click
     */
    fun onGoogleLoginClick() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        coroutineScope.launch {
            try {
                // TODO: Implement Google Sign-In logic
                delay(800) // Simulate network delay
                
                // Mock: Google continues as DINER
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigationEvent = LoginNavigationEvent.NavigateToDinerMainPage(UserRole.DINER)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Google sign-in failed. Please try again."
                    ) 
                }
            }
        }
    }

    /**
     * Handle Apple login button click
     */
    fun onAppleLoginClick() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        coroutineScope.launch {
            try {
                // TODO: Implement Apple Sign-In logic
                delay(600) // Simulate network delay
                
                // Mock: Apple continues as CHEF
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigationEvent = LoginNavigationEvent.NavigateToChefMainPage(UserRole.CHEF)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Apple sign-in failed. Please try again."
                    ) 
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Consume navigation event after it's been handled
     */
    fun consumeNavigationEvent() {
        _uiState.update { it.copy(navigationEvent = LoginNavigationEvent.Consumed) }
    }

    /**
     * Simple email validation (multiplatform)
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        return emailRegex.matches(email)
    }
}

