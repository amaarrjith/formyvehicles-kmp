package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined

fun MainViewController() = ComposeUIViewController {
    val locationManager = CLLocationManager()
    if (locationManager.authorizationStatus() == kCLAuthorizationStatusNotDetermined) {
        locationManager.requestWhenInUseAuthorization()
    }
    App()
}