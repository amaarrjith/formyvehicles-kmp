package org.example.project

import org.example.project.data.remote.api.AuthApiService
import org.example.project.data.remote.api.AuthApiServiceImpl
import org.example.project.data.repository.AuthRepositoryImpl
import org.example.project.data.settings.AppPreferences
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.location.LocationManager
import org.example.project.location.createLocationManager
import org.example.project.network.CountryApi
import org.example.project.network.createHttpClient
import org.example.project.phone.PhoneNumberValidator
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
    single { AuthPreferences() }
    single { AppPreferences() }
    factory { createHttpClient(get(), get()) }
    factory<AuthApiService> { AuthApiServiceImpl(get()) }
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
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
