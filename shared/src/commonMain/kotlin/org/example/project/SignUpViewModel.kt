package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.example.project.model.Country

class SignUpViewModel(private val db: AppDatabase) : ViewModel() {
    var name by mutableStateOf("")
    var selectedState by mutableStateOf("")
    var mobileNumber by mutableStateOf("")
    var selectedCountry by mutableStateOf(Country("India", "IN", "🇮🇳", "+91"))
    var agreed by mutableStateOf(false)

    var showToast by mutableStateOf(false)
    var toastMessage by mutableStateOf("")

    val statesList = listOf(
        "Andhra Pradesh", "Delhi", "Gujarat", "Karnataka", 
        "Kerala", "Maharashtra", "Tamil Nadu", "Telangana"
    )

    fun onSignUpClick(onSuccess: () -> Unit) {
        val cleanName = name.trim()
        val cleanMobile = mobileNumber.trim()

        if (cleanName.isEmpty() || cleanMobile.isEmpty()) {
            toastMessage = "Please fill in all fields."
            showToast = true
            return
        }

        try {
            val existingUser = db.appDatabaseQueries.getUserByMobile(cleanMobile).executeAsOneOrNull()
            if (existingUser != null) {
                toastMessage = "Mobile number already registered."
                showToast = true
                return
            }

            db.appDatabaseQueries.insertUser(
                name = cleanName,
                state = selectedState.trim(),
                mobileNumber = cleanMobile
            )
            setPersistedString("logged_in_user_mobile", cleanMobile)
            onSuccess()
        } catch (e: Exception) {
            toastMessage = "An error occurred. Please try again."
            showToast = true
        }
    }
}
