package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.example.project.data.model.GenericContentRequest
import org.example.project.data.model.GenericContentResponse
import org.example.project.data.remote.api.GenericApiService
import org.example.project.domain.repository.GenericRepository
import org.example.project.network.ApiEndpoints
import org.example.project.network.NetworkResult
import org.example.project.network.safeApiCall

class GenericRepositoryImpl(
    private val genericApiService: GenericApiService
): GenericRepository {
    override suspend fun fetchGenericContent(type: Int): NetworkResult<GenericContentResponse> {
        return genericApiService.getGenericContents(
            GenericContentRequest(type)
        )
    }
}