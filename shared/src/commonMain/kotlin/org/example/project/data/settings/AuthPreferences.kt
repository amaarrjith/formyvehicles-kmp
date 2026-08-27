package org.example.project.data.settings

open class AuthPreferences {
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var tokenExpiry: Long = 0L

    open fun getAccessToken(): String? = accessToken
    open fun getRefreshToken(): String? = refreshToken

    open fun saveTokens(accessToken: String, refreshToken: String, tokenExpiry: Long) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.tokenExpiry = tokenExpiry
    }

    open fun clearTokens() {
        this.accessToken = null
        this.refreshToken = null
        this.tokenExpiry = 0L
    }
}
