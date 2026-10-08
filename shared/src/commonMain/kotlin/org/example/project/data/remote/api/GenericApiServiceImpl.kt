package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.example.project.data.model.GenericContentRequest
import org.example.project.data.model.GenericContentResponse
import org.example.project.network.ApiEndpoints
import org.example.project.network.NetworkResult
import org.example.project.network.jsonBody
import org.example.project.network.safeApiCall

class GenericApiServiceImpl(
    private val httpClient: HttpClient
): GenericApiService {
    override suspend fun getGenericContents(request: GenericContentRequest): NetworkResult<GenericContentResponse> =
        safeApiCall {
            httpClient.get(ApiEndpoints.GENERIC_CONTENT) {
                jsonBody(request)
            }
        }
}