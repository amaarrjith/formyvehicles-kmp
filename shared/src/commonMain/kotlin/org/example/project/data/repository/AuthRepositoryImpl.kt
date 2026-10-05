package org.example.project.data.repository

import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.ForgetPasswordRequest
import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.OTPRequest
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.RegisterRequest
import org.example.project.data.model.RegisterResponse
import org.example.project.data.remote.api.AuthApiService
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

class AuthRepositoryImpl(
    private val apiService: AuthApiService
) : AuthRepository {

    override suspend fun login(
        countryCode: String,
        mobileNumber: String
    ): NetworkResult<LoginResponse> {
        return apiService.login(
            LoginRequest(
                countryCode = countryCode,
                mobileNumber = mobileNumber
            )
        )
    }

    override suspend fun forgetPassword(
        email: String
    ): NetworkResult<ForgetPasswordResponse> {
        return apiService.forgetPassword(
            ForgetPasswordRequest(
                email = email
            )
        )
    }

    override suspend fun register(
        name: String,
        stateId: String,
        state: String,
        countryCode: String,
        mobileNumber: String?,
        agreedTerms: Boolean?
    ): NetworkResult<RegisterResponse> {
        return apiService.register(
            request = RegisterRequest(
                name = name,
                stateId = stateId,
                state = state,
                countryCode = countryCode,
                mobileNumber = mobileNumber ?: "",
                agreedTerms = agreedTerms
            )

        )
    }

    override suspend fun verifyOTP(
        mobileNumber: String,
        code: String
    ): NetworkResult<OTPResponse> {
        return apiService.verifyOTP(
            OTPRequest(
                code = code,
                mobileNumber = mobileNumber
            )
        )
    }

    override suspend fun logout(): NetworkResult<CommonModelResponse> {
        return apiService.logout()
    }
}
