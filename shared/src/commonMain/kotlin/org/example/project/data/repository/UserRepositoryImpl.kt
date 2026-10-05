package org.example.project.data.repository

import org.example.project.data.model.State
import org.example.project.data.model.UserDto
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
}