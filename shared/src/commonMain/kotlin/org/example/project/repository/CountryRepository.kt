package org.example.project.repository

import org.example.project.AppDatabase
import org.example.project.model.Country
import org.example.project.network.CountryApi

class CountryRepository(
    private val api: CountryApi,
    private val db: AppDatabase
) {
    val fallbackCountries = listOf(
        Country("India", "IN", "🇮🇳", "+91"),
        Country("United States", "US", "🇺🇸", "+1"),
        Country("United Kingdom", "GB", "🇬🇧", "+44"),
        Country("United Arab Emirates", "AE", "🇦🇪", "+971"),
        Country("Australia", "AU", "🇦🇺", "+61"),
        Country("Canada", "CA", "🇨🇦", "+1"),
        Country("Singapore", "SG", "🇸🇬", "+65"),
        Country("Germany", "DE", "🇩🇪", "+49"),
        Country("France", "FR", "🇫🇷", "+33"),
        Country("Japan", "JP", "🇯🇵", "+81"),
        Country("Saudi Arabia", "SA", "🇸🇦", "+966"),
        Country("Qatar", "QA", "🇶🇦", "+974"),
        Country("Kuwait", "KW", "🇰🇼", "+965"),
        Country("Oman", "OM", "🇴🇲", "+968"),
        Country("Malaysia", "MY", "🇲🇾", "+60"),
        Country("New Zealand", "NZ", "🇳🇿", "+64"),
        Country("Brazil", "BR", "🇧🇷", "+55"),
        Country("South Africa", "ZA", "🇿🇦", "+27")
    )

    private fun ensureTableExists() {
        try {
            db.appDatabaseQueries.createCountryTable()
        } catch (t: Throwable) {
            // Ignore table creation error if already created
        }
    }

    suspend fun getCountries(): Result<List<Country>> {
        ensureTableExists()
        println("[CountryRepository] 🚀 Attempting to fetch country list...")
        return try {
            val remoteCountries = api.fetchCountries()
            if (remoteCountries.isNotEmpty()) {
                println("[CountryRepository] 💾 Caching ${remoteCountries.size} countries into local SQLDelight database...")
                try {
                    db.appDatabaseQueries.transaction {
                        db.appDatabaseQueries.deleteAllCountries()
                        remoteCountries.forEach { country ->
                            db.appDatabaseQueries.insertCountry(
                                isoCode = country.isoCode,
                                name = country.name,
                                flag = country.flag,
                                dialCode = country.dialCode
                            )
                        }
                    }
                    println("[CountryRepository] ✅ Local SQLDelight cache updated successfully.")
                } catch (t: Throwable) {
                    println("[CountryRepository] ⚠️ Failed to write cache to database: ${t.message}")
                }
                Result.success(remoteCountries)
            } else {
                println("[CountryRepository] ⚠️ API returned empty list. Falling back to local cache.")
                getCachedOrFallback()
            }
        } catch (t: Throwable) {
            println("[CountryRepository] ❌ Network request failed: ${t.message}. Falling back to cached/default countries.")
            getCachedOrFallback()
        }
    }

    private fun getCachedOrFallback(): Result<List<Country>> {
        ensureTableExists()
        return try {
            val cachedEntities = db.appDatabaseQueries.selectAllCountries().executeAsList()
            if (cachedEntities.isNotEmpty()) {
                println("[CountryRepository] 📂 Loaded ${cachedEntities.size} countries from local SQLDelight cache.")
                val cachedCountries = cachedEntities.map { entity ->
                    Country(
                        isoCode = entity.isoCode,
                        name = entity.name,
                        flag = entity.flag,
                        dialCode = entity.dialCode
                    )
                }
                Result.success(cachedCountries)
            } else {
                println("[CountryRepository] 📋 Database cache empty. Returning ${fallbackCountries.size} fallback countries.")
                Result.success(fallbackCountries)
            }
        } catch (t: Throwable) {
            println("[CountryRepository] ⚠️ Could not query database: ${t.message}. Returning fallback countries.")
            Result.success(fallbackCountries)
        }
    }
}
