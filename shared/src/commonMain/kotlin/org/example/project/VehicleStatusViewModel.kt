package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.data.model.UserVehicle
import org.example.project.domain.repository.UserRepository
import org.example.project.network.NetworkResult

data class VehicleFilterOption(
    val title: String,       // Line 1: e.g. "Swift VXI Hatchback" or "All Vehicles"
    val subtitle: String,    // Line 2: e.g. "KL 56 X 7004" or "2 Vehicles"
    val filterKey: String    // unique key e.g. "KL 56 X 7004" or "All"
)

class VehicleStatusViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {
    val activeMobile: String
        get() = getPersistedString("logged_in_user_mobile") ?: ""

    var selectedVehicleFilter by mutableStateOf("All")
    var tempSelectedVehicleFilter by mutableStateOf("All")
    var isChooseVehicleSheetOpen by mutableStateOf(false)
    var selectedStatusDetail by mutableStateOf<VehicleStatus?>(null)

    var statusList by mutableStateOf<List<VehicleStatus>>(emptyList())
    var uiState by mutableStateOf<VehicleStatusUiState>(VehicleStatusUiState.Loading)
    var isAddStatusSheetOpen by mutableStateOf(false)
    var vehicleDropdownExpanded by mutableStateOf(false)

    // Form inputs for "Add New Info" modal sheet matching screenshot
    var statusTitle by mutableStateOf("")
    var vehicleAssociation by mutableStateOf("All Vehicles")
    var statusType by mutableStateOf("Alert")
    
    // Info Date (date for which info is being added)
    val todayDateString = getCurrentDateString()
    var infoDate by mutableStateOf(todayDateString)
    var isInfoDatePickerOpen by mutableStateOf(false)

    // Alert Date & Time (Optional)
    var alertDate by mutableStateOf("")
    var alertTime by mutableStateOf("")
    var isAlertDatePickerOpen by mutableStateOf(false)
    var isAlertTimePickerOpen by mutableStateOf(false)

    // Cycle selection (Initial default MUST be "Ones")
    var selectedCycle by mutableStateOf("Ones") // "Ones", "Every Week", "Every Month", "Every Year"
    val cycleOptions = listOf("Ones", "Every Week", "Every Month", "Every Year")

    val alertDateTimeDisplay: String
        get() {
            if (alertDate.isBlank()) return ""
            return if (alertTime.isNotBlank()) "$alertDate - $alertTime" else alertDate
        }

    val dynamicInfoMessage: String?
        get() {
            if (alertDate.isBlank()) return null
            val cycleText = when (selectedCycle) {
                "Ones", "Once" -> "once"
                "Every Week" -> "every week"
                "Every Month" -> "every month"
                "Every Year" -> "every year"
                else -> selectedCycle.lowercase()
            }
            val timeText = if (alertTime.isNotBlank()) " at $alertTime" else ""
            return "You'll receive an alert $cycleText on $alertDate$timeText."
        }

    var userVehicles by mutableStateOf<List<Vehicle>>(emptyList())

    private fun UserVehicle.toVehicle(): Vehicle {
        return Vehicle(
            registrationNumber = registrationNumber,
            vehicleType = vehicleType.name,
            brand = brand.name,
            model = vehicleModel.name,
            year = year.toIntOrNull() ?: 0,
            fuelType = fuelType,
            gearType = gearType,
            imageRes = imageUrl
        )
    }

    val filterOptions: List<VehicleFilterOption>
        get() {
            val list = mutableListOf<VehicleFilterOption>()
            list.add(
                VehicleFilterOption(
                    title = "All Vehicles",
                    subtitle = "${userVehicles.size} Vehicles",
                    filterKey = "All"
                )
            )
            userVehicles.forEach { vehicle ->
                list.add(
                    VehicleFilterOption(
                        title = "${vehicle.brand} ${vehicle.model}".trim(),
                        subtitle = vehicle.registrationNumber.trim(),
                        filterKey = vehicle.registrationNumber
                    )
                )
            }
            return list
        }

    val availableVehicleOptions: List<String>
        get() {
            val list = mutableListOf("All Vehicles")
            list.addAll(userVehicles.map { "${it.brand} ${it.model} (${it.registrationNumber})" })
            return list
        }

    val selectedOption: VehicleFilterOption
        get() {
            val normalizedSelected = selectedVehicleFilter.replace(" ", "").replace("%20", "").lowercase()
            val matched = filterOptions.firstOrNull { 
                it.filterKey == selectedVehicleFilter || 
                (it.filterKey != "All" && it.filterKey.replace(" ", "").equals(normalizedSelected, ignoreCase = true)) ||
                it.title.contains(selectedVehicleFilter, ignoreCase = true)
            }
            if (matched != null) return matched
            if (selectedVehicleFilter != "All" && selectedVehicleFilter.isNotBlank()) {
                val cleanSubtitle = selectedVehicleFilter.replace("%20", " ")
                return VehicleFilterOption(
                    title = "Vehicle",
                    subtitle = cleanSubtitle,
                    filterKey = selectedVehicleFilter
                )
            }
            return filterOptions.firstOrNull() ?: VehicleFilterOption("All Vehicles", "${userVehicles.size} Vehicles", "All")
        }

    // Dynamic Filter Count Badge calculation
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedVehicleFilter != "All" && selectedVehicleFilter.isNotEmpty()) {
                count++
            }
            return count
        }

    fun loadVehiclesAndStatus(targetRegNumber: String? = null) {
        viewModelScope.launch {
            uiState = VehicleStatusUiState.Loading

            val cleanTarget = targetRegNumber?.trim()?.takeIf {
                it.isNotEmpty() && !it.equals("all", ignoreCase = true)
            }
            if (cleanTarget != null) {
                selectedVehicleFilter = cleanTarget
                tempSelectedVehicleFilter = cleanTarget
            }

            when (val result = userRepository.getUserVehicles()) {
                is NetworkResult.Success -> {
                    val vehicles = result.data.map { it.toVehicle() }
                    userVehicles = vehicles

                    if (cleanTarget != null) {
                        val normalizedTarget = cleanTarget.replace(" ", "").replace("%20", "").lowercase()
                        val matched = vehicles.firstOrNull {
                            it.registrationNumber.equals(cleanTarget, ignoreCase = true) ||
                            it.registrationNumber.replace(" ", "").equals(normalizedTarget, ignoreCase = true)
                        }
                        if (matched != null) {
                            selectedVehicleFilter = matched.registrationNumber
                            tempSelectedVehicleFilter = matched.registrationNumber
                            vehicleAssociation = "${matched.brand} ${matched.model} (${matched.registrationNumber})"
                        } else {
                            val decoded = cleanTarget.replace("%20", " ")
                            selectedVehicleFilter = decoded
                            tempSelectedVehicleFilter = decoded
                            vehicleAssociation = decoded
                        }
                    } else if (selectedVehicleFilter != "All") {
                        val normalizedCurrent = selectedVehicleFilter.replace(" ", "").replace("%20", "").lowercase()
                        val matched = vehicles.firstOrNull {
                            it.registrationNumber.equals(selectedVehicleFilter, ignoreCase = true) ||
                            it.registrationNumber.replace(" ", "").equals(normalizedCurrent, ignoreCase = true)
                        }
                        if (matched != null) {
                            selectedVehicleFilter = matched.registrationNumber
                            tempSelectedVehicleFilter = matched.registrationNumber
                            vehicleAssociation = "${matched.brand} ${matched.model} (${matched.registrationNumber})"
                        }
                    }
                }
                is NetworkResult.Error -> {
                    showErrorToast(result.message ?: "Failed to fetch vehicles")
                }
            }

            loadStatusList()
            uiState = VehicleStatusUiState.Success(statusList)
        }
    }

    fun openChooseVehicleSheet() {
        tempSelectedVehicleFilter = selectedVehicleFilter
        isChooseVehicleSheetOpen = true
    }

    fun applyChooseVehicleSelection() {
        selectedVehicleFilter = tempSelectedVehicleFilter
        val matched = userVehicles.firstOrNull { it.registrationNumber == selectedVehicleFilter }
        if (matched != null) {
            vehicleAssociation = "${matched.brand} ${matched.model} (${matched.registrationNumber})"
        } else if (selectedVehicleFilter == "All") {
            vehicleAssociation = "All Vehicles"
        }
        loadStatusList()
        uiState = VehicleStatusUiState.Success(statusList)
        isChooseVehicleSheetOpen = false
    }

    fun loadStatusList() {
        statusList = VehicleStatusStore.getStatusList(selectedVehicleFilter)
        if (vehicleAssociation.isEmpty()) {
            vehicleAssociation = "All Vehicles"
        }
    }

    fun clearAlertDateTime() {
        alertDate = ""
        alertTime = ""
    }

    fun submitStatus() {
        val titleText = statusTitle.trim().ifEmpty { "My Car Loan - ₹ 12000" }
        val targetInfoDate = infoDate.trim().ifEmpty { todayDateString }
        val infoMsg = dynamicInfoMessage

        val newStatus = VehicleStatus(
            id = (statusList.size + 10).toString(),
            type = statusType,
            title = titleText,
            description = infoMsg ?: "Info added for $targetInfoDate",
            vehicleName = vehicleAssociation.ifEmpty { "All Vehicles" },
            date = targetInfoDate,
            time = alertTime,
            alertTime = alertDateTimeDisplay.ifBlank { null },
            cycle = selectedCycle
        )
        VehicleStatusStore.addStatus(newStatus)
        loadStatusList()
        uiState = VehicleStatusUiState.Success(statusList)

        // Reset fields to default
        statusTitle = ""
        val matched = userVehicles.firstOrNull { it.registrationNumber == selectedVehicleFilter }
        vehicleAssociation = if (matched != null) "${matched.brand} ${matched.model} (${matched.registrationNumber})" else "All Vehicles"
        statusType = "Alert"
        infoDate = todayDateString
        alertDate = ""
        alertTime = ""
        selectedCycle = "Ones"
        isAddStatusSheetOpen = false
    }
}
