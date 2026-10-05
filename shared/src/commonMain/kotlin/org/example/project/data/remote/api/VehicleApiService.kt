package org.example.project.data.remote.api

import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleBrandRequest
import org.example.project.data.model.VehicleModel
import org.example.project.data.model.VehicleModelRequest
import org.example.project.data.model.VehicleType
import org.example.project.network.NetworkResult

interface VehicleApiService {
    suspend fun getVehicleTypes(): NetworkResult<List<VehicleType>>
    suspend fun getVehicleBrands(
        request: VehicleBrandRequest
    ): NetworkResult<List<VehicleBrand>>
    suspend fun getVehicleModels(
        request: VehicleModelRequest
    ): NetworkResult<List<VehicleModel>>
}