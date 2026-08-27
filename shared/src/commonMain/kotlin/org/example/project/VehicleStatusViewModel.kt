package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class VehicleFilterOption(
    val title: String,       // Line 1: e.g. "Swift VXI Hatchback" or "All Vehicles"
    val subtitle: String,    // Line 2: e.g. "KL 56 X 7004" or "2 Vehicles"
    val filterKey: String    // unique key e.g. "KL 56 X 7004" or "All"
)

class VehicleStatusViewModel(private val db: AppDatabase) : ViewModel() {
    private val queries = db.appDatabaseQueries
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

    // Fetches actual added vehicles belonging to active user from database
    val userVehicles: List<Vehicle>
        get() = try {
            val list = queries.selectUserVehicles(activeMobile).executeAsList()
            list.map { dbVehicle ->
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
            emptyList()
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
            return filterOptions.firstOrNull { 
                it.filterKey == selectedVehicleFilter || 
                selectedVehicleFilter.contains(it.filterKey, ignoreCase = true) ||
                it.title.contains(selectedVehicleFilter, ignoreCase = true)
            } ?: filterOptions.firstOrNull() ?: VehicleFilterOption("All Vehicles", "${userVehicles.size} Vehicles", "All")
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

    fun openChooseVehicleSheet() {
        tempSelectedVehicleFilter = selectedVehicleFilter
        isChooseVehicleSheetOpen = true
    }

    fun applyChooseVehicleSelection() {
        selectedVehicleFilter = tempSelectedVehicleFilter
        loadStatusList()
        isChooseVehicleSheetOpen = false
    }

    fun loadStatusList() {
        statusList = VehicleStatusStore.getStatusList(selectedVehicleFilter)
        uiState = VehicleStatusUiState.Success(statusList)
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

        // Reset fields to default
        statusTitle = ""
        vehicleAssociation = "All Vehicles"
        statusType = "Alert"
        infoDate = todayDateString
        alertDate = ""
        alertTime = ""
        selectedCycle = "Ones"
        isAddStatusSheetOpen = false
    }
}
