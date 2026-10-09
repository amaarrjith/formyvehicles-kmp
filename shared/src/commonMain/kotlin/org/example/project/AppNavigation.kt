package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.delay
import org.example.project.manager.AppManager
import org.example.project.manager.LogoutEvent

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var logoutToast by remember { mutableStateOf<LogoutEvent?>(null) }
    var isToastVisible by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        AppManager.logoutEvents.collect { event ->
            navController.navigate(AppScreens.WelcomeScreen.route) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            delay(350)
            logoutToast = event
            isToastVisible = true
            AppManager.resetLogoutState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

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
                    AppManager.logout(
                        title = "Logged Out",
                        message = "You have been logged out successfully.",
                        type = ToastType.INFO
                    )
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
                    AppManager.logout(
                        title = "Logged Out",
                        message = "You have been logged out successfully.",
                        type = ToastType.INFO
                    )
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
            val regNumber = backStackEntry.savedStateHandle.get<String>("regNumber") ?: ""
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
            val tab = backStackEntry.savedStateHandle.get<String>("tab") ?: "privacy"
            PrivacyPolicyScreen(
                initialTab = tab,
                onBackClick = { navController.popBackStack() }
            )
        }
    }

    ToastHost(
        visible = isToastVisible,
        type = logoutToast?.type ?: ToastType.WARNING,
        title = logoutToast?.title ?: "Session Expired",
        message = logoutToast?.message ?: "",
        onDismiss = { isToastVisible = false }
    )
}
}