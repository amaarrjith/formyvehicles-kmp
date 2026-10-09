package org.example.project

import org.example.project.data.remote.api.AuthApiService
import org.example.project.data.remote.api.AuthApiServiceImpl
import org.example.project.data.remote.api.GenericApiService
import org.example.project.data.remote.api.GenericApiServiceImpl
import org.example.project.data.remote.api.UserApiService
import org.example.project.data.remote.api.UserApiServiceImpl
import org.example.project.data.remote.api.VehicleApiService
import org.example.project.data.remote.api.VehicleApiServiceImpl
import org.example.project.data.repository.AuthRepositoryImpl
import org.example.project.data.repository.GenericRepositoryImpl
import org.example.project.data.repository.UserRepositoryImpl
import org.example.project.data.repository.VehicleRepositoryImpl
import org.example.project.data.settings.AppPreferences
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.GenericRepository
import org.example.project.domain.repository.UserRepository
import org.example.project.domain.repository.VehicleRepository
import org.example.project.location.LocationManager
import org.example.project.location.createLocationManager
import org.example.project.network.CountryApi
import org.example.project.network.createHttpClient
import org.example.project.phone.PhoneNumberValidator
import org.example.project.repository.CountryRepository
import org.example.project.viewmodel.OthersViewModel
import org.example.project.viewmodel.PhoneNumberViewModel
import org.example.project.viewmodel.PrivacyPolicyViewModel
import org.koin.dsl.module

val appModule = module {
    single<LocationManager> { createLocationManager() }
    single { AuthPreferences() }
    single { AppPreferences() }
    factory { createHttpClient(get(), get()) }
    factory<AuthApiService> { AuthApiServiceImpl(get()) }
    factory<AuthRepository> { AuthRepositoryImpl(get()) }
    factory<UserApiService> { UserApiServiceImpl(get()) }
    factory<UserRepository> { UserRepositoryImpl(get()) }
    factory<VehicleApiService> { VehicleApiServiceImpl(get()) }
    factory<VehicleRepository> { VehicleRepositoryImpl(get()) }
    factory<GenericApiService> { GenericApiServiceImpl(get()) }
    factory<GenericRepository> { GenericRepositoryImpl(get()) }
    single { CountryApi() }
    single { CountryRepository(get()) }
    single { PhoneNumberValidator() }
    factory { PhoneNumberViewModel(get(), get()) }
    factory { LoginViewModel(get()) }
    factory { SignUpViewModel(get(), get()) }
    factory { OtpVerificationViewModel(get(), get()) }
    factory { HomeViewModel(get(), get()) }
    factory { VehicleStatusViewModel(get(), get()) }
    factory { OthersViewModel(get()) }
    factory { PrivacyPolicyViewModel(get()) }
}
