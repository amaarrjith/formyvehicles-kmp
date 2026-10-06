package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.BaseViewModel
import org.example.project.model.Country
import org.example.project.model.CountryPhoneInfo
import org.example.project.model.PhoneValidationState
import org.example.project.phone.PhoneNumberValidator
import org.example.project.repository.CountryRepository

sealed interface CountryPickerUiState {
    object Loading : CountryPickerUiState
    data class Success(val countries: List<Country>) : CountryPickerUiState
    data class Error(val message: String) : CountryPickerUiState
    data class EmptySearchResult(val query: String) : CountryPickerUiState
}

class PhoneNumberViewModel(
    private val countryRepository: CountryRepository,
    private val validator: PhoneNumberValidator
) : BaseViewModel() {

    private val _countriesState = MutableStateFlow<CountryPickerUiState>(CountryPickerUiState.Loading)
    val countriesState: StateFlow<CountryPickerUiState> = _countriesState.asStateFlow()

    private val _allCountries = MutableStateFlow<List<Country>>(emptyList())
    val allCountries: StateFlow<List<Country>> = _allCountries.asStateFlow()

    private val defaultIndia = Country("India", "IN", "🇮🇳", "+91")
    private val _selectedCountry = MutableStateFlow<Country>(defaultIndia)
    val selectedCountry: StateFlow<Country> = _selectedCountry.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _validationInfo = MutableStateFlow(
        CountryPhoneInfo(PhoneValidationState.Empty, "", "", true)
    )
    val validationInfo: StateFlow<CountryPhoneInfo> = _validationInfo.asStateFlow()

    init {
        loadCountries()
    }

    fun loadCountries() {
        viewModelScope.launch {
            _countriesState.value = CountryPickerUiState.Loading
            val result = countryRepository.getCountries()
            result.onSuccess { list ->
                _allCountries.value = list
                val defaultCountry = list.firstOrNull { it.isoCode == "IN" } ?: list.firstOrNull() ?: defaultIndia
                _selectedCountry.value = defaultCountry
                filterCountries(_searchQuery.value)
            }.onFailure { error ->
                _countriesState.value = CountryPickerUiState.Error(error.message ?: "Failed to load countries")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterCountries(query)
    }

    private fun filterCountries(query: String) {
        val trimmed = query.trim()
        val currentList = _allCountries.value
        if (currentList.isEmpty()) return

        if (trimmed.isEmpty()) {
            _countriesState.value = CountryPickerUiState.Success(currentList)
        } else {
            val filtered = currentList.filter { country ->
                country.name.contains(trimmed, ignoreCase = true) ||
                country.dialCode.contains(trimmed, ignoreCase = true) ||
                country.isoCode.contains(trimmed, ignoreCase = true)
            }
            if (filtered.isEmpty()) {
                _countriesState.value = CountryPickerUiState.EmptySearchResult(trimmed)
            } else {
                _countriesState.value = CountryPickerUiState.Success(filtered)
            }
        }
    }

    fun selectCountry(country: Country) {
        _selectedCountry.value = country
        validateNumber(_phoneNumber.value, country)
    }

    fun onPhoneNumberChange(newNumber: String) {
        val filteredDigits = newNumber.filter { it.isDigit() || it == ' ' || it == '-' }
        _phoneNumber.value = filteredDigits
        validateNumber(filteredDigits, _selectedCountry.value)
    }

    private fun validateNumber(number: String, country: Country) {
        val info = validator.validate(number, country.isoCode)
        _validationInfo.value = info
    }
}
