package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class VehicleStatus(
    val id: String,
    val type: String, // "Service", "Challan", "PUC", "Insurance", "Toll", "Alert"
    val title: String,
    val description: String,
    val vehicleName: String,
    val date: String, // "DD/MM/YYYY"
    val time: String = "",
    val alertTime: String? = null,
    val cycle: String = "Ones",
    val actionText: String? = null,
    val isActionable: Boolean = false,
    val viewAllCount: Int? = null
)

object VehicleStatusStore {
    private var store by mutableStateOf<List<VehicleStatus>>(emptyList())
    private var isInitialized = true
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    init {
        // Clear all vehicle statuses initially
        clearAllStatuses()
    }

    private fun initializeIfNeeded() {
        if (!isInitialized) {
            clearAllStatuses()
        }
    }

    fun clearAllStatuses() {
        store = emptyList()
        isInitialized = true
        setPersistedString("persisted_vehicle_statuses", "")
    }

    private fun saveToPreferences() {
        try {
            val encoded = json.encodeToString(store)
            setPersistedString("persisted_vehicle_statuses", encoded)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getStatusList(vehicleFilter: String): List<VehicleStatus> {
        initializeIfNeeded()
        if (vehicleFilter.isBlank() || vehicleFilter == "All") {
            return store
        }
        val isSwiftFilter = vehicleFilter.contains("Swift", ignoreCase = true) || vehicleFilter.contains("7004")
        val isGlamourFilter = vehicleFilter.contains("Glamour", ignoreCase = true) || vehicleFilter.contains("6332")

        return store.filter { status ->
            if (isSwiftFilter && (status.vehicleName.contains("Swift", ignoreCase = true) || status.vehicleName.contains("7004"))) {
                true
            } else if (isGlamourFilter && (status.vehicleName.contains("Glamour", ignoreCase = true) || status.vehicleName.contains("6332"))) {
                true
            } else {
                status.vehicleName.contains(vehicleFilter, ignoreCase = true) ||
                vehicleFilter.contains(status.vehicleName, ignoreCase = true)
            }
        }
    }

    fun addStatus(status: VehicleStatus) {
        initializeIfNeeded()
        store = (store + status).sortedWith(Comparator { a, b ->
            getEpochDays(a.date).compareTo(getEpochDays(b.date))
        })
        saveToPreferences()
    }
}


