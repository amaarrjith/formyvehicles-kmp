package org.example.project.repository

import org.example.project.model.Country
import org.example.project.network.CountryApi

class CountryRepository(
    private val api: CountryApi
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

    suspend fun getCountries(): Result<List<Country>> {
        return try {
            val remoteCountries = api.fetchCountries()
            if (remoteCountries.isNotEmpty()) {
                Result.success(remoteCountries)
            } else {
                Result.success(fallbackCountries)
            }
        } catch (t: Throwable) {
            println("[CountryRepository] ❌ Network request failed: ${t.message}. Falling back to default countries.")
            Result.success(fallbackCountries)
        }
    }
}
