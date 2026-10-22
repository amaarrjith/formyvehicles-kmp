package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.model.Country
import org.example.project.network.NetworkResult

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val authPreferences: AuthPreferences = AuthPreferences()
) : BaseViewModel() {
    var mobileNumber by mutableStateOf("")
    var selectedCountry by mutableStateOf(Country("India", "IN", "🇮🇳", "+91"))
    var countryCode: String
        get() = selectedCountry.dialCode
        set(value) {
            selectedCountry = selectedCountry.copy(dialCode = value)
        }
    var dropdownExpanded by mutableStateOf(false)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    fun onContinueClick(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            if (mobileNumber.trim().isEmpty()) {
                showErrorToast("Please enter your mobile number.", "Login")
                _uiState.value = LoginUiState(isLoading = false)
            } else {
                val result = authRepository.login(
                    countryCode = countryCode,
                    mobileNumber = mobileNumber.trim()
                )
                when(result) {
                    is NetworkResult.Success -> {
                        _uiState.value = LoginUiState(isLoading = false)
                        val user = result.data.user
                        if (user != null) {
                            authPreferences.saveUser(user)
                        }
                        val accessToken = result.data.access
                        val refreshToken = result.data.refresh
                        if (!accessToken.isNullOrBlank()) {
                            authPreferences.saveTokens(
                                accessToken = accessToken,
                                refreshToken = refreshToken ?: "",
                                tokenExpiry = result.data.tokenExpiry ?: 3600
                            )
                        }
                        val mobile = user?.mobileNumber?.takeIf { it.isNotBlank() } ?: mobileNumber.trim()
                        authPreferences.saveUserMobile(mobile)
                        onSuccess(mobile)
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = LoginUiState(isLoading = false)
                        showErrorToast(result.message, "Login Failed")
                    }
                }
            }
        }
    }
}
data class LoginUiState(
    val isLoading: Boolean = false
)
