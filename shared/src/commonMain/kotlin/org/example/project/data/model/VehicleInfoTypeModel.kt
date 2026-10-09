package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VehicleInfoTypeModel(
    val id: Int,
    val name: String
)

@Serializable
data class VehicleInfoTypesResponse(
    val types: List<VehicleInfoTypeModel> = emptyList()
)
