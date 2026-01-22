package nl.tue.hci.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import nl.tue.hci.core.model.UserRole
import kotlinx.browser.localStorage

actual class UserSessionRepository {
    private val _userRole = MutableStateFlow<UserRole?>(null)
    private val storageKey = "userRole"
    
    init {
        // Load from localStorage if available
        try {
            val storedRole = localStorage.getItem(storageKey)
            if (storedRole != null) {
                _userRole.value = UserRole.valueOf(storedRole)
            }
        } catch (e: Throwable) {
            // If localStorage is not available or fails, start with null
            _userRole.value = null
        }
    }

    actual val userRole: Flow<UserRole?> = _userRole

    actual suspend fun setUserRole(role: UserRole) {
        _userRole.value = role
        try {
            localStorage.setItem(storageKey, role.name)
        } catch (e: Throwable) {
            // If localStorage fails, continue with in-memory storage
        }
    }

    actual suspend fun clearSession() {
        _userRole.value = null
        try {
            localStorage.removeItem(storageKey)
        } catch (e: Throwable) {
            // If localStorage fails, continue with in-memory storage
        }
    }
}

actual fun createUserSessionRepository(): UserSessionRepository {
    return UserSessionRepository()
}

