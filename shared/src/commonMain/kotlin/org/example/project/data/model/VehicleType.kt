package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VehicleType(
    val id: Int,
    val name: String
)