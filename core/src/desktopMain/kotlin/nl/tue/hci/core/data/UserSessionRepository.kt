package nl.tue.hci.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import nl.tue.hci.core.model.UserRole

actual class UserSessionRepository {
    private val _userRole = MutableStateFlow<UserRole?>(null)
    
    init {
        // Data is stored only in-memory, nothing persists to disk
        // Application starts fresh each time it's launched
    }

    actual val userRole: Flow<UserRole?> = _userRole

    actual suspend fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    actual suspend fun clearSession() {
        _userRole.value = null
    }
}

actual fun createUserSessionRepository(): UserSessionRepository {
    return UserSessionRepository()
}
