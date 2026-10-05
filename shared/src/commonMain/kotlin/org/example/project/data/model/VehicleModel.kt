package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VehicleModel(
    val id: Int,
    val name: String
) {
}