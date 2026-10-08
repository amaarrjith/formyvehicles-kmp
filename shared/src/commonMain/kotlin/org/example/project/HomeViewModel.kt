package org.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.UserVehicle
import org.example.project.data.model.VehicleBrand
import org.example.project.data.model.VehicleModel
import org.example.project.data.model.VehicleType
import org.example.project.domain.repository.UserRepository
import org.example.project.domain.repository.VehicleRepository
import org.example.project.network.NetworkResult

/**
 * Immutable UI State representing the entire Home screen and Add Vehicle sheet.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userName: String = "",
    val vehicleList: List<UserVehicle> = emptyList(),
    var isSheetOpen: Boolean = false,

    // Loading states for dropdowns
    val isTypesLoading: Boolean = false,
    val isBrandsLoading: Boolean = false,
    val isModelsLoading: Boolean = false,

    // Dynamic model lists from repository
    val vehicleTypes: List<VehicleType> = emptyList(),
    val availableBrands: List<VehicleBrand> = emptyList(),
    val modelsList: List<VehicleModel> = emptyList(),

    // Static dropdown options
    val fuelTypes: List<String> = listOf("Petrol", "Diesel", "Electric"),
    val gearTypes: List<String> = listOf("Automatic", "Manual"),
    val vehicleColors: List<String> = emptyList(),
    val vehicleCategories: List<String> = emptyList(),
    val seatingCapacities: List<String> = emptyList(),

    // Mandatory form inputs
    val regNumber: String = "",
    val selectedVehicleType: VehicleType? = null,
    val selectedBrand: VehicleBrand? = null,
    val selectedModel: VehicleModel? = null,
    val selectedYear: String = "",
    val selectedFuelType: String = "",
    val selectedGearType: String = "",

    // Optional inputs
    val isMoreDetailsVisible: Boolean = false,
    val selectedColor: String = "",
    val selectedCategory: String = "",
    val selectedSeatingCapacity: String = "",
    val mileageInput: String = "",
    val selectedIsTaxi: String = ""
) {
    val isFormValid: Boolean
        get() = regNumber.trim().isNotEmpty() &&
                selectedVehicleType != null &&
                selectedBrand != null &&
                selectedModel != null &&
                selectedYear.isNotEmpty() &&
                selectedFuelType.isNotEmpty() &&
                selectedGearType.isNotEmpty()
}

class HomeViewModel(
    private val userRepository: UserRepository,
    private val vehicleRepository: VehicleRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshUser()
        getAvailableVehicleTypes()
        getUserVehicles()
    }

    /**
     * Reload user name, vehicles, and vehicle types.
     */
    fun refreshUser() {
        loadUserName()
        getUserDetails()
        getAvailableVehicleTypes()
    }

    private fun loadUserName() {
        val persistedUser = getLoggedInUser()
        if (isGuestUser()) {
            _uiState.update { it.copy(userName = "Guest") }
            return
        }

        val nameFromUser = persistedUser?.name?.takeIf { it.isNotBlank() }
        val nameFromPrefs = getPersistedString("logged_in_user_name")?.takeIf { it.isNotBlank() }
        val name = nameFromUser ?: nameFromPrefs ?: "User"
        _uiState.update { it.copy(userName = name) }
    }

    private fun getUserDetails() {
        viewModelScope.launch {
            when (val result = userRepository.getUser()) {
                is NetworkResult.Success -> {
                    val name = result.data.name?.takeIf { it.isNotBlank() }
                    if (name != null) {
                        _uiState.update { it.copy(userName = name) }
                    }
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun getUserVehicles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true)}
            val result = userRepository.getUserVehicles()
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        vehicleList = result.data
                    ) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    showErrorToast(result.message, "Vehicles")
                }
            }
        }
    }

    fun getAvailableVehicleTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTypesLoading = true) }
            when (val result = vehicleRepository.getVehicleTypes()) {
                is NetworkResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            vehicleTypes = result.data,
                            isTypesLoading = false
                        ) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isTypesLoading = false) }
                }
            }
        }
    }

    fun getAvailableBrands(vehicleTypeId: Int) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isBrandsLoading = true,
                    availableBrands = emptyList(),
                    selectedBrand = null,
                    modelsList = emptyList(),
                    selectedModel = null
                ) 
            }
            when (val result = vehicleRepository.getVehicleBrands(vehicleTypeId)) {
                is NetworkResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            availableBrands = result.data,
                            isBrandsLoading = false
                        ) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isBrandsLoading = false) }
                }
            }
        }
    }

    fun getAvailableModel(vehicleTypeId: Int, vehicleBrandId: Int) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isModelsLoading = true,
                    modelsList = emptyList(),
                    selectedModel = null
                ) 
            }
            when (val result = vehicleRepository.getVehicleModels(vehicleTypeId, vehicleBrandId)) {
                is NetworkResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            modelsList = result.data,
                            isModelsLoading = false
                        ) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isModelsLoading = false) }
                }
            }
        }
    }

    // UI event handlers (Unidirectional Data Flow)
    fun setSheetOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isSheetOpen = isOpen) }
        if (isOpen && _uiState.value.vehicleTypes.isEmpty()) {
            getAvailableVehicleTypes()
        }
    }

    fun onRegNumberChange(value: String) {
        _uiState.update { it.copy(regNumber = value) }
    }

    fun onVehicleTypeSelected(type: VehicleType) {
        _uiState.update {
            it.copy(
                selectedVehicleType = type,
                selectedBrand = null,
                selectedModel = null,
                availableBrands = emptyList(),
                modelsList = emptyList()
            )
        }
        getAvailableBrands(type.id)
    }

    fun onBrandSelected(brand: VehicleBrand) {
        val currentType = _uiState.value.selectedVehicleType
        _uiState.update {
            it.copy(
                selectedBrand = brand,
                selectedModel = null,
                modelsList = emptyList()
            )
        }
        if (currentType != null) {
            getAvailableModel(currentType.id, brand.id)
        }
    }

    fun onModelSelected(model: VehicleModel) {
        _uiState.update { it.copy(selectedModel = model) }
    }

    fun onYearSelected(year: String) {
        _uiState.update { it.copy(selectedYear = year) }
    }

    fun onFuelTypeSelected(fuelType: String) {
        _uiState.update { it.copy(selectedFuelType = fuelType) }
    }

    fun onGearTypeSelected(gearType: String) {
        _uiState.update { it.copy(selectedGearType = gearType) }
    }

    fun toggleMoreDetails() {
        _uiState.update { it.copy(isMoreDetailsVisible = !it.isMoreDetailsVisible) }
    }

    fun onColorSelected(color: String) {
        _uiState.update { it.copy(selectedColor = color) }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onSeatingCapacitySelected(capacity: String) {
        _uiState.update { it.copy(selectedSeatingCapacity = capacity) }
    }

    fun onMileageChange(mileage: String) {
        _uiState.update { it.copy(mileageInput = mileage) }
    }

    fun onIsTaxiSelected(isTaxi: String) {
        _uiState.update { it.copy(selectedIsTaxi = isTaxi) }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        _uiState.update { current ->
            current.copy(vehicleList = current.vehicleList.filterNot { it.registrationNumber == vehicle.registrationNumber })
        }
    }

    fun submitVehicle() {
        val state = _uiState.value
        val vehicleType = state.selectedVehicleType ?: return
        val brand = state.selectedBrand ?: return
        val model = state.selectedModel ?: return

        if (state.isFormValid) {
            viewModelScope.launch {
                val result = userRepository.addUserVehicles(
                    registrationNumber = state.regNumber,
                    vehicleTypeId = state.selectedVehicleType.id,
                    brandTypeId = state.selectedBrand.id,
                    modelTypeId = state.selectedModel.id,
                    year = state.selectedYear,
                    fuelType = state.selectedFuelType,
                    gearType = state.selectedGearType
                )
                when(result) {
                    is NetworkResult.Success -> {
                        if(result.data.isVehicleAdded) {
                            getUserVehicles()
                            _uiState.update { it.copy(isSheetOpen = false) }
                            showSuccessToast("${result.data.vehicle.registrationNumber} Added Successfully")
                        }
                    }
                    is NetworkResult.Error -> {
                        showErrorToast(result.message, "Add Vehicle")
                    }
                }
            }
//            _uiState.update { current ->
//                current.copy(
//                    vehicleList = current.vehicleList + newVehicle,
//                    isSheetOpen = false,
//                    regNumber = "",
//                    selectedVehicleType = null,
//                    selectedBrand = null,
//                    selectedModel = null,
//                    selectedYear = "",
//                    selectedFuelType = "",
//                    selectedGearType = "",
//                    isMoreDetailsVisible = false,
//                    selectedColor = "",
//                    selectedCategory = "",
//                    selectedSeatingCapacity = "",
//                    mileageInput = "",
//                    selectedIsTaxi = "",
//                    availableBrands = emptyList(),
//                    modelsList = emptyList()
//                )
//            }
        }
    }

    override fun clearError() {
        super.clearError()
        _uiState.update { it.copy(errorMessage = null) }
    }
}
