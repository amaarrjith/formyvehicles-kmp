package org.example.project.data.repository

import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleBrandRequest
import org.example.project.data.model.VehicleModel
import org.example.project.data.model.VehicleModelRequest
import org.example.project.data.model.VehicleType
import org.example.project.data.remote.api.VehicleApiService
import org.example.project.domain.repository.VehicleRepository
import org.example.project.network.NetworkResult

class VehicleRepositoryImpl(
    private val vehicleApiService: VehicleApiService
): VehicleRepository {
    override suspend fun getVehicleTypes(): NetworkResult<List<VehicleType>> {
        return vehicleApiService.getVehicleTypes()
    }

    override suspend fun getVehicleBrands(vehicleTypeId: Int): NetworkResult<List<VehicleBrand>> {
        return vehicleApiService.getVehicleBrands(
            VehicleBrandRequest(
                vehicleTypeId
            )
        )
    }

    override suspend fun getVehicleModels(
        vehicleTypeId: Int,
        vehicleBrandId: Int
    ): NetworkResult<List<VehicleModel>> {
        return vehicleApiService.getVehicleModels(
            request = VehicleModelRequest(
                vehicleTypeId,
                vehicleBrandId
            )
        )
    }
}