package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.example.project.data.model.State
import org.example.project.data.model.UserDto
import org.example.project.network.ApiEndpoints
import org.example.project.network.NetworkResult
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
}