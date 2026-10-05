package org.example.project.data.repository

import org.example.project.data.model.AddVehicleRequest
import org.example.project.data.model.State
import org.example.project.data.model.UserDto
import org.example.project.data.model.UserVehicle
import org.example.project.data.remote.api.UserApiService
import org.example.project.domain.repository.UserRepository
import org.example.project.network.NetworkResult

class UserRepositoryImpl(
    private val userApiService: UserApiService
) : UserRepository {
    override suspend fun getAllStates(): NetworkResult<List<State>> {
        return userApiService.getAllStates()
    }

    override suspend fun getUser(): NetworkResult<UserDto> {
        return userApiService.getUser()
    }

    override suspend fun getUserVehicles(): NetworkResult<List<UserVehicle>> {
        return userApiService.getUserVehicles()
    }

    override suspend fun addUserVehicles(
        registrationNumber: String,
        vehicleTypeId: Int,
        brandTypeId: Int,
        modelTypeId: Int,
        year: String,
        fuelType: String,
        gearType: String
    ): NetworkResult<String> {
        return userApiService.addUserVehicle(
            request = AddVehicleRequest(
                registrationNumber = registrationNumber,
                vehicleTypeId = vehicleTypeId,
                brandTypeId = brandTypeId,
                modelTypeId = modelTypeId,
                year = year,
                fuelType = fuelType,
                gearType = gearType
            )
        )
    }
}