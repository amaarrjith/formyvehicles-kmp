package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleModelRequest(
    @SerialName("vehicle_type_id")
    val vehicleTypeId: Int,
    @SerialName("brand_id")
    val vehicleBrandId: Int
)