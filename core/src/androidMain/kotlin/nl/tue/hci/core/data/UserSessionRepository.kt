package nl.tue.hci.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.tue.hci.core.model.UserRole

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")
private val USER_ROLE_KEY = stringPreferencesKey("user_role")

// Platform-specific context holder
object PlatformContext {
    var context: Context? = null
}

actual class UserSessionRepository {
    private lateinit var dataStore: DataStore<Preferences>
    
    init {
        val ctx = PlatformContext.context
            ?: throw IllegalStateException("Context not initialized. Call PlatformContext.context = ... first")
        dataStore = ctx.dataStore
    }

    actual val userRole: Flow<UserRole?> = dataStore.data.map { preferences ->
        preferences[USER_ROLE_KEY]?.let { roleName ->
            try {
                UserRole.valueOf(roleName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    actual suspend fun setUserRole(role: UserRole) {
        dataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role.name
        }
    }

    actual suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ROLE_KEY)
        }
    }
}

actual fun createUserSessionRepository(): UserSessionRepository {
    return UserSessionRepository()
}

