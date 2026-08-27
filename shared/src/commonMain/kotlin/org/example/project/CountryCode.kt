package org.example.project

data class CountryCode(
    val name: String,
    val code: String,
    val flag: String
)

val defaultCountryCodes = listOf(
    CountryCode("India", "+91", "🇮🇳"),
    CountryCode("United States", "+1", "🇺🇸"),
    CountryCode("United Kingdom", "+44", "🇬🇧"),
    CountryCode("United Arab Emirates", "+971", "🇦🇪"),
    CountryCode("Australia", "+61", "🇦🇺"),
    CountryCode("Canada", "+1", "🇨🇦"),
    CountryCode("Singapore", "+65", "🇸🇬"),
    CountryCode("Germany", "+49", "🇩🇪"),
    CountryCode("France", "+33", "🇫🇷"),
    CountryCode("Japan", "+81", "🇯🇵"),
    CountryCode("Saudi Arabia", "+966", "🇸🇦"),
    CountryCode("Qatar", "+974", "🇶🇦"),
    CountryCode("Kuwait", "+965", "🇰🇼"),
    CountryCode("Oman", "+968", "🇴🇲"),
    CountryCode("Malaysia", "+60", "🇲🇾"),
    CountryCode("New Zealand", "+64", "🇳🇿"),
    CountryCode("Brazil", "+55", "🇧🇷"),
    CountryCode("South Africa", "+27", "🇿🇦")
)
