package org.example.project.manager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.example.project.data.settings.AuthPreferences
import org.example.project.setGuestUser
import org.example.project.setPersistedString
import org.example.project.setUserLoggedIn

object AppManager {
    private val _logoutEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvents: SharedFlow<Unit> = _logoutEvents.asSharedFlow()

    fun logout() {
        println("[AppManager] User logged out due to invalid or expired session.")
        setUserLoggedIn(false)
        setGuestUser(false)
        AuthPreferences().clearTokens()
        setPersistedString("logged_in_user", null)
        setPersistedString("logged_in_user_id", null)
        setPersistedString("logged_in_user_name", null)
        setPersistedString("logged_in_user_mobile", null)
        setPersistedString("logged_in_user_country_code", null)
        setPersistedString("access_token", null)
        setPersistedString("refresh_token", null)
        _logoutEvents.tryEmit(Unit)
    }
}
