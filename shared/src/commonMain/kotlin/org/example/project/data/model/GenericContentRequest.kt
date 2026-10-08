package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenericContentRequest(
    val type: Int
) {

}

@Serializable
data class GenericContentResponse(
    val content: String
)