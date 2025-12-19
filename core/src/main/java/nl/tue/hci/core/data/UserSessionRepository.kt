package nl.tue.hci.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.tue.hci.core.model.UserRole

class UserSessionRepository(context: Context) {
    private val dataStore = context.dataStore

    val userRole: Flow<UserRole?> = dataStore.data.map { preferences ->
        preferences[USER_ROLE_KEY]?.let { roleName ->
            try {
                UserRole.valueOf(roleName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    suspend fun setUserRole(role: UserRole) {
        dataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role.name
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ROLE_KEY)
        }
    }
}

