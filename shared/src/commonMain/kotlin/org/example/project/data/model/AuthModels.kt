package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String? = null,
    val mobileNumber: String? = null,
    val dialCode: String? = null,
    val password: String? = null
)

@Serializable
data class LoginResponse(
    val access: String? = null,
    val refresh: String? = null,
    val tokenExpiry: Long? = null,
    val user: UserDto? = null
)

@Serializable
data class RegisterRequest(
    val name: String,
    val state: String,
    val countryCode: String,
    val mobileNumber: String,
    val agreedTerms: Boolean? = null,
)

@Serializable
data class RegisterResponse(
    val tempUserId: Int? = null,
    val email: String? = null,
    val message: String? = null
)

@Serializable
data class OTPRequest(
    val tempUserId: Int? = null,
    val email: String? = null,
    val otp: String
)

@Serializable
data class OTPResponse(
    val access: String? = null,
    val refresh: String? = null,
    val user: UserDto? = null
)

@Serializable
data class TokenRefreshRequest(
    val refreshToken: String
)

@Serializable
data class AuthResponse(
    val access: String? = null,
    val refresh: String? = null,
    val tokenExpiry: Long? = null,
    val user: UserDto? = null
)

@Serializable
data class ForgetPasswordRequest(
    val email: String
)

@Serializable
data class ForgetPasswordResponse(
    val message: String? = null
)

@Serializable
data class UserDto(
    val id: Int? = null,
    val name: String? = null,
    val email: String? = null,
    val mobileNumber: String? = null,
    val company: String? = null,
    val designation: String? = null
)
