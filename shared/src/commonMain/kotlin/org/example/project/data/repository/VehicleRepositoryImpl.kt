package org.example.project.data.repository
import org.example.project.VehicleStatus
import org.example.project.VehicleStatusRequest
import org.example.project.VehicleStatusResponse
import org.example.project.data.model.AddVehicleInfoRequest
import org.example.project.data.model.AddVehicleInfoResponse
import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleBrandRequest
import org.example.project.data.model.VehicleInfoTypeModel
import org.example.project.data.model.VehicleInfoTypesResponse
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

    override suspend fun addVehicleInfo(
        title: String,
        vehicleId: Int,
        typeId: Int,
        date: String,
        cycle: Int
    ): NetworkResult<AddVehicleInfoResponse> {
        return vehicleApiService.addVehicleInfo(
            request = AddVehicleInfoRequest(
                title,
                vehicleId,
                typeId,
                date,
                cycle
            )
        )
    }

    override suspend fun getVehicleInfoTypes(): NetworkResult<VehicleInfoTypesResponse> {
        return vehicleApiService.getVehicleInfoTypes()
    }

    override suspend fun getVehicleStatuses(registrationNumber: Int?): NetworkResult<VehicleStatusResponse> {
        return vehicleApiService.getVehicleStatuses(
            VehicleStatusRequest(
                registrationNumber
            )
        )
    }
}