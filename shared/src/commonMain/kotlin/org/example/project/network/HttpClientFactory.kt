package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.data.model.AuthResponse
import org.example.project.data.model.CommonResponse
import org.example.project.data.model.TokenRefreshRequest
import org.example.project.data.settings.AppPreferences
import org.example.project.data.settings.AuthPreferences
import org.example.project.setPersistedString

internal const val BASE_URL = "http://192.168.1.32:8000/api/"

internal fun HttpClientConfig<*>.commonConfig(authPreferences: AuthPreferences, appPreferences: AppPreferences) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        })
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 30_000
    }

    install(Auth) {
        bearer {
            loadTokens {
                val access = authPreferences.getAccessToken()
                val refresh = authPreferences.getRefreshToken()
                if (!access.isNullOrBlank()) {
                    BearerTokens(access, refresh ?: "")
                } else null
            }
            refreshTokens {
                val refreshToken = authPreferences.getRefreshToken()
                if (refreshToken.isNullOrBlank()) {
                    println("KTOR => No refresh token available, logging out...")
                    org.example.project.manager.AppManager.logout()
                    return@refreshTokens null
                }
                var shouldLogout = false
                try {
                    val response = client.post(BASE_URL + ApiEndpoints.REFRESH_TOKEN) {
                        contentType(ContentType.Application.Json)
                        setBody(TokenRefreshRequest(refreshToken))
                        markAsRefreshTokenRequest()
                    }
                    if (response.status.value == 401 || response.status.value == 400 || response.status.value == 403) {
                        println("KTOR => Refresh token API failed with status ${response.status.value}, triggering logout...")
                        shouldLogout = true
                    } else if (response.status.isSuccess()) {
                        val bodyText = response.bodyAsText()
                        val json = Json { ignoreUnknownKeys = true; isLenient = true }
                        val commonResponse = try {
                            json.decodeFromString<CommonResponse<AuthResponse>>(bodyText)
                        } catch (e: Exception) {
                            null
                        }
                        val authData = commonResponse?.data ?: try {
                            json.decodeFromString<AuthResponse>(bodyText)
                        } catch (e: Exception) {
                            null
                        }

                        val newAccess = authData?.access ?: authData?.accessToken
                        val newRefresh = authData?.refresh ?: authData?.refreshToken ?: refreshToken
                        val newExpiry = authData?.tokenExpiry ?: 0L

                        if (!newAccess.isNullOrBlank()) {
                            authPreferences.saveTokens(newAccess, newRefresh, newExpiry)
                            setPersistedString("access_token", newAccess)
                            setPersistedString("refresh_token", newRefresh)
                            println("KTOR => Token refreshed successfully.")
                            return@refreshTokens BearerTokens(newAccess, newRefresh)
                        } else {
                            println("KTOR => Token refresh response missing access token, logging out...")
                            shouldLogout = true
                        }
                    } else {
                        shouldLogout = true
                    }
                } catch (e: Exception) {
                    println("KTOR => Token refresh failed: ${e.message}")
                    shouldLogout = true
                }
                if (shouldLogout) {
                    org.example.project.manager.AppManager.logout()
                }
                null
            }
            sendWithoutRequest { true }
        }
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("KTOR => $message")
            }
        }
        level = LogLevel.ALL
    }

    defaultRequest {
        url(BASE_URL)
        header("Language", appPreferences.getLanguage())
        header("ngrok-skip-browser-warning", true)
    }
}

expect fun createHttpClient(authPreferences: AuthPreferences, appPreferences: AppPreferences): HttpClient
