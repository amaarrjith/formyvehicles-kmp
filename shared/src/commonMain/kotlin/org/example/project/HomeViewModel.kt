package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.data.model.UserDto
import org.example.project.domain.repository.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    var currentUser by mutableStateOf<UserDto?>(null)

    // Read activeMobile dynamically every time so it picks up the latest logged-in user
    val activeMobile: String
        get() = currentUser?.mobileNumber
            ?: getLoggedInUser()?.mobileNumber
            ?: getPersistedString("logged_in_user_mobile")
            ?: ""

    val vehicleTypes = emptyList<String>()
    val fuelTypes = emptyList<String>()
    val gearTypes = emptyList<String>()

    val vehicleColors: List<String> = emptyList<String>()
    val vehicleCategories: List<String> = emptyList<String>()
    val seatingCapacities: List<String> = emptyList<String>()

    var vehicleList by mutableStateOf<List<Vehicle>>(
        emptyList()
    )
    var userName by mutableStateOf("")
    var uiState by mutableStateOf<HomeUiState>(HomeUiState.Loading)
    var isSheetOpen by mutableStateOf(false)

    // Form inputs initialized to empty to make them mandatory
    var regNumber by mutableStateOf("")
    var selectedVehicleType by mutableStateOf("")
    var selectedBrand by mutableStateOf("")
    var selectedModel by mutableStateOf("")
    var selectedYear by mutableStateOf("")
    var selectedFuelType by mutableStateOf("")
    var selectedGearType by mutableStateOf("")

    // Optional inputs (toggle via "Add More Details")
    var isMoreDetailsVisible by mutableStateOf(false)
    var selectedColor by mutableStateOf("")
    var selectedCategory by mutableStateOf("")
    var selectedSeatingCapacity by mutableStateOf("")
    var mileageInput by mutableStateOf("")
    var selectedIsTaxi by mutableStateOf("")

    var availableBrands by mutableStateOf<List<String>>(emptyList())
    var modelsList by mutableStateOf<List<String>>(emptyList())

    val isFormValid: Boolean
        get() = regNumber.trim().isNotEmpty() &&
                selectedVehicleType.isNotEmpty() &&
                selectedBrand.isNotEmpty() &&
                selectedModel.isNotEmpty() &&
                selectedYear.isNotEmpty() &&
                selectedFuelType.isNotEmpty() &&
                selectedGearType.isNotEmpty()

    // Track which mobile was last loaded so we can detect user changes
    private var lastLoadedMobile: String = ""

    init {
        refreshUser()
    }

    /**
     * Call this to reload user name and vehicles for the current logged-in user.
     * Detects if the user has changed since last load.
     */
    fun refreshUser() {
        val currentMobile = activeMobile
        if (currentMobile != lastLoadedMobile || userName.isBlank()) {
            lastLoadedMobile = currentMobile
            loadUserName()
            loadVehicles()
            getUserDetails()
        }
    }

    fun loadUserName() {
        val persistedUser = getLoggedInUser()
        currentUser = persistedUser

        if (isGuestUser()) {
            userName = "Guest"
            return
        }

        val nameFromUser = persistedUser?.name?.takeIf { it.isNotBlank() }
        val nameFromPrefs = getPersistedString("logged_in_user_name")?.takeIf { it.isNotBlank() }
        val mobile = activeMobile

        userName = nameFromUser ?: nameFromPrefs ?: if (mobile.isNotBlank()) "User" else "Guest"
    }

    fun loadVehicles() {
        // In-memory vehicles ready for state presentation
        uiState = HomeUiState.Success(vehicleList)
    }

    fun getUserDetails() {
        viewModelScope.launch {
            userRepository.getUser()
        }
    }

    fun onVehicleTypeSelected(type: String) {
        selectedVehicleType = type
        selectedBrand = ""
        selectedModel = ""
        modelsList = emptyList()
        availableBrands = if (type == "Car") {
            listOf("Maruthi Suzuki", "Hyundai", "Tata", "Honda")
        } else if (type == "Bike") {
            listOf("Hero Honda")
        } else {
            emptyList()
        }
    }

    fun onBrandSelected(brand: String) {
        selectedBrand = brand
        selectedModel = ""
        modelsList = when (brand) {
            "Maruthi Suzuki" -> listOf("Swift VXI Hatchback", "Baleno", "Brezza", "Dzire", "Ertiga")
            "Hyundai" -> listOf("i20", "Creta", "Venue", "Verna")
            "Tata" -> listOf("Nexon", "Harrier", "Punch", "Tiago", "Safari")
            "Honda" -> listOf("City", "Amaze", "Elevate")
            "Hero Honda" -> listOf("GLAMOUR 125 Fi", "Splendor Plus", "Passion Pro", "HF Deluxe")
            else -> emptyList()
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        vehicleList = vehicleList.filterNot { it.registrationNumber == vehicle.registrationNumber }
        uiState = HomeUiState.Success(vehicleList)
    }

    fun submitVehicle() {
        if (isFormValid) {
            val newVehicle = Vehicle(
                registrationNumber = regNumber.trim().uppercase(),
                vehicleType = selectedVehicleType,
                brand = selectedBrand,
                model = selectedModel,
                year = selectedYear.toIntOrNull() ?: 2022,
                fuelType = selectedFuelType,
                gearType = selectedGearType,
                imageRes = if (selectedVehicleType == "Car") "img_car_swift" else "img_bike_glamour"
            )
            vehicleList = vehicleList + newVehicle
            uiState = HomeUiState.Success(vehicleList)
            isSheetOpen = false

            // Reset form fields
            regNumber = ""
            selectedVehicleType = ""
            selectedBrand = ""
            selectedModel = ""
            selectedYear = ""
            selectedFuelType = ""
            selectedGearType = ""
            isMoreDetailsVisible = false
            selectedColor = ""
            selectedCategory = ""
            selectedSeatingCapacity = ""
            mileageInput = ""
            selectedIsTaxi = ""
            availableBrands = emptyList()
            modelsList = emptyList()
        }
    }
}
