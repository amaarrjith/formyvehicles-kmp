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

fun saveLoggedInUser(user: UserDto) {
    try {
        val json = Json { ignoreUnknownKeys = true }
        val userJson = json.encodeToString(UserDto.serializer(), user)
        setPersistedString("logged_in_user", userJson)
    } catch (_: Exception) {}
    user.id?.let { setPersistedString("logged_in_user_id", it) }
    user.name?.let { setPersistedString("logged_in_user_name", it) }
    user.mobileNumber?.let { setPersistedString("logged_in_user_mobile", it) }
    user.countryCode?.let { setPersistedString("logged_in_user_country_code", it) }
}

fun getLoggedInUserName(): String {
    if (isGuestUser()) return "Guest"
    return getLoggedInUser()?.name?.takeIf { it.isNotBlank() }
        ?: getPersistedString("logged_in_user_name")?.takeIf { it.isNotBlank() }
        ?: ""
}
