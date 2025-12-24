package nl.tue.hci.core.data

import kotlinx.coroutines.flow.Flow
import nl.tue.hci.core.model.UserRole

expect class UserSessionRepository {
    val userRole: Flow<UserRole?>
    suspend fun setUserRole(role: UserRole)
    suspend fun clearSession()
}

expect fun createUserSessionRepository(): UserSessionRepository

