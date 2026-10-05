package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.model.Country
import org.example.project.network.NetworkResult

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    var mobileNumber by mutableStateOf("")
    var selectedCountry by mutableStateOf(Country("India", "IN", "🇮🇳", "+91"))
    var countryCode: String
        get() = selectedCountry.dialCode
        set(value) {
            selectedCountry = selectedCountry.copy(dialCode = value)
        }
    var dropdownExpanded by mutableStateOf(false)
    var showToast by mutableStateOf(false)
    var toastMessage by mutableStateOf("Please enter your mobile number.")

    fun onContinueClick(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            if (mobileNumber.trim().isEmpty()) {
                toastMessage = "Please enter your mobile number."
                showToast = true
            } else {
                val result = authRepository.login(
                    countryCode = countryCode,
                    mobileNumber = mobileNumber.trim()
                )
                when(result) {
                    is NetworkResult.Success -> {
                        val mobile = result.data.user?.mobileNumber?.takeIf { it.isNotBlank() } ?: mobileNumber.trim()
                        setPersistedString("logged_in_user_mobile", mobile)
                        onSuccess(mobile)
                    }
                    is NetworkResult.Error -> {
                        toastMessage = result.message
                        showToast = true
                    }
                }
            }
        }
    }
}
