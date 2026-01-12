package nl.tue.hci.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import nl.tue.hci.core.model.UserRole
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

actual class UserSessionRepository {
    private val _userRole = MutableStateFlow<UserRole?>(null)
    private val storageDir = File(System.getProperty("user.home"), ".bestechef")
    private val storageFile = File(storageDir, "session.txt")
    
    init {
        // Create storage directory if it doesn't exist
        storageDir.mkdirs()
        
        // Load from file if available
        try {
            if (storageFile.exists()) {
                val storedRole = storageFile.readText().trim()
                if (storedRole.isNotEmpty()) {
                    _userRole.value = UserRole.valueOf(storedRole)
                }
            }
        } catch (e: Throwable) {
            // If file read fails, start with null
            _userRole.value = null
        }
    }

    actual val userRole: Flow<UserRole?> = _userRole

    actual suspend fun setUserRole(role: UserRole) {
        _userRole.value = role
        try {
            storageFile.writeText(role.name)
        } catch (e: Throwable) {
            // If file write fails, continue with in-memory storage
        }
    }

    actual suspend fun clearSession() {
        _userRole.value = null
        try {
            if (storageFile.exists()) {
                storageFile.delete()
            }
        } catch (e: Throwable) {
            // If file delete fails, continue with in-memory storage
        }
    }
}

actual fun createUserSessionRepository(): UserSessionRepository {
    return UserSessionRepository()
}
