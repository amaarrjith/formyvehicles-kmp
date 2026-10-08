package org.example.project.data.remote.api

import org.example.project.data.model.GenericContentRequest
import org.example.project.data.model.GenericContentResponse
import org.example.project.network.NetworkResult

interface GenericApiService {
    suspend fun getGenericContents(request: GenericContentRequest): NetworkResult<GenericContentResponse>
}