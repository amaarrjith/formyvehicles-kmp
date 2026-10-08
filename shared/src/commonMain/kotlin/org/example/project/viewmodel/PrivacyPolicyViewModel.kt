package org.example.project.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.BaseViewModel
import org.example.project.domain.repository.GenericRepository
import org.example.project.network.NetworkResult

class PrivacyPolicyViewModel(
    private val genericRepository: GenericRepository
): BaseViewModel() {
    private val _uiState = MutableStateFlow<PrivacyUiState>(PrivacyUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _termsUiState = MutableStateFlow<PrivacyUiState>(PrivacyUiState.Idle)
    val termsUiState = _termsUiState.asStateFlow()

    init {
        getPrivacyContent()
        getTermsContent()
    }

    fun getPrivacyContent() {
        viewModelScope.launch {
            _uiState.value = PrivacyUiState.Loading
            val result = genericRepository.fetchGenericContent(
                type = 1
            )
            when(result) {
                is NetworkResult.Success -> {
                    _uiState.value = PrivacyUiState.Success(
                        content = result.data.content
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = PrivacyUiState.Error(
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun getTermsContent() {
        viewModelScope.launch {
            _termsUiState.value = PrivacyUiState.Loading
            val result = genericRepository.fetchGenericContent(
                type = 2
            )
            when(result) {
                is NetworkResult.Success -> {
                    _termsUiState.value = PrivacyUiState.Success(
                        content = result.data.content
                    )
                }
                is NetworkResult.Error -> {
                    _termsUiState.value = PrivacyUiState.Error(
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}

sealed class PrivacyUiState() {
    data object Idle: PrivacyUiState()
    data class Success(
        val content: String
    ): PrivacyUiState()
    data object Loading: PrivacyUiState()
    data class Error(
        val errorMessage: String
    ): PrivacyUiState()
}