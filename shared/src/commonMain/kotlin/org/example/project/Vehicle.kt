package org.example.project

data class Vehicle(
    val registrationNumber: String,
    val vehicleType: String, // "Car" or "Bike"
    val brand: String,
    val model: String,
    val year: Int,
    val fuelType: String,
    val gearType: String,
    val imageRes: String? = null
)
