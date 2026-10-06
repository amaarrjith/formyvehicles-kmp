package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable()
data class AddVehicleResponse(
    @SerialName("is_vehicle_added")
    val isVehicleAdded: Boolean,
    val vehicle: UserVehicle

) {
}