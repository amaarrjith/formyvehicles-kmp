package org.example.project.domain.repository

import org.example.project.ToastType
import org.example.project.data.model.GenericContentRequest
import org.example.project.data.model.GenericContentResponse
import org.example.project.network.NetworkResult

interface GenericRepository {
    suspend fun fetchGenericContent(type: Int): NetworkResult<GenericContentResponse>
}