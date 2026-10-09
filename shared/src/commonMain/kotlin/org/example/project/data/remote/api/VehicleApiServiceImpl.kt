package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import org.example.project.VehicleStatusRequest
import org.example.project.VehicleStatusResponse
import org.example.project.data.model.AddVehicleInfoRequest
import org.example.project.data.model.AddVehicleInfoResponse
import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleBrandRequest
import org.example.project.data.model.VehicleInfoTypesResponse
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

    override suspend fun addVehicleInfo(request: AddVehicleInfoRequest): NetworkResult<AddVehicleInfoResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.ADD_VEHICLE_INFO) {
            jsonBody(request)
        }
    }

    override suspend fun getVehicleInfoTypes(): NetworkResult<VehicleInfoTypesResponse> = safeApiCall {
        httpClient.get(ApiEndpoints.VEHICLE_INFO_TYPES)
    }

    override suspend fun getVehicleStatuses(request: VehicleStatusRequest): NetworkResult<VehicleStatusResponse> = safeApiCall {
        httpClient.get(ApiEndpoints.VEHICLE_INFO_LIST) {
            jsonBody(request)
        }
    }
}