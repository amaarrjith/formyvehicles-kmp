package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserVehicle(
    val id: Int,
    @SerialName("registration_number")
    val registrationNumber: String,
    @SerialName("vehicle_type")
    val vehicleType: VehicleType,
    val brand: VehicleBrand,
    @SerialName("vehicle_model")
    val vehicleModel: VehicleModel,
    val year: String,
    @SerialName("fuel_type")
    val fuelType: String,
    @SerialName("gear_type")
    val gearType: String,
    @SerialName("image_url")
    val imageUrl: String? = null
)
