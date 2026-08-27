package org.example.project.model

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val name: String,
    val isoCode: String,
    val flag: String,
    val dialCode: String
)

@Serializable
data class CountryApiDto(
    val name: String? = null,
    val alpha2Code: String? = null,
    val flag: String? = null,
    val callingCodes: List<String>? = null
)

enum class PhoneValidationState {
    Empty,
    Valid,
    Invalid,
    ParsingError
}

data class CountryPhoneInfo(
    val state: PhoneValidationState,
    val formattedNumber: String,
    val message: String,
    val isMobile: Boolean = true
)
