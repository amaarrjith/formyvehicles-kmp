package org.example.project.data.settings

import kotlinx.serialization.json.Json
import org.example.project.data.model.UserDto
import org.example.project.getPersistedString
import org.example.project.setPersistedString

open class AuthPreferences {
    companion object {
        const val KEY_ACCESS_TOKEN = "auth_access_token"
        const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        const val KEY_TOKEN_EXPIRY = "auth_token_expiry"

        const val KEY_USER = "logged_in_user"
        const val KEY_USER_ID = "logged_in_user_id"
        const val KEY_USER_NAME = "logged_in_user_name"
        const val KEY_USER_MOBILE = "logged_in_user_mobile"
        const val KEY_USER_COUNTRY_CODE = "logged_in_user_country_code"
    }

    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var tokenExpiry: Long = 0L
    private var cachedUser: UserDto? = null

    open fun getAccessToken(): String? {
        if (accessToken != null) return accessToken
        accessToken = getPersistedString(KEY_ACCESS_TOKEN) ?: getPersistedString("access_token")
        return accessToken
    }

    open fun getRefreshToken(): String? {
        if (refreshToken != null) return refreshToken
        refreshToken = getPersistedString(KEY_REFRESH_TOKEN) ?: getPersistedString("refresh_token")
        return refreshToken
    }

    open fun getTokenExpiry(): Long {
        if (tokenExpiry != 0L) return tokenExpiry
        tokenExpiry = getPersistedString(KEY_TOKEN_EXPIRY)?.toLongOrNull() ?: 0L
        return tokenExpiry
    }

    open fun saveTokens(accessToken: String, refreshToken: String, tokenExpiry: Long = 0L) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.tokenExpiry = tokenExpiry

        setPersistedString(KEY_ACCESS_TOKEN, accessToken)
        setPersistedString("access_token", accessToken)

        setPersistedString(KEY_REFRESH_TOKEN, refreshToken)
        setPersistedString("refresh_token", refreshToken)

        setPersistedString(KEY_TOKEN_EXPIRY, tokenExpiry.toString())
    }

    open fun saveUser(user: UserDto) {
        this.cachedUser = user
        try {
            val json = Json { ignoreUnknownKeys = true }
            val userJson = json.encodeToString(UserDto.serializer(), user)
            setPersistedString(KEY_USER, userJson)
        } catch (_: Exception) {}

        user.id?.let { setPersistedString(KEY_USER_ID, it) }
        user.name?.let { setPersistedString(KEY_USER_NAME, it) }
        user.mobileNumber?.let { setPersistedString(KEY_USER_MOBILE, it) }
        user.countryCode?.let { setPersistedString(KEY_USER_COUNTRY_CODE, it) }
    }

    open fun saveUserMobile(mobile: String) {
        setPersistedString(KEY_USER_MOBILE, mobile)
    }

    open fun getUser(): UserDto? {
        if (cachedUser != null) return cachedUser
        val jsonString = getPersistedString(KEY_USER) ?: return null
        return try {
            val user = Json { ignoreUnknownKeys = true }.decodeFromString(UserDto.serializer(), jsonString)
            cachedUser = user
            user
        } catch (_: Exception) {
            null
        }
    }

    open fun getUserName(): String? {
        return getUser()?.name?.takeIf { it.isNotBlank() }
            ?: getPersistedString(KEY_USER_NAME)?.takeIf { it.isNotBlank() }
    }

    open fun getUserMobile(): String? {
        return getUser()?.mobileNumber?.takeIf { it.isNotBlank() }
            ?: getPersistedString(KEY_USER_MOBILE)?.takeIf { it.isNotBlank() }
    }

    open fun getUserId(): String? {
        return getUser()?.id?.takeIf { it.isNotBlank() }
            ?: getPersistedString(KEY_USER_ID)?.takeIf { it.isNotBlank() }
    }

    open fun getUserCountryCode(): String? {
        return getUser()?.countryCode?.takeIf { it.isNotBlank() }
            ?: getPersistedString(KEY_USER_COUNTRY_CODE)?.takeIf { it.isNotBlank() }
    }

    open fun clearUser() {
        this.cachedUser = null
        setPersistedString(KEY_USER, null)
        setPersistedString(KEY_USER_ID, null)
        setPersistedString(KEY_USER_NAME, null)
        setPersistedString(KEY_USER_MOBILE, null)
        setPersistedString(KEY_USER_COUNTRY_CODE, null)
    }

    open fun clearTokens() {
        this.accessToken = null
        this.refreshToken = null
        this.tokenExpiry = 0L

        setPersistedString(KEY_ACCESS_TOKEN, null)
        setPersistedString("access_token", null)

        setPersistedString(KEY_REFRESH_TOKEN, null)
        setPersistedString("refresh_token", null)

        setPersistedString(KEY_TOKEN_EXPIRY, null)
    }

    open fun clearAll() {
        clearTokens()
        clearUser()
    }
}
