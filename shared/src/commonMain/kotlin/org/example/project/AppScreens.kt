package org.example.project

enum class AppScreens(val route: String) {
    Splash("splash"),
    WelcomeScreen("welcome_screen"),
    Login("login"),
    SignUp("sign_up"),
    OtpVerification("otp_verification/{mobile}"),
    Home("ic_tab_0"),
    VehicleStatus("vehicle_status/{regNumber}"),
    Settings("settings"),
    Notifications("notifications"),
    MyVehicles("my_vehicles"),
    PrivacyPolicy("privacy_policy/{tab}");

    companion object {
        fun otpVerification(mobile: String): String = "otp_verification/${if (mobile.isBlank()) "unknown" else mobile.trim()}"
        fun vehicleStatus(regNumber: String): String = "vehicle_status/${if (regNumber.isBlank()) "all" else regNumber.trim()}"
        fun privacyPolicy(tab: String = "privacy"): String = "privacy_policy/${if (tab.isBlank()) "privacy" else tab.trim().lowercase()}"
        fun termsAndPrivacy(tab: String = "terms"): String = privacyPolicy(tab)
    }
}