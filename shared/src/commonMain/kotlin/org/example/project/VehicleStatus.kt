package org.example.project

import kotlinx.serialization.Serializable
import org.example.project.data.model.VehicleType

import kotlinx.serialization.SerialName

@Serializable
data class VehicleStatus(
    val id: Int,
    val type: VehicleType,
    val title: String,
    val date: String,
    @SerialName("vehicle_model")
    val vehicleModel: String? = null,
    @SerialName("vehicle_name")
    val vehicleName: String? = null,
    @SerialName("reminder_time")
    val reminderTime: String? = null
)

@Serializable
data class VehicleStatusResponse(
    val status: List<VehicleStatus> = emptyList()
)

@Serializable
data class VehicleStatusRequest(
    val id: Int? = null
)