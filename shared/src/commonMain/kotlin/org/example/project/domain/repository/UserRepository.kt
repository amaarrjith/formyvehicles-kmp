package org.example.project.domain.repository

import org.example.project.data.model.State
import org.example.project.data.model.UserDto
import org.example.project.network.NetworkResult

interface UserRepository {
    suspend fun getAllStates(): NetworkResult<List<State>>
    suspend fun getUser(): NetworkResult<UserDto>
}