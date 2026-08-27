package org.example.project

enum class AppScreens(val route: String) {
    Splash("splash"),
    WelcomeScreen("welcome_screen"),
    Login("login"),
    SignUp("sign_up"),
    OtpVerification("otp_verification"),
    Home("ic_tab_0"),
    VehicleStatus("vehicle_status/{regNumber}"),
    Settings("settings"),
    Notifications("notifications"),
    MyVehicles("my_vehicles")
}