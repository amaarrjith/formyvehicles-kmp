package org.example.project.data.model

import kotlinx.serialization.Serializable


@Serializable
data class State(
    val id: Int,
    val name: String,
    val code: String
)