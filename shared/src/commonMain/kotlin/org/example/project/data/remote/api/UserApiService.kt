package org.example.project.data.remote.api

import org.example.project.data.model.State
import org.example.project.data.model.UserDto
import org.example.project.network.NetworkResult

interface UserApiService {
    suspend fun getAllStates(): NetworkResult<List<State>>
    suspend fun getUser(): NetworkResult<UserDto>
}