package org.example.project.data.remote.api

import org.example.project.VehicleStatusRequest
import org.example.project.data.model.AddVehicleInfoRequest
import org.example.project.data.model.AddVehicleInfoResponse
import org.example.project.data.model.GenericContentRequest
import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleBrandRequest
import org.example.project.data.model.VehicleInfoTypesResponse
import org.example.project.data.model.VehicleModel
import org.example.project.data.model.VehicleModelRequest
import org.example.project.VehicleStatusResponse
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
    suspend fun addVehicleInfo(
        request: AddVehicleInfoRequest
    ): NetworkResult<AddVehicleInfoResponse>
    suspend fun getVehicleInfoTypes(): NetworkResult<VehicleInfoTypesResponse>
    suspend fun getVehicleStatuses(
        request: VehicleStatusRequest
    ): NetworkResult<VehicleStatusResponse>
}