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

    // Initialize SQLDelight database manager
    remember {
        DatabaseManager.init(DatabaseDriverFactory())
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
                onLoginSuccess = {
                    navController.navigate(AppScreens.OtpVerification.route)
                }
            )
        }
        composable(AppScreens.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(AppScreens.OtpVerification.route)
                }
            )
        }
        composable(AppScreens.OtpVerification.route) {
            val mobile = getPersistedString("logged_in_user_mobile") ?: ""
            val formattedPhone = if (mobile.trim().isNotEmpty()) {
                val clean = mobile.trim()
                if (clean.length >= 4) {
                    "**********${clean.takeLast(4)}"
                } else {
                    "**********$clean"
                }
            } else {
                "**********7644"
            }
            OtpVerificationScreen(
                emailOrPhone = formattedPhone,
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
                    navController.navigate("vehicle_status/${vehicle.registrationNumber}")
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
                    navController.navigate("vehicle_status/${vehicle.registrationNumber}")
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
            val regNumber = backStackEntry.savedStateHandle.get<String>("regNumber") ?: ""
            VehicleStatusScreen(
                regNumber = regNumber,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}