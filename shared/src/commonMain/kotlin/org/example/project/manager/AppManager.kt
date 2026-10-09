package org.example.project.manager

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.example.project.ToastType
import org.example.project.data.settings.AuthPreferences
import org.example.project.setGuestUser
import org.example.project.setPersistedString
import org.example.project.setUserLoggedIn

data class LogoutEvent(
    val title: String = "Session Expired",
    val message: String = "Your session has expired. Please log in again.",
    val type: ToastType = ToastType.WARNING
)

object AppManager {
    private var isHandlingLogout = false
    private val _logoutEvents = MutableSharedFlow<LogoutEvent>(extraBufferCapacity = 1)
    val logoutEvents: SharedFlow<LogoutEvent> = _logoutEvents.asSharedFlow()

    fun logout(
        title: String = "Session Expired",
        message: String = "Your session has expired. Please log in again.",
        type: ToastType = ToastType.WARNING
    ) {
        if (isHandlingLogout) return
        isHandlingLogout = true
        println("[AppManager] User logged out: $message")
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
        _logoutEvents.tryEmit(LogoutEvent(title = title, message = message, type = type))
    }

    fun resetLogoutState() {
        isHandlingLogout = false
    }
}
