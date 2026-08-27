package org.example.project.location

interface LocationManager {
    suspend fun getCurrentAddress(): UserAddress?
}

expect fun createLocationManager(): LocationManager
