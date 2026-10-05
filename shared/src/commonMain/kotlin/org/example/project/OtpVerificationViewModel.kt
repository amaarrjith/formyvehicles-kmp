package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.example.project.data.model.UserDto
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult
import kotlin.time.Duration.Companion.milliseconds

class OtpVerificationViewModel(
    private val authRepository: AuthRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {
    var otp1 by mutableStateOf("")
    var otp2 by mutableStateOf("")
    var otp3 by mutableStateOf("")
    var otp4 by mutableStateOf("")

    var showToast by mutableStateOf(false)
    var toastMessage by mutableStateOf("")
    var isErrorToast by mutableStateOf(false)

    // Pending navigation callback to be executed ONLY after toast goes away
    private var pendingNavigation: (() -> Unit)? = null

    // 2-minute countdown timer (120 seconds)
    var timerSeconds by mutableStateOf(120)
    private var timerJob: Job? = null

    init {
        startResendTimer()
    }

    fun startResendTimer() {
        timerJob?.cancel()
        timerSeconds = 120
        timerJob = viewModelScope.launch {
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds--
            }
        }
    }

    fun formatTimer(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        val minsStr = if (mins < 10) "0$mins" else "$mins"
        val secsStr = if (secs < 10) "0$secs" else "$secs"
        return "$minsStr:$secsStr"
    }

    fun onResendClick(onResendCallback: () -> Unit) {
        if (timerSeconds == 0) {
            toastMessage = "OTP code resent successfully!"
            isErrorToast = false
            pendingNavigation = null
            showToast = true
            startResendTimer()
            onResendCallback()
        }
    }

    fun getOtp(): String {
        return otp1 + otp2 + otp3 + otp4
    }

    fun verifyOtp(mobileNumber: String, onSuccess: (String) -> Unit) {
        val otp = getOtp()
        viewModelScope.launch {
            val result = authRepository.verifyOTP(
                mobileNumber,
                code = otp
            )
            when(result) {
                is NetworkResult.Success -> {
                    val response = result.data
                    val accessToken = response.accessToken ?: response.access
                    val refreshToken = response.refreshToken ?: response.refresh
                    val user = response.user

                    // Save access and refresh tokens in UserDefaults / Preferences
                    if (!accessToken.isNullOrBlank()) {
                        authPreferences.saveTokens(
                            accessToken = accessToken,
                            refreshToken = refreshToken ?: "",
                            tokenExpiry = 3600
                        )
                        setPersistedString("access_token", accessToken)
                    }
                    if (!refreshToken.isNullOrBlank()) {
                        setPersistedString("refresh_token", refreshToken)
                    }

                    // Save user in UserDefaults / Preferences
                    if (user != null) {
                        val json = Json { ignoreUnknownKeys = true }
                        val userJson = json.encodeToString(UserDto.serializer(), user)
                        setPersistedString("logged_in_user", userJson)
                        user.id?.let { id -> setPersistedString("logged_in_user_id", id) }
                        user.name?.let { name -> setPersistedString("logged_in_user_name", name) }
                        setPersistedString("logged_in_user_mobile", user.mobileNumber ?: mobileNumber)
                        user.countryCode?.let { cc -> setPersistedString("logged_in_user_country_code", cc) }
                    } else {
                        setPersistedString("logged_in_user_mobile", mobileNumber)
                    }

                    // Set user as logged in and clear guest status
                    setUserLoggedIn(true)
                    setGuestUser(false)

                    isErrorToast = false
                    toastMessage = "OTP Verified Successfully"
                    showToast = true
                    onSuccess(otp)
                }
                is NetworkResult.Error -> {
                    isErrorToast = true
                    toastMessage = result.message
                    showToast = true
                }
            }
        }
    }

    fun onToastDismissed() {
        showToast = false
        val nav = pendingNavigation
        pendingNavigation = null
        nav?.invoke()
    }

    fun clearOtp() {
        otp1 = ""
        otp2 = ""
        otp3 = ""
        otp4 = ""
    }
}
