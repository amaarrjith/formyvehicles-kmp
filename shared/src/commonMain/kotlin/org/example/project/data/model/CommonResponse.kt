package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonResponse<T>(
    val hasError: Boolean = false,
    val errorCode: Int? = null,
    val message: String? = null,
    @SerialName("data")
    val data: T? = null
)

@Serializable
data class CommonModelResponse(
    val hasError: Boolean = false,
    val errorCode: Int? = null,
    val message: String? = null,
    @SerialName("data")
    val data: String? = null
)
