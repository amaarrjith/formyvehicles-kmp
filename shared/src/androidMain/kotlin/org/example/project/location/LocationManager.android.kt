package org.example.project.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.project.appContext
import java.util.Locale
import kotlin.coroutines.resume

class AndroidLocationManager(
    private val context: Context
) : LocationManager {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    override suspend fun getCurrentAddress(): UserAddress? {
        return try {
            val hasFinePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val hasCoarsePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasFinePermission && !hasCoarsePermission) {
                return null
            }

            val location = fetchLocation() ?: return null
            reverseGeocode(location)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchLocation(): Location? {
        return suspendCancellableCoroutine<Location?> { continuation ->
            try {
                // Try last known location first
                fusedLocationClient.lastLocation.addOnSuccessListener(OnSuccessListener<Location?> { lastLoc ->
                    if (lastLoc != null) {
                        if (continuation.isActive) {
                            continuation.resume(lastLoc)
                        }
                    } else {
                        // Request fresh location if last location is null
                        val cts = CancellationTokenSource()
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            cts.token
                        ).addOnSuccessListener(OnSuccessListener<Location?> { freshLoc ->
                            if (continuation.isActive) {
                                continuation.resume(freshLoc)
                            }
                        }).addOnFailureListener(OnFailureListener {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        })
                        continuation.invokeOnCancellation {
                            cts.cancel()
                        }
                    }
                }).addOnFailureListener(OnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                })
            } catch (_: Exception) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }

    private suspend fun reverseGeocode(location: Location): UserAddress? {
        if (!Geocoder.isPresent()) return null

        return suspendCancellableCoroutine<UserAddress?> { continuation ->
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                val address = addresses.firstOrNull()
                                val userAddress = if (address != null) {
                                    val area = address.subLocality ?: address.thoroughfare ?: address.featureName
                                    val city = address.locality ?: address.subAdminArea
                                    val district = address.subAdminArea ?: address.locality
                                    val state = address.adminArea
                                    UserAddress(
                                        area = area,
                                        city = city,
                                        district = district,
                                        state = state
                                    )
                                } else null
                                if (continuation.isActive) {
                                    continuation.resume(userAddress)
                                }
                            }

                            override fun onError(errorMessage: String?) {
                                if (continuation.isActive) {
                                    continuation.resume(null)
                                }
                            }
                        }
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val address = addresses?.firstOrNull()
                    val userAddress = if (address != null) {
                        val area = address.subLocality ?: address.thoroughfare ?: address.featureName
                        val city = address.locality ?: address.subAdminArea
                        val district = address.subAdminArea ?: address.locality
                        val state = address.adminArea
                        UserAddress(
                            area = area,
                            city = city,
                            district = district,
                            state = state
                        )
                    } else null
                    if (continuation.isActive) {
                        continuation.resume(userAddress)
                    }
                }
            } catch (_: Exception) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }
}

actual fun createLocationManager(): LocationManager = AndroidLocationManager(appContext)
