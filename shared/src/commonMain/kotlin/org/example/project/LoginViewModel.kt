package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.model.Country
import org.example.project.network.NetworkResult

class LoginViewModel(
    private val authRepository: AuthRepository
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
                        val mobile = result.data.user?.mobileNumber?.takeIf { it.isNotBlank() } ?: mobileNumber.trim()
                        setPersistedString("logged_in_user_mobile", mobile)
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
