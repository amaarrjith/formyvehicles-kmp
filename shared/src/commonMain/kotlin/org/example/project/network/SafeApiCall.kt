package org.example.project.network

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.example.project.data.model.CommonResponse

fun HttpRequestBuilder.jsonBody(body: Any) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

suspend inline fun <reified T> safeApiCall(
    crossinline call: suspend () -> HttpResponse
): NetworkResult<T> {
    return try {
        val response = call()
        val status = response.status
        when {
            status.isSuccess() -> {
                val bodyText = response.bodyAsText()
                val json = Json { ignoreUnknownKeys = true; isLenient = true }
                val jsonElement = try {
                    json.parseToJsonElement(bodyText)
                } catch (e: Exception) {
                    null
                }

                if (jsonElement == null || jsonElement !is JsonObject) {
                    return NetworkResult.Error(
                        message = "Failed to parse server response: Invalid JSON payload.",
                        type = ErrorType.UNKNOWN
                    )
                }

                val hasError = jsonElement.jsonObject["hasError"]?.jsonPrimitive?.booleanOrNull == true
                if (hasError) {
                    val errorCode = jsonElement.jsonObject["errorCode"]?.jsonPrimitive?.intOrNull
                    val message = jsonElement.jsonObject["message"]?.jsonPrimitive?.contentOrNull ?: "Something went wrong"
                    NetworkResult.Error(
                        message = message,
                        type = mapErrorCodeToType(errorCode),
                        errorCode = errorCode
                    )
                } else {
                    val body = try {
                        json.decodeFromString<CommonResponse<T>>(bodyText)
                    } catch (e: Exception) {
                        return NetworkResult.Error(
                            message = "Response data parsing error: Unable to map server response.",
                            type = ErrorType.UNKNOWN
                        )
                    }
                    val data = body.response
                    if (data != null) {
                        NetworkResult.Success(data)
                    } else {
                        NetworkResult.Error(
                            message = body.message ?: "Empty response body",
                            type = ErrorType.UNKNOWN
                        )
                    }
                }
            }
            status.value == 401 -> {
                NetworkResult.Error("Unauthorized (401)", ErrorType.UNAUTHORIZED, 401)
            }
            status.value == 404 -> {
                NetworkResult.Error("Requested endpoint not found (404)", ErrorType.UNKNOWN, 404)
            }
            status.value == 422 -> {
                val errorMsg = try {
                    response.body<CommonResponse<T>>().message ?: "Validation failed"
                } catch (e: Exception) {
                    "Validation failed"
                }
                NetworkResult.Error(errorMsg, ErrorType.VALIDATION, 422)
            }
            status.value >= 500 -> {
                NetworkResult.Error("Server error (${status.value})", ErrorType.SERVER, status.value)
            }
            else -> {
                NetworkResult.Error("HTTP Error: ${status.value}", ErrorType.UNKNOWN, status.value)
            }
        }
    } catch (e: Throwable) {
        val message = parseErrorMessage(e)
        val type = parseErrorType(e)
        NetworkResult.Error(message = message, type = type)
    }
}

fun parseErrorMessage(e: Throwable): String {
    val msg = e.message ?: ""
    return when {
        e is kotlinx.serialization.SerializationException || msg.contains("SerializationException", ignoreCase = true) -> {
            "Response parsing error: Invalid server JSON format."
        }
        msg.contains("-1003") || msg.contains("hostname could not be found", ignoreCase = true) || msg.contains("UnknownHostException", ignoreCase = true) || msg.contains("UnresolvedAddressException", ignoreCase = true) -> {
            "Server hostname could not be found. Please check your network connection or server URL."
        }
        msg.contains("-1009") || msg.contains("internet connection", ignoreCase = true) || msg.contains("offline", ignoreCase = true) -> {
            "Network error: Please check your internet connection."
        }
        msg.contains("-1001") || msg.contains("timed out", ignoreCase = true) || e is io.ktor.client.plugins.HttpRequestTimeoutException -> {
            "Request timed out. Please try again."
        }
        else -> {
            if (msg.startsWith("Exception in http request:")) {
                val localizedDescRegex = Regex("""NSLocalizedDescription=([^,]+)""")
                val match = localizedDescRegex.find(msg)
                match?.groupValues?.get(1)?.trim() ?: "A network error occurred."
            } else if (msg.isNotBlank()) {
                msg
            } else {
                "An unexpected network error occurred."
            }
        }
    }
}

fun parseErrorType(e: Throwable): ErrorType {
    val msg = e.message ?: ""
    return when {
        msg.contains("-1001") || msg.contains("timed out", ignoreCase = true) -> ErrorType.TIMEOUT
        msg.contains("-1003") || msg.contains("hostname could not be found", ignoreCase = true) || msg.contains("UnknownHostException", ignoreCase = true) -> ErrorType.NETWORK
        else -> ErrorType.NETWORK
    }
}

fun mapErrorCodeToType(errorCode: Int?): ErrorType {
    return when (errorCode) {
        401 -> ErrorType.UNAUTHORIZED
        422 -> ErrorType.VALIDATION
        in 500..599 -> ErrorType.SERVER
        else -> ErrorType.UNKNOWN
    }
}
