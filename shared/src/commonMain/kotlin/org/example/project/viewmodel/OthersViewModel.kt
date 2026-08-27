package org.example.project.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.location.LocationManager
import org.example.project.location.UserAddress

class OthersViewModel(
    private val locationManager: LocationManager
) : ViewModel() {

    var userAddress by mutableStateOf<UserAddress?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchLocation()
    }

    fun fetchLocation() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val address = locationManager.getCurrentAddress()
                userAddress = address
                if (address == null) {
                    errorMessage = "Location unavailable or permissions denied"
                } else {
                    println("Area: ${address.area}")
                    println("City: ${address.city}")
                    println("District: ${address.district}")
                    println("State: ${address.state}")
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to get location"
            } finally {
                isLoading = false
            }
        }
    }
}
