package org.example.project.location

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.CLPlacemark
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IosLocationManager : LocationManager {

    override suspend fun getCurrentAddress(): UserAddress? {
        val location = fetchLocation() ?: return null
        return reverseGeocode(location)
    }

    private suspend fun fetchLocation(): CLLocation? {
        return suspendCancellableCoroutine { continuation ->
            val locationManager = CLLocationManager()
            val initialStatus = locationManager.authorizationStatus()

            if (initialStatus == kCLAuthorizationStatusDenied || initialStatus == kCLAuthorizationStatusRestricted) {
                if (continuation.isActive) continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                    manager.stopUpdatingLocation()
                    val loc = didUpdateLocations.lastOrNull() as? CLLocation
                    if (continuation.isActive) {
                        continuation.resume(loc)
                    }
                }

                override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                    val status = manager.authorizationStatus()
                    if (status != kCLAuthorizationStatusNotDetermined) {
                        manager.stopUpdatingLocation()
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                }

                override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                    handleAuthorization(manager)
                }

                override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
                    handleAuthorization(manager)
                }

                private fun handleAuthorization(manager: CLLocationManager) {
                    val status = manager.authorizationStatus()
                    when (status) {
                        kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> {
                            manager.startUpdatingLocation()
                        }
                        kCLAuthorizationStatusDenied, kCLAuthorizationStatusRestricted -> {
                            manager.stopUpdatingLocation()
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                        else -> { }
                    }
                }
            }

            locationManager.delegate = delegate

            if (initialStatus == kCLAuthorizationStatusAuthorizedWhenInUse || initialStatus == kCLAuthorizationStatusAuthorizedAlways) {
                locationManager.startUpdatingLocation()
            } else if (initialStatus == kCLAuthorizationStatusNotDetermined) {
                locationManager.requestWhenInUseAuthorization()
            }

            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
            }
        }
    }

    private suspend fun reverseGeocode(location: CLLocation): UserAddress? {
        return suspendCancellableCoroutine { continuation ->
            val geocoder = CLGeocoder()
            geocoder.reverseGeocodeLocation(location) { placemarks, error ->
                if (error != null || placemarks == null) {
                    if (continuation.isActive) continuation.resume(null)
                } else {
                    val placemark = placemarks.firstOrNull() as? CLPlacemark
                    val userAddress = if (placemark != null) {
                        val area = placemark.subLocality ?: placemark.thoroughfare ?: placemark.name
                        val city = placemark.locality ?: placemark.subAdministrativeArea
                        val district = placemark.subAdministrativeArea ?: placemark.locality
                        val state = placemark.administrativeArea
                        UserAddress(
                            area = area,
                            city = city,
                            district = district,
                            state = state
                        )
                    } else null

                    if (continuation.isActive) continuation.resume(userAddress)
                }
            }
        }
    }
}

actual fun createLocationManager(): LocationManager = IosLocationManager()
