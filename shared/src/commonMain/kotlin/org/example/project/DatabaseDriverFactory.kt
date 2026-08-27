package org.example.project

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory() {
    fun createDriver(): SqlDriver
}

object DatabaseManager {
    private var database: AppDatabase? = null

    fun init(driverFactory: DatabaseDriverFactory) {
        if (database == null) {
            database = AppDatabase(driverFactory.createDriver())
            prepopulateMetadata()
        }
    }

    private fun prepopulateMetadata() {
        val db = database ?: return
        val queries = db.appDatabaseQueries
        
        try {
            val existingTypes = queries.selectVehicleTypes().executeAsList()
            if (existingTypes.isEmpty()) {
                // Populate Vehicle Types
                queries.insertVehicleType("Car")
                queries.insertVehicleType("Bike")
                
                // Populate Brands
                queries.insertBrand("Maruthi Suzuki")
                queries.insertBrand("Hero Honda")
                queries.insertBrand("Hyundai")
                queries.insertBrand("Tata")
                queries.insertBrand("Honda")
                
                // Populate Models
                queries.insertModel("Maruthi Suzuki", "Swift VXI Hatchback")
                queries.insertModel("Maruthi Suzuki", "Baleno VXI")
                queries.insertModel("Maruthi Suzuki", "Brezza ZXI")
                queries.insertModel("Hero Honda", "GLAMOUR 125 Fi")
                queries.insertModel("Hero Honda", "Splendor Plus")
                queries.insertModel("Hero Honda", "Passion Pro")
                queries.insertModel("Hyundai", "i20 Asta")
                queries.insertModel("Hyundai", "Creta SX")
                queries.insertModel("Hyundai", "Verna SX")
                queries.insertModel("Tata", "Nexon EV")
                queries.insertModel("Tata", "Harrier XZ")
                queries.insertModel("Tata", "Altroz XZ")
                queries.insertModel("Honda", "City ZX")
                queries.insertModel("Honda", "Civic Hybrid")
                queries.insertModel("Honda", "Amaze VX")
                
                // Populate Fuel Types
                queries.insertFuelType("Petrol")
                queries.insertFuelType("Diesel")
                queries.insertFuelType("Electric")
                queries.insertFuelType("Hybrid")
                
                // Populate Gear Types
                queries.insertGearType("Manual")
                queries.insertGearType("Automatic")

                // Populate Vehicle Colors
                val colors = listOf("Red", "White", "Black", "Silver", "Blue", "Grey", "Yellow", "Green", "Orange", "Brown")
                colors.forEach { queries.insertVehicleColor(it) }

                // Populate Vehicle Categories
                val categories = listOf("Hatchback", "Sedan", "SUV", "MUV", "Coupe", "Convertible", "Pickup Truck", "Scooter", "Motorcycle", "EV")
                categories.forEach { queries.insertVehicleCategory(it) }

                // Populate Seating Capacities
                val seatingCapacities = listOf("2 Seater", "4 Seater", "5 Seater", "6 Seater", "7 Seater", "8+ Seater")
                seatingCapacities.forEach { queries.insertSeatingCapacity(it) }

                // Seed Demo vehicles for both default empty and Krish Maitreyi accounts
                queries.insertUserVehicle(
                    registrationNumber = "KL 56 X 7004",
                    vehicleType = "Car",
                    brand = "Maruthi Suzuki",
                    model = "Swift VXI Hatchback",
                    year = 2022,
                    fuelType = "Petrol",
                    gearType = "Automatic",
                    imageRes = "img_car_swift",
                    userMobile = ""
                )
                queries.insertUserVehicle(
                    registrationNumber = "KL 56 E 6332",
                    vehicleType = "Bike",
                    brand = "Hero Honda",
                    model = "GLAMOUR 125 Fi",
                    year = 2012,
                    fuelType = "Petrol",
                    gearType = "Manual",
                    imageRes = "img_bike_glamour",
                    userMobile = ""
                )
            }
        } catch (e: Exception) {
            // Ignore / Log
        }
    }

    fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException("Database not initialized")
    }
}
