package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class HomeViewModel(private val db: AppDatabase) : ViewModel() {
    // Read activeMobile dynamically every time so it picks up the latest logged-in user
    val activeMobile: String
        get() = getPersistedString("logged_in_user_mobile") ?: ""

    private val queries = db.appDatabaseQueries

    val vehicleTypes = queries.selectVehicleTypes().executeAsList()
    val fuelTypes = queries.selectFuelTypes().executeAsList()
    val gearTypes = queries.selectGearTypes().executeAsList()

    val vehicleColors: List<String> = try {
        val list = queries.selectVehicleColors().executeAsList()
        if (list.isNotEmpty()) list else listOf("Red", "White", "Black", "Silver", "Blue", "Grey", "Yellow", "Green", "Orange", "Brown")
    } catch (e: Exception) {
        listOf("Red", "White", "Black", "Silver", "Blue", "Grey", "Yellow", "Green", "Orange", "Brown")
    }

    val vehicleCategories: List<String> = try {
        val list = queries.selectVehicleCategories().executeAsList()
        if (list.isNotEmpty()) list else listOf("Hatchback", "Sedan", "SUV", "MUV", "Coupe", "Convertible", "Pickup Truck", "Scooter", "Motorcycle", "EV")
    } catch (e: Exception) {
        listOf("Hatchback", "Sedan", "SUV", "MUV", "Coupe", "Convertible", "Pickup Truck", "Scooter", "Motorcycle", "EV")
    }

    val seatingCapacities: List<String> = try {
        val list = queries.selectSeatingCapacities().executeAsList()
        if (list.isNotEmpty()) list else listOf("2 Seater", "4 Seater", "5 Seater", "6 Seater", "7 Seater", "8+ Seater")
    } catch (e: Exception) {
        listOf("2 Seater", "4 Seater", "5 Seater", "6 Seater", "7 Seater", "8+ Seater")
    }

    var vehicleList by mutableStateOf<List<Vehicle>>(emptyList())
    var userName by mutableStateOf("Krish Maitreyi")
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
        if (currentMobile != lastLoadedMobile) {
            lastLoadedMobile = currentMobile
            loadUserName()
            loadVehicles()
        }
    }

    fun loadUserName() {
        try {
            val user = queries.getUserByMobile(activeMobile).executeAsOneOrNull()
            userName = user?.name ?: "Krish Maitreyi"
        } catch (e: Exception) {
            userName = "Krish Maitreyi"
        }
    }

    fun loadVehicles() {
        try {
            val userList = queries.selectUserVehicles(activeMobile).executeAsList()
            vehicleList = userList.map { dbVehicle ->
                Vehicle(
                    registrationNumber = dbVehicle.registrationNumber,
                    vehicleType = dbVehicle.vehicleType,
                    brand = dbVehicle.brand,
                    model = dbVehicle.model,
                    year = dbVehicle.year.toInt(),
                    fuelType = dbVehicle.fuelType,
                    gearType = dbVehicle.gearType,
                    imageRes = dbVehicle.imageRes
                )
            }
        } catch (e: Exception) {
            vehicleList = emptyList()
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
        try {
            modelsList = queries.selectModelsByBrand(brand).executeAsList()
        } catch (e: Exception) {
            modelsList = emptyList()
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        try {
            queries.deleteUserVehicle(vehicle.registrationNumber)
        } catch (e: Exception) {
            // ignore
        }
        loadVehicles()
        uiState = HomeUiState.Success(vehicleList)
    }

    fun submitVehicle() {
        if (isFormValid) {
            try {
                queries.insertUserVehicle(
                    registrationNumber = regNumber.trim().uppercase(),
                    vehicleType = selectedVehicleType,
                    brand = selectedBrand,
                    model = selectedModel,
                    year = selectedYear.toLong(),
                    fuelType = selectedFuelType,
                    gearType = selectedGearType,
                    imageRes = if (selectedVehicleType == "Car") "img_car_swift" else "img_bike_glamour",
                    userMobile = activeMobile
                )
            } catch (e: Exception) {
                // ignore
            }
            loadVehicles()
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
