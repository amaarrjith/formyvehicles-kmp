package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import org.example.project.data.model.AddVehicleRequest
import org.example.project.data.model.State
import org.example.project.data.model.UserDto
import org.example.project.data.model.UserVehicle
import org.example.project.network.ApiEndpoints
import org.example.project.network.NetworkResult
import org.example.project.network.jsonBody
import org.example.project.network.safeApiCall

class UserApiServiceImpl(
    private val httpClient: HttpClient
) : UserApiService {
    override suspend fun getAllStates(): NetworkResult<List<State>> = safeApiCall {
        httpClient.get(ApiEndpoints.GET_STATES)
    }

    override suspend fun getUser(): NetworkResult<UserDto> = safeApiCall {
        httpClient.get(ApiEndpoints.USER)
    }

    override suspend fun getUserVehicles(): NetworkResult<List<UserVehicle>> = safeApiCall {
        httpClient.get(ApiEndpoints.USER_VEHICLES)
    }

    override suspend fun addUserVehicle(request: AddVehicleRequest): NetworkResult<String> = safeApiCall{
        httpClient.post(ApiEndpoints.USER_VEHICLES) {
            jsonBody(request)
        }
    }
}