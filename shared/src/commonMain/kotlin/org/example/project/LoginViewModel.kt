package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.example.project.model.Country

class LoginViewModel(private val db: AppDatabase) : ViewModel() {
    var mobileNumber by mutableStateOf("")
    var selectedCountry by mutableStateOf(Country("India", "IN", "🇮🇳", "+91"))
    var countryCode: String
        get() = selectedCountry.dialCode
        set(value) {
            selectedCountry = selectedCountry.copy(dialCode = value)
        }
    var dropdownExpanded by mutableStateOf(false)
    var showToast by mutableStateOf(false)
    var toastMessage by mutableStateOf("Please enter your mobile number.")

    fun onContinueClick(onSuccess: () -> Unit) {
        if (mobileNumber.trim().isEmpty()) {
            toastMessage = "Please enter your mobile number."
            showToast = true
        } else {
            var userExists = false
            try {
                val users = db.appDatabaseQueries.getUserByMobile(mobileNumber.trim()).executeAsList()
                userExists = users.isNotEmpty()
            } catch (e: Exception) {
                userExists = true
            }
            
            if (userExists) {
                setPersistedString("logged_in_user_mobile", mobileNumber.trim())
                onSuccess()
            } else {
                toastMessage = "Mobile number is not registered. Please sign up."
                showToast = true
            }
        }
    }
}
