package org.example.project.domain.repository

import org.example.project.data.model.AddVehicleResponse
import org.example.project.data.model.State
import org.example.project.data.model.UserDto
import org.example.project.data.model.UserVehicle
import org.example.project.network.NetworkResult

interface UserRepository {
    suspend fun getAllStates(): NetworkResult<List<State>>
    suspend fun getUser(): NetworkResult<UserDto>
    suspend fun getUserVehicles(): NetworkResult<List<UserVehicle>>

    suspend fun addUserVehicles(
        registrationNumber: String,
        vehicleTypeId: Int,
        brandTypeId: Int,
        modelTypeId: Int,
        year: String,
        fuelType: String,
        gearType: String
    ): NetworkResult<AddVehicleResponse>
}