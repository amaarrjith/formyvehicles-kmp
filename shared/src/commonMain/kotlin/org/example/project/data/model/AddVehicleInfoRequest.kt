package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddVehicleInfoRequest(
    val title: String,
    @SerialName("vehicle_id")
    val vehicleId: Int,
    @SerialName("type_id")
    val typeId: Int,
    val date: String, // dd-MM-yyyy
    val cycle: Int // 1: One Time, 2: Every Week, 3: Every Month, 4: Every Year
)

@Serializable
data class AddVehicleInfoResponse(
    val isSuccess: Boolean,
    val statusMessage: String
)