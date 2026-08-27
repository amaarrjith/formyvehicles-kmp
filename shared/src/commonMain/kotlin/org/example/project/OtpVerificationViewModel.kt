package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpVerificationViewModel : ViewModel() {
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

    fun verifyOtp(onSuccess: (String) -> Unit): Boolean {
        val otp = getOtp()
        return if (otp == "0000") {
            toastMessage = "OTP verified successfully!"
            isErrorToast = false
            pendingNavigation = { onSuccess(otp) }
            showToast = true
            true
        } else {
            toastMessage = "Invalid OTP code"
            isErrorToast = true
            pendingNavigation = null
            showToast = true
            false
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
