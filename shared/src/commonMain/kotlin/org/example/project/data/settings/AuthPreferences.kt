package org.example.project.data.settings

import org.example.project.getPersistedString
import org.example.project.setPersistedString

open class AuthPreferences {
    companion object {
        const val KEY_ACCESS_TOKEN = "auth_access_token"
        const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        const val KEY_TOKEN_EXPIRY = "auth_token_expiry"
    }

    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var tokenExpiry: Long = 0L

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
}
