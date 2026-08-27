package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.model.Country
import org.example.project.model.CountryApiDto

class CountryApi(
    private val client: HttpClient = createDefaultHttpClient()
) {
    suspend fun fetchCountries(): List<Country> {
        println("[CountryApi] 🌐 Requesting countries from https://countries.dev/countries ...")
        val dtos: List<CountryApiDto> = client.get("https://countries.dev/countries").body()
        println("[CountryApi] 📥 Received ${dtos.size} country DTO objects from API.")
        val result = dtos.mapNotNull { dto ->
            val iso = dto.alpha2Code?.trim()?.uppercase()
            val name = dto.name?.trim()
            val callingCode = dto.callingCodes?.firstOrNull { it.isNotBlank() }?.trim()
            val flag = dto.flag?.trim() ?: ""

            if (!iso.isNullOrEmpty() && !name.isNullOrEmpty() && !callingCode.isNullOrEmpty()) {
                val cleanDial = if (callingCode.startsWith("+")) callingCode else "+$callingCode"
                Country(
                    name = name,
                    isoCode = iso,
                    flag = flag,
                    dialCode = cleanDial
                )
            } else null
        }.sortedBy { it.name }
        println("[CountryApi] ✅ Parsed ${result.size} valid countries with flags and calling codes.")
        return result
    }

    companion object {
        fun createDefaultHttpClient(): HttpClient {
            return HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        isLenient = true
                    })
                }
            }
        }
    }
}
