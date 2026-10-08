package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavBackStackEntry
import androidx.core.bundle.Bundle

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        org.example.project.manager.AppManager.logoutEvents.collect {
            navController.navigate(AppScreens.WelcomeScreen.route) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route
    ) {
        composable(AppScreens.Splash.route) {
            AppSplashScreen(
                onSplashCompleted = {
                    val nextRoute = if (isUserLoggedIn() || isGuestUser()) AppScreens.Home.route else AppScreens.WelcomeScreen.route
                    navController.navigate(nextRoute) {
                        popUpTo(AppScreens.Splash.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppScreens.WelcomeScreen.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    setGuestUser(false)
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(AppScreens.WelcomeScreen.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToGuest = {
                    setGuestUser(true)
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.WelcomeScreen.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppScreens.Login.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(AppScreens.SignUp.route)
                },
                onLoginSuccess = { mobile ->
                    navController.navigate(AppScreens.otpVerification(mobile))
                }
            )
        }
        composable(AppScreens.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = { mobile ->
                    navController.navigate(AppScreens.otpVerification(mobile))
                },
                onNavigateToTermsAndPrivacy = { tab ->
                    navController.navigate(AppScreens.termsAndPrivacy(tab))
                }
            )
        }
        composable(
            route = AppScreens.OtpVerification.route,
            arguments = listOf(
                androidx.navigation.navArgument("mobile") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val rawMobile = backStackEntry.savedStateHandle.get<String>("mobile")
                ?: ""
            val mobile = if (rawMobile == "unknown" || rawMobile.isBlank()) {
                getPersistedString("logged_in_user_mobile") ?: ""
            } else {
                rawMobile
            }
            OtpVerificationScreen(
                emailOrPhone = mobile,
                onVerifyClick = { otp ->
                    setGuestUser(false)
                    setUserLoggedIn(true)
                    navController.navigate(AppScreens.Home.route) {
                        popUpTo(AppScreens.Splash.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onResendClick = {
                    // Handle OTP resend action
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(AppScreens.Home.route) {
            AppTabBar(
                onAddVehicleClick = {
                    // Handle add vehicle action
                },
                onVehicleClick = { vehicle ->
                    navController.navigate(AppScreens.vehicleStatus(vehicle.registrationNumber))
                },
                onLogoutClick = {
                    setGuestUser(false)
                    setUserLoggedIn(false)
                    navController.navigate(AppScreens.WelcomeScreen.route) {
                        popUpTo(AppScreens.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    setGuestUser(false)
                    navController.navigate(AppScreens.WelcomeScreen.route) {
                        popUpTo(AppScreens.Home.route) {
                            inclusive = true
                        }
                    }
                    navController.navigate(AppScreens.Login.route) {
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(AppScreens.Settings.route)
                },
                onNotificationClick = {
                    navController.navigate(AppScreens.Notifications.route)
                },
                onViewAllClick = {
                    navController.navigate(AppScreens.MyVehicles.route)
                }
            )
        }
        composable(AppScreens.Settings.route) {
            SettingsScreen(
                onLogoutClick = {
                    setGuestUser(false)
                    setUserLoggedIn(false)
                    navController.navigate(AppScreens.WelcomeScreen.route) {
                        popUpTo(AppScreens.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onTermsAndPrivacyClick = { tab ->
                    navController.navigate(AppScreens.termsAndPrivacy(tab))
                }
            )
        }
        composable(AppScreens.Notifications.route) {
            NotificationsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(AppScreens.MyVehicles.route) {
            MyVehiclesScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onVehicleClick = { vehicle ->
                    navController.navigate(AppScreens.vehicleStatus(vehicle.registrationNumber))
                }
            )
        }
        composable(
            route = AppScreens.VehicleStatus.route,
            arguments = listOf(
                androidx.navigation.navArgument("regNumber") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val regNumber = backStackEntry.arguments?.getString("regNumber")
                ?: backStackEntry.savedStateHandle.get<String>("regNumber")
                ?: ""
            VehicleStatusScreen(
                regNumber = regNumber,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = AppScreens.PrivacyPolicy.route,
            arguments = listOf(
                androidx.navigation.navArgument("tab") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = "privacy"
                }
            )
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab")
                ?: backStackEntry.savedStateHandle.get<String>("tab")
                ?: "privacy"
            PrivacyPolicyScreen(
                initialTab = tab,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}