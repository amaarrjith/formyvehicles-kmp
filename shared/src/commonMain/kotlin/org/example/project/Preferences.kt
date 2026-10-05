package org.example.project

import kotlinx.serialization.json.Json
import org.example.project.data.model.UserDto

expect fun isUserLoggedIn(): Boolean
expect fun setUserLoggedIn(isLoggedIn: Boolean)

expect fun isGuestUser(): Boolean
expect fun setGuestUser(isGuest: Boolean)

expect fun getPersistedString(key: String): String?
expect fun setPersistedString(key: String, value: String?)

fun getLoggedInUser(): UserDto? {
    val jsonString = getPersistedString("logged_in_user") ?: return null
    return try {
        Json { ignoreUnknownKeys = true }.decodeFromString(UserDto.serializer(), jsonString)
    } catch (e: Exception) {
        null
    }
}
