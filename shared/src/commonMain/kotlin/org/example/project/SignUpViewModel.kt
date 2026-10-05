package org.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.State
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.UserRepository
import org.example.project.network.NetworkResult

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    init {
        getAllStates()
    }
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onSelectState(state: String) {
        _uiState.update { it.copy(selectedState = state) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onTermsAcceptedChange(accepted: Boolean) {
        _uiState.update { it.copy(isTermsAccepted = accepted) }
    }

    fun onSignUpClick(onSuccess: (String) -> Unit) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState.name.isEmpty()) {
                _uiState.update { it.copy(error = "Name is Required", isLoading = false) }
                return@launch
            }
            if (currentState.selectedState.isNullOrEmpty()) {
                _uiState.update { it.copy(error = "State is Required", isLoading = false) }
                return@launch
            }
            if (currentState.phone.isEmpty()) {
                _uiState.update { it.copy(error = "Phone is Required", isLoading = false) }
                return@launch
            }
            val selectedStateObj = currentState.states.find { it.name == currentState.selectedState }
            val stateId = selectedStateObj?.id?.toString() ?: "1c9f59df-effe-4166-82e5-a2655f84c119"
            val stateName = selectedStateObj?.name ?: currentState.selectedState ?: "Kerala"

            val result = authRepository.register(
                name = currentState.name,
                stateId = stateId,
                state = stateName,
                countryCode = "+91",
                mobileNumber = currentState.phone,
                agreedTerms = currentState.isTermsAccepted
            )
            when (result) {
                is NetworkResult.Success -> {
                    val mobile = result.data.user?.mobileNumber?.takeIf { it.isNotBlank() } ?: currentState.phone
                    setPersistedString("logged_in_user_mobile", mobile)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess(mobile)
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(error = result.message, isLoading = false) }
                }
            }
        }
    }

    fun getAllStates() {
        viewModelScope.launch {
            val result = userRepository.getAllStates()
            when(result) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(states = result.data) }
                }
                is NetworkResult.Error -> {
                    println("Failed to fetch states: ${result.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isTermsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val selectedState: String? = null,
    val states: List<State> = emptyList()
)
