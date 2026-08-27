package org.example.project.phone

import io.michaelrocks.libphonenumber.kotlin.PhoneNumberUtil
import io.michaelrocks.libphonenumber.kotlin.Phonenumber
import io.michaelrocks.libphonenumber.kotlin.metadata.defaultMetadataLoader
import org.example.project.model.CountryPhoneInfo
import org.example.project.model.PhoneValidationState

class PhoneNumberValidator(
    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.createInstance(defaultMetadataLoader())
) {
    fun validate(rawNumber: String, countryIsoCode: String): CountryPhoneInfo {
        val cleanNumber = rawNumber.trim()
        if (cleanNumber.isEmpty()) {
            return CountryPhoneInfo(
                state = PhoneValidationState.Empty,
                formattedNumber = "",
                message = "",
                isMobile = true
            )
        }

        return try {
            val parsedNumber: Phonenumber.PhoneNumber = phoneUtil.parse(cleanNumber, countryIsoCode.uppercase())
            val isValid = phoneUtil.isValidNumberForRegion(parsedNumber, countryIsoCode.uppercase())
            
            if (isValid) {
                val formatted = phoneUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
                val numberType = phoneUtil.getNumberType(parsedNumber)
                val isMobile = numberType == PhoneNumberUtil.PhoneNumberType.MOBILE ||
                        numberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE ||
                        numberType == PhoneNumberUtil.PhoneNumberType.UNKNOWN

                CountryPhoneInfo(
                    state = PhoneValidationState.Valid,
                    formattedNumber = formatted,
                    message = "✓ Valid mobile number",
                    isMobile = isMobile
                )
            } else {
                CountryPhoneInfo(
                    state = PhoneValidationState.Invalid,
                    formattedNumber = cleanNumber,
                    message = "Please enter a valid mobile number",
                    isMobile = false
                )
            }
        } catch (e: Exception) {
            CountryPhoneInfo(
                state = PhoneValidationState.ParsingError,
                formattedNumber = cleanNumber,
                message = "Invalid phone number format",
                isMobile = false
            )
        }
    }

    fun format(rawNumber: String, countryIsoCode: String): String {
        return try {
            val parsed = phoneUtil.parse(rawNumber.trim(), countryIsoCode.uppercase())
            if (phoneUtil.isValidNumberForRegion(parsed, countryIsoCode.uppercase())) {
                phoneUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
            } else {
                rawNumber
            }
        } catch (e: Exception) {
            rawNumber
        }
    }
}
