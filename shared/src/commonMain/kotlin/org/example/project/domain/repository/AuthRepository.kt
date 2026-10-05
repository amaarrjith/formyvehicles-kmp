package org.example.project.domain.repository

import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.RegisterResponse
import org.example.project.network.NetworkResult

interface AuthRepository {
    suspend fun login(countryCode: String, mobileNumber: String): NetworkResult<LoginResponse>
    suspend fun forgetPassword(email: String): NetworkResult<ForgetPasswordResponse>
    suspend fun register(
        name: String,
        stateId: String,
        state: String,
        countryCode: String,
        mobileNumber: String?,
        agreedTerms: Boolean?
    ): NetworkResult<RegisterResponse>
    suspend fun verifyOTP(mobileNumber: String, code: String): NetworkResult<OTPResponse>
    suspend fun logout(): NetworkResult<CommonModelResponse>
}
