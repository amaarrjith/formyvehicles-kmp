package org.example.project.repository

import org.example.project.model.Country
import org.example.project.network.CountryApi

class CountryRepository(
    private val api: CountryApi
) {
    suspend fun getCountries(): Result<List<Country>> {
        return try {
            val remoteCountries = api.fetchCountries()
            Result.success(remoteCountries)
        } catch (t: Throwable) {
            println("[CountryRepository] ❌ Network request failed: ${t.message}.")
            Result.failure(t)
        }
    }
}
