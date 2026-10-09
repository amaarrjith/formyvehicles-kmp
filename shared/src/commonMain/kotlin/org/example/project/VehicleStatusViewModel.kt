package org.example.project

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.alarm.scheduleDeviceAlarm
import org.example.project.data.model.UserVehicle
import org.example.project.data.model.VehicleInfoTypeModel
import org.example.project.domain.repository.UserRepository
import org.example.project.domain.repository.VehicleRepository
import org.example.project.network.NetworkResult

data class VehicleFilterOption(
    val title: String,       // Line 1: e.g. "Swift VXI Hatchback" or "All Vehicles"
    val subtitle: String,    // Line 2: e.g. "KL 56 X 7004" or "2 Vehicles"
    val filterKey: String    // unique key e.g. "KL 56 X 7004" or "All"
)

/**
 * Immutable UI State representing the entire Vehicle Status screen.
 */
data class VehicleStatusUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedVehicleFilter: UserVehicle? = null,
    val tempSelectedVehicleFilter: UserVehicle? = null,
    val isChooseVehicleSheetOpen: Boolean = false,
    val selectedStatusDetail: VehicleStatus? = null,
    val statusList: List<VehicleStatus> = emptyList(),
    val userVehicles: List<UserVehicle> = emptyList(),
    val isAddStatusSheetOpen: Boolean = false,
    val vehicleDropdownExpanded: Boolean = false,

    // Form inputs for "Add New Info" modal sheet
    val statusTitle: String = "",
    val vehicleAssociation: String = "",
    val statusType: String = "",
    val isInfoTypesLoading: Boolean = false,
    val infoTypes: List<VehicleInfoTypeModel> = emptyList(),
    val selectedInfoType: VehicleInfoTypeModel? = null,

    // Info Date (date for which info is being added)
    val infoDate: String = "",
    val isInfoDatePickerOpen: Boolean = false,

    // Alert Time with Time Picker
    val alertTime: String = "",
    val isAlertTimePickerOpen: Boolean = false,

    // Cycle selection (Initial default is "Ones")
    val selectedCycle: String = "Ones",
    val cycleOptions: List<String> = listOf("Ones", "Every Week", "Every Month", "Every Year")
) {



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
                        title = "${vehicle.brand.name} ${vehicle.vehicleModel.name}".trim(),
                        subtitle = vehicle.registrationNumber.trim(),
                        filterKey = vehicle.registrationNumber
                    )
                )
            }
            return list
        }

    val availableVehicleOptions: List<String>
        get() = userVehicles.map { "${it.brand.name} ${it.vehicleModel.name} (${it.registrationNumber})" }


    val selectedOption: VehicleFilterOption
        get() {
            val current = selectedVehicleFilter
            return if (current != null) {
                VehicleFilterOption(
                    title = "${current.brand.name} ${current.vehicleModel.name}".trim(),
                    subtitle = current.registrationNumber.trim(),
                    filterKey = current.registrationNumber
                )
            } else {
                VehicleFilterOption(
                    title = "All Vehicles",
                    subtitle = "${userVehicles.size} Vehicles",
                    filterKey = "All"
                )
            }
        }

    // Dynamic Filter Count Badge calculation
    val activeFilterCount: Int
        get() = if (selectedVehicleFilter != null) 1 else 0
}

class VehicleStatusViewModel(
    private val userRepository: UserRepository,
    private val vehicleRepository: VehicleRepository
) : BaseViewModel() {

    val activeMobile: String
        get() = getPersistedString("logged_in_user_mobile") ?: ""

    val todayDateString = getCurrentDateString()

    private val _uiState = MutableStateFlow(
        VehicleStatusUiState(
            infoDate = todayDateString
        )
    )
    val uiState: StateFlow<VehicleStatusUiState> = _uiState.asStateFlow()

    init {
        getVehicleInfoTypes()
    }

    fun getVehicleInfoTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isInfoTypesLoading = true) }
            when (val result = vehicleRepository.getVehicleInfoTypes()) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            infoTypes = result.data.types,
                            isInfoTypesLoading = false
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isInfoTypesLoading = false) }
                }
            }
        }
    }

    fun loadVehiclesAndStatus(targetRegNumber: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val cleanTarget = targetRegNumber?.trim()?.takeIf {
                it.isNotEmpty() && !it.equals("all", ignoreCase = true)
            }

            var currentFilter = _uiState.value.selectedVehicleFilter
            var tempFilter = _uiState.value.tempSelectedVehicleFilter
            var association = _uiState.value.vehicleAssociation

            var fetchedVehicles = _uiState.value.userVehicles

            when (val result = userRepository.getUserVehicles()) {
                is NetworkResult.Success -> {
                    fetchedVehicles = result.data

                    if (cleanTarget != null) {
                        val normalizedTarget = cleanTarget.replace(" ", "").replace("%20", "").lowercase()
                        val matched = fetchedVehicles.firstOrNull {
                            it.registrationNumber.equals(cleanTarget, ignoreCase = true) ||
                            it.registrationNumber.replace(" ", "").equals(normalizedTarget, ignoreCase = true)
                        }
                        if (matched != null) {
                            currentFilter = matched
                            tempFilter = matched
                            association = "${matched.brand.name} ${matched.vehicleModel.name} (${matched.registrationNumber})"
                        }
                    } else if (currentFilter != null) {
                        val matched = fetchedVehicles.firstOrNull { it.id == currentFilter?.id }
                        currentFilter = matched
                        tempFilter = matched
                        if (matched != null) {
                            association = "${matched.brand.name} ${matched.vehicleModel.name} (${matched.registrationNumber})"
                        }
                    }
                }
                is NetworkResult.Error -> {
                    showErrorToast(result.message ?: "Failed to fetch vehicles")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
            }

            if (association.isEmpty()) {
                association = "All Vehicles"
            }

            val regNumberParam = currentFilter?.registrationNumber
            var statuses = emptyList<VehicleStatus>()
            when (val statusResult = vehicleRepository.getVehicleStatuses(currentFilter?.id)) {
                is NetworkResult.Success -> {
                    statuses = statusResult.data.status
                }
                is NetworkResult.Error -> {
                    showErrorToast(statusResult.message ?: "Failed to fetch vehicle statuses")
                    _uiState.update { it.copy(errorMessage = statusResult.message) }
                }
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    userVehicles = fetchedVehicles,
                    selectedVehicleFilter = currentFilter,
                    tempSelectedVehicleFilter = tempFilter,
                    vehicleAssociation = association,
                    statusList = statuses
                )
            }
        }
    }

    fun openChooseVehicleSheet() {
        _uiState.update {
            it.copy(
                tempSelectedVehicleFilter = it.selectedVehicleFilter,
                isChooseVehicleSheetOpen = true
            )
        }
    }

    fun setChooseVehicleSheetOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isChooseVehicleSheetOpen = isOpen) }
    }

    fun setTempSelectedVehicleFilter(vehicle: UserVehicle?) {
        _uiState.update { it.copy(tempSelectedVehicleFilter = vehicle) }
    }

    fun applyChooseVehicleSelection() {
        val currentState = _uiState.value
        val selected = currentState.tempSelectedVehicleFilter
        val newAssociation = if (selected != null) {
            "${selected.brand.name} ${selected.vehicleModel.name} (${selected.registrationNumber})"
        } else {
            "All Vehicles"
        }

        _uiState.update {
            it.copy(
                selectedVehicleFilter = selected,
                vehicleAssociation = newAssociation,
                isChooseVehicleSheetOpen = false
            )
        }
        loadStatusList()
    }

    fun loadStatusList() {
        val id = _uiState.value.selectedVehicleFilter?.id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = vehicleRepository.getVehicleStatuses(id)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusList = result.data.status,
                            vehicleAssociation = if (it.vehicleAssociation.isEmpty()) "All Vehicles" else it.vehicleAssociation
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                    showErrorToast(result.message ?: "Failed to load vehicle statuses")
                }
            }
        }
    }


    fun setSelectedStatusDetail(status: VehicleStatus?) {
        _uiState.update { it.copy(selectedStatusDetail = status) }
    }

    fun setAddStatusSheetOpen(isOpen: Boolean) {
        val currentVehicle = _uiState.value.selectedVehicleFilter ?: _uiState.value.userVehicles.firstOrNull()
        val defaultAssociation = if (currentVehicle != null) {
            "${currentVehicle.brand.name} ${currentVehicle.vehicleModel.name} (${currentVehicle.registrationNumber})"
        } else ""
        val firstType = _uiState.value.infoTypes.firstOrNull()

        _uiState.update {
            it.copy(
                isAddStatusSheetOpen = isOpen,
                vehicleAssociation = if (it.vehicleAssociation.isEmpty()) defaultAssociation else it.vehicleAssociation,
                selectedInfoType = it.selectedInfoType ?: firstType,
                statusType = it.selectedInfoType?.name ?: firstType?.name ?: ""
            )
        }
        if (isOpen && _uiState.value.infoTypes.isEmpty()) {
            getVehicleInfoTypes()
        }
    }


    fun onStatusTitleChange(title: String) {
        _uiState.update { it.copy(statusTitle = title) }
    }

    fun onVehicleAssociationChange(association: String) {
        _uiState.update { it.copy(vehicleAssociation = association) }
    }

    fun onStatusTypeChange(type: String) {
        val matched = _uiState.value.infoTypes.firstOrNull { it.name.equals(type, ignoreCase = true) }
        _uiState.update {
            it.copy(
                statusType = type,
                selectedInfoType = matched ?: it.selectedInfoType
            )
        }
    }

    fun onInfoTypeSelected(type: VehicleInfoTypeModel) {
        _uiState.update {
            it.copy(
                selectedInfoType = type,
                statusType = type.name
            )
        }
    }

    fun onInfoDateChange(date: String) {
        _uiState.update { it.copy(infoDate = date) }
    }

    fun setInfoDatePickerOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isInfoDatePickerOpen = isOpen) }
    }

    fun onAlertTimeChange(time: String) {
        _uiState.update { it.copy(alertTime = time) }
    }

    fun setAlertTimePickerOpen(isOpen: Boolean) {
        _uiState.update { it.copy(isAlertTimePickerOpen = isOpen) }
    }

    fun onCycleSelected(cycle: String) {
        _uiState.update { it.copy(selectedCycle = cycle) }
    }


    private fun formatToApiDate(dateStr: String): String {
        val parts = dateStr.trim().split(" ", "-", "/")
        if (parts.size >= 3) {
            val day = parts[0].padStart(2, '0')
            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val monthIdx = monthNames.indexOfFirst { it.equals(parts[1], ignoreCase = true) }
            val month = if (monthIdx >= 0) (monthIdx + 1).toString().padStart(2, '0') else parts[1].padStart(2, '0')
            val year = parts[2]
            return "$day-$month-$year"
        }
        return dateStr
    }

    fun submitStatus() {
        val state = _uiState.value
        val titleText = state.statusTitle.trim()
        if (titleText.isEmpty()) {
            showErrorToast("Please enter a title", "Add Info")
            return
        }

        val selectedVehicle = state.userVehicles.firstOrNull {
            "${it.brand.name} ${it.vehicleModel.name} (${it.registrationNumber})" == state.vehicleAssociation ||
            it.registrationNumber == state.vehicleAssociation
        } ?: state.selectedVehicleFilter ?: state.userVehicles.firstOrNull()

        if (selectedVehicle == null) {
            showErrorToast("Please select a vehicle", "Add Info")
            return
        }
        val vehicleId = selectedVehicle.id

        val typeId = state.selectedInfoType?.id
            ?: state.infoTypes.firstOrNull { it.name.equals(state.statusType, ignoreCase = true) }?.id

        if (typeId == null) {
            showErrorToast("Please select a type", "Add Info")
            return
        }

        val targetDate = state.infoDate.trim().ifEmpty { todayDateString }
        val apiDate = formatToApiDate(targetDate)

        val cycleInt = when (state.selectedCycle) {
            "Ones", "Once", "One Time" -> 1
            "Every Week" -> 2
            "Every Month" -> 3
            "Every Year" -> 4
            else -> 1
        }


        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = vehicleRepository.addVehicleInfo(
                title = titleText,
                vehicleId = vehicleId,
                typeId = typeId,
                date = apiDate,
                cycle = cycleInt
            )

            when (result) {
                is NetworkResult.Success -> {
                    showSuccessToast(result.data.statusMessage.ifBlank { "Status added successfully" }, "Vehicle Status")

                    if (state.alertTime.isNotBlank()) {
                        scheduleDeviceAlarm(
                            title = titleText,
                            dateString = targetDate,
                            timeString = state.alertTime
                        )
                    }

                    val defaultAssociation = if (state.selectedVehicleFilter != null) {
                        "${state.selectedVehicleFilter.brand.name} ${state.selectedVehicleFilter.vehicleModel.name} (${state.selectedVehicleFilter.registrationNumber})"
                    } else {
                        "All Vehicles"
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusTitle = "",
                            vehicleAssociation = defaultAssociation,
                            selectedInfoType = it.infoTypes.firstOrNull(),
                            statusType = it.infoTypes.firstOrNull()?.name.orEmpty(),
                            infoDate = todayDateString,
                            alertTime = "",
                            selectedCycle = "Ones",

                            isAddStatusSheetOpen = false
                        )
                    }
                    loadStatusList()
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    showErrorToast(result.message, "Add Status")
                }

            }
        }
    }

    override fun clearError() {
        super.clearError()
        _uiState.update { it.copy(errorMessage = null) }
    }
}
