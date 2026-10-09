package org.example.project.data.model

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("country_code")
    val countryCode: String? = null,
    @SerialName("mobile_number")
    val mobileNumber: String? = null,
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
    @SerialName("name")
    val name: String,

    @SerialName("state_id")
    val stateId: String,

    @SerialName("state")
    val state: String,

    @SerialName("country_code")
    val countryCode: String,

    @SerialName("mobile_number")
    val mobileNumber: String,

    @SerialName("terms_accepted")
    val agreedTerms: Boolean? = null,
)


@Serializable
data class RegisterResponse(
    val tempUserId: Int? = null,
    val email: String? = null,
    val message: String? = null,
    val user: UserDto? = null
)

@Serializable
data class OTPRequest(
    @SerialName("mobile_number")
    val mobileNumber: String,
    val code: String
)

@Serializable
data class OTPResponse(
    val access: String? = null,
    val refresh: String? = null,
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    val user: UserDto? = null
)

@Serializable
data class TokenRefreshRequest(
    val refresh: String,
    @SerialName("refresh_token")
    val refreshToken: String = refresh
)

@Serializable
data class AuthResponse(
    val access: String? = null,
    val refresh: String? = null,
    @SerialName("access_token")
    val accessToken: String? = null,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
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
data class
UserDto(
    val id: String? = null,
    val name: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    @SerialName("mobile_number")
    val mobileNumber: String? = null,
)


@Serializable
data class UserResponse(
    val user: UserDto
)