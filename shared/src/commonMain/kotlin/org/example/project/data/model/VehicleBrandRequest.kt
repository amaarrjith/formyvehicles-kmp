package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class VehicleBrandRequest(
    @SerialName("vehicle_type_id")
    val vehicleTypeId: Int
)