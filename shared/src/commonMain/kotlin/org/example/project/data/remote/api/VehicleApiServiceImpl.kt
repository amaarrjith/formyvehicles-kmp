package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleBrandRequest
import org.example.project.data.model.VehicleModel
import org.example.project.data.model.VehicleModelRequest
import org.example.project.data.model.VehicleType
import org.example.project.network.ApiEndpoints
import org.example.project.network.NetworkResult
import org.example.project.network.jsonBody
import org.example.project.network.safeApiCall

class VehicleApiServiceImpl(
    private val httpClient: HttpClient
): VehicleApiService {
    override suspend fun getVehicleTypes(): NetworkResult<List<VehicleType>> = safeApiCall{
       httpClient.get(ApiEndpoints.VEHICLE_TYPES)
    }

    override suspend fun getVehicleBrands(request: VehicleBrandRequest): NetworkResult<List<VehicleBrand>> = safeApiCall{
        httpClient.get(ApiEndpoints.VEHICLE_BRANDS) {
            jsonBody(request)
        }
    }

    override suspend fun getVehicleModels(request: VehicleModelRequest): NetworkResult<List<VehicleModel>> = safeApiCall{
        httpClient.get(ApiEndpoints.VEHICLE_MODELS) {
            jsonBody(request)
        }
    }

}