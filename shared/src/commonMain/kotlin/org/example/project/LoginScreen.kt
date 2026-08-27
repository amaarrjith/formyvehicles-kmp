package org.example.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.viewmodel.PhoneNumberViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope

/**
 * Premium Login Screen styled to match design mockups.
 *
 * @param onNavigateToSignUp Callback to navigate to the Sign Up screen.
 * @param onLoginSuccess Callback invoked when the user successfully submits mobile number.
 * @param modifier Layout modifier.
 */
@Composable
fun LoginScreen(
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = run {
        val koin = org.koin.compose.getKoin()
        androidx.lifecycle.viewmodel.compose.viewModel { koin.get() }
    },
    phoneViewModel: PhoneNumberViewModel = run {
        val koin = org.koin.compose.getKoin()
        androidx.lifecycle.viewmodel.compose.viewModel { koin.get() }
    }
) {
    val focusManager = LocalFocusManager.current
    val selectedCountry by phoneViewModel.selectedCountry.collectAsState()
    val countriesState by phoneViewModel.countriesState.collectAsState()
    val searchQuery by phoneViewModel.searchQuery.collectAsState()
    val validationInfo by phoneViewModel.validationInfo.collectAsState()

    // Disable back swipe / back press on LoginScreen
    AppBackHandler(enabled = true) {
        // Do nothing on back gesture to prevent returning to previous screen
    }

    LaunchedEffect(viewModel.showToast) {
        if (viewModel.showToast) {
            delay(3000)
            viewModel.showToast = false
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(paddingValues)
                .statusBarsPadding()
                .navigationBarsPadding()
//                .imePadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Login",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val annotatedString = buildAnnotatedString {
                append("Don’t have an account? ")
                withStyle(style = SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)) {
                    append("Create Now")
                }
            }

            Text(
                text = annotatedString,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF334155),
                modifier = Modifier.clickable { onNavigateToSignUp() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            AppPhoneField(
                mobileNumber = viewModel.mobileNumber,
                onMobileNumberChange = {
                    viewModel.mobileNumber = it
                    phoneViewModel.onPhoneNumberChange(it)
                },
                selectedCountry = selectedCountry,
                onCountrySelected = {
                    phoneViewModel.selectCountry(it)
                    viewModel.selectedCountry = it
                },
                countriesState = countriesState,
                searchQuery = searchQuery,
                onSearchQueryChange = { phoneViewModel.onSearchQueryChange(it) },
                onRetry = { phoneViewModel.loadCountries() },
                validationInfo = validationInfo,
                title = "Mobile Number",
                isMandatory = false,
                placeholder = "8921731641"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "We’ll send a one-time password (OTP) to your\nregistered mobile number.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF64748B),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    viewModel.onContinueClick(onLoginSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6338F6)
                )
            ) {
                Text(
                    text = "Continue",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            }

            ToastHost(
                visible = viewModel.showToast,
                type = ToastType.ERROR,
                title = "Validation Error",
                message = viewModel.toastMessage,
                onDismiss = { viewModel.showToast = false }
            )
        }
    }
}
