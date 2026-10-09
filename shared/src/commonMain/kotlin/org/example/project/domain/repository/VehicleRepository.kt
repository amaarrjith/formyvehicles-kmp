package org.example.project.domain.repository

import org.example.project.VehicleStatus
import org.example.project.VehicleStatusResponse
import org.example.project.data.model.AddVehicleInfoResponse
import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleInfoTypeModel
import org.example.project.data.model.VehicleInfoTypesResponse
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

    suspend fun addVehicleInfo(
        title: String,
        vehicleId: Int,
        typeId: Int,
        date: String,
        cycle: Int
    ) : NetworkResult<AddVehicleInfoResponse>

    suspend fun getVehicleInfoTypes(): NetworkResult<VehicleInfoTypesResponse>

    suspend fun getVehicleStatuses(
        registrationNumber: Int? = null
    ): NetworkResult<VehicleStatusResponse>
}