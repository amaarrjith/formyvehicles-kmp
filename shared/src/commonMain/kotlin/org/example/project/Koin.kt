package org.example.project

import org.example.project.network.CountryApi
import org.example.project.phone.PhoneNumberValidator
import org.example.project.location.LocationManager
import org.example.project.location.createLocationManager
import org.example.project.repository.CountryRepository
import org.example.project.viewmodel.OthersViewModel
import org.example.project.viewmodel.PhoneNumberViewModel
import org.koin.dsl.module

val appModule = module {
    single { DatabaseDriverFactory() }
    single { 
        val driverFactory: DatabaseDriverFactory = get()
        DatabaseManager.init(driverFactory)
        DatabaseManager.getDatabase()
    }
    single<LocationManager> { createLocationManager() }
    single { CountryApi() }
    single { CountryRepository(get(), get()) }
    single { PhoneNumberValidator() }
    factory { PhoneNumberViewModel(get(), get()) }
    factory { LoginViewModel(get()) }
    factory { SignUpViewModel(get()) }
    factory { OtpVerificationViewModel() }
    factory { HomeViewModel(get()) }
    factory { VehicleStatusViewModel(get()) }
    factory { OthersViewModel(get()) }
}
