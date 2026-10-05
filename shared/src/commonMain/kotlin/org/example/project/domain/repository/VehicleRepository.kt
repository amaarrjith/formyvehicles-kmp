package org.example.project.domain.repository

import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleModel
import org.example.project.data.model.VehicleType
import org.example.project.network.NetworkResult

interface VehicleRepository {
    suspend fun getVehicleTypes(): NetworkResult<List<VehicleType>>
    suspend fun getVehicleBrands(
        vehicleTypeId: Int
    ): NetworkResult<List<VehicleBrand>>
    suspend fun getVehicleModels(
        vehicleTypeId: Int,
        vehicleBrandId: Int
    ): NetworkResult<List<VehicleModel>>
}