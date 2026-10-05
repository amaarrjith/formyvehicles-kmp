package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddVehicleRequest(
    @SerialName("registration_number")
    val registrationNumber: String,
    @SerialName("vehicle_type_id")
    val vehicleTypeId: Int,
    @SerialName("brand_id")
    val brandTypeId: Int,
    @SerialName("vehicle_model_id")
    val modelTypeId: Int,
    val year: String,
    @SerialName("fuel_type")
    val fuelType: String,
    @SerialName("gear_type")
    val gearType: String
) {
}