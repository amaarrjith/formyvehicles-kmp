package org.example.project.data.remote.api

import org.example.project.data.model.AddVehicleRequest
import org.example.project.data.model.AddVehicleResponse
import org.example.project.data.model.State
import org.example.project.data.model.UserDto
import org.example.project.data.model.UserResponse
import org.example.project.data.model.UserVehicle
import org.example.project.network.NetworkResult

interface UserApiService {
    suspend fun getAllStates(): NetworkResult<List<State>>
    suspend fun getUser(): NetworkResult<UserResponse>

    suspend fun getUserVehicles(): NetworkResult<List<UserVehicle>>
    suspend fun addUserVehicle(
        request: AddVehicleRequest
    ): NetworkResult<AddVehicleResponse>
}