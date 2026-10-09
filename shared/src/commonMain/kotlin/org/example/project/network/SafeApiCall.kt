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
        val bodyText = response.bodyAsText()

        if (bodyText.isBlank()) {
            return if (status.isSuccess()) {
                val data = try {
                    Json.decodeFromString<T>("{}")
                } catch (e: Exception) {
                    null
                }
                if (data != null) NetworkResult.Success(data)
                else NetworkResult.Error(
                    message = "Empty response body.",
                    type = ErrorType.UNKNOWN,
                    errorCode = status.value
                )
            } else {
                NetworkResult.Error(
                    message = when (status.value) {
                        401 -> "Unauthorized"
                        404 -> "Requested endpoint not found"
                        422 -> "Validation failed"
                        in 500..599 -> "Server error (${status.value})"
                        else -> "Request failed (${status.value})"
                    },
                    type = mapErrorCodeToType(status.value),
                    errorCode = status.value
                )
            }
        }

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        val jsonElement = try {
            json.parseToJsonElement(bodyText)
        } catch (e: Exception) {
            null
        }

        val jsonObject = jsonElement as? JsonObject
        val backendMessage = extractBackendErrorMessage(jsonObject)
            ?: if (!bodyText.trim().startsWith("<") && bodyText.isNotBlank() && bodyText.length < 300) bodyText.trim() else null

        val hasError = jsonObject?.get("hasError")?.jsonPrimitive?.booleanOrNull == true

        if (hasError || !status.isSuccess()) {
            val errorCode = jsonObject?.get("errorCode")?.jsonPrimitive?.intOrNull ?: status.value
            val message = when (status.value) {
                404 -> "Requested endpoint not found"
                else -> backendMessage ?: when (status.value) {
                    401 -> "Unauthorized"
                    422 -> "Validation failed"
                    in 500..599 -> "Server error (${status.value})"
                    else -> "Something went wrong"
                }
            }
            return NetworkResult.Error(
                message = message,
                type = mapErrorCodeToType(errorCode),
                errorCode = errorCode
            )
        }

        val body = try {
            json.decodeFromString<CommonResponse<T>>(bodyText)
        } catch (e: Exception) {
            null
        }

        val data = body?.data ?: try {
            json.decodeFromString<T>(bodyText)
        } catch (e: Exception) {
            null
        }

        if (data != null) {
            NetworkResult.Success(data)
        } else {
            NetworkResult.Error(
                message = backendMessage ?: "Unable to process server response.",
                type = ErrorType.UNKNOWN,
                errorCode = status.value
            )
        }

    } catch (e: Throwable) {
        NetworkResult.Error(
            message = parseErrorMessage(e),
            type = parseErrorType(e)
        )
    }
}

fun extractBackendErrorMessage(jsonObject: JsonObject?): String? {
    if (jsonObject == null) return null

    val errorsElement = jsonObject["errors"]
    val specificValidationMsg = when (errorsElement) {
        is JsonObject -> {
            errorsElement.values.firstNotNullOfOrNull { value ->
                when (value) {
                    is kotlinx.serialization.json.JsonArray -> value.firstOrNull()?.jsonPrimitive?.contentOrNull
                    is kotlinx.serialization.json.JsonPrimitive -> value.contentOrNull
                    else -> null
                }
            }
        }
        is kotlinx.serialization.json.JsonArray -> {
            errorsElement.firstOrNull()?.jsonPrimitive?.contentOrNull
        }
        else -> null
    }

    val message = jsonObject["message"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
    val error = jsonObject["error"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
    val detail = jsonObject["detail"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }

    if (message != null && (message.contains("Validation", ignoreCase = true) || message.contains("invalid", ignoreCase = true))) {
        return specificValidationMsg ?: message
    }

    return specificValidationMsg ?: message ?: error ?: detail
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
        msg.contains("Connection refused", ignoreCase = true) ||
                msg.contains("ConnectException", ignoreCase = true) -> {
            "Unable to connect to the server.\nPlease try again later."
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

fun getErrorMessage(throwable: Throwable): String {
    val errorText = generateSequence(throwable) { it.cause }
        .mapNotNull { it.message }
        .joinToString(" ")
        .lowercase()

    return when {
        "connection refused" in errorText ||
                "failed to connect" in errorText ->
            "Unable to connect to the server. Please try again later."

        "timeout" in errorText ->
            "The request timed out. Please try again."

        "unknownhost" in errorText ||
                "no address associated" in errorText ->
            "Please check your internet connection."

        else ->
            "Something went wrong. Please try again."
    }
}