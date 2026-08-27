package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.viewmodel.PhoneNumberViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Premium Sign Up Screen styled to match design mockups.
 *
 * @param onNavigateToLogin Callback to navigate to the Login screen.
 * @param onSignUpSuccess Callback invoked when the user successfully registers.
 * @param modifier Layout modifier.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = run {
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

    LaunchedEffect(viewModel.showToast) {
        if (viewModel.showToast) {
            delay(3000)
            viewModel.showToast = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
                    .padding(paddingValues)
                    .statusBarsPadding()
                    .padding(top = 56.dp)
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Sign Up",
                    letterSpacing = (-0.02).em,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val annotatedString = buildAnnotatedString {
                    append("Already have an account ? ")
                    withStyle(style = SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)) {
                        append("Login Now")
                    }
                }

                Text(
                    text = annotatedString,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.01.em,
                    color = Color(0xFF334155),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Name Field
                AppTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    title = "Name",
                    isMandatory = false,
                    isSecure = false,
                    placeholder = "Luc"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // State Dropdown
                AppDropdown(
                    value = viewModel.selectedState,
                    onValueChange = { viewModel.selectedState = it },
                    title = "State",
                    options = viewModel.statesList,
                    isMandatory = false,
                    placeholder = "Select State"
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                    placeholder = ""
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Terms and Privacy Checkbox Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.agreed = !viewModel.agreed }
                ) {
                    Checkbox(
                        checked = viewModel.agreed,
                        onCheckedChange = { viewModel.agreed = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF6366F1),
                            uncheckedColor = Color(0xFFCBD5E1),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val termsText = buildAnnotatedString {
                        append("I've read and agree with the ")
                        withStyle(style = SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.Medium)) {
                            append("Terms and Conditions")
                        }
                        append(" and the ")
                        withStyle(style = SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.Medium)) {
                            append("Privacy Policy.")
                        }
                    }
                    Text(
                        text = termsText,
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(1f),
                        letterSpacing = 0.01.em
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        viewModel.onSignUpClick(onSignUpSuccess)
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
                        text = "Send OTP",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.01.em
                    )
                }
            }
        }

        ToastHost(
            visible = viewModel.showToast,
            type = ToastType.ERROR,
            title = "Registration Error",
            message = viewModel.toastMessage,
            onDismiss = { viewModel.showToast = false }
        )
    }
}
