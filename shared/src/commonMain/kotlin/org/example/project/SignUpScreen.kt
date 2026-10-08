package org.example.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.example.project.viewmodel.PhoneNumberViewModel

/**
 * Premium Sign Up Screen styled to match design mockups.
 *
 * @param onNavigateToLogin Callback to navigate to the Login screen.
 * @param onSignUpSuccess Callback invoked when the user successfully registers.
 * @param onNavigateToTermsAndPrivacy Callback invoked when user clicks Terms or Privacy links.
 * @param modifier Layout modifier.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: (String) -> Unit,
    onNavigateToTermsAndPrivacy: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = org.koin.compose.koinInject(),
    phoneViewModel: PhoneNumberViewModel = org.koin.compose.koinInject()
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val selectedCountry by phoneViewModel.selectedCountry.collectAsState()
    val countriesState by phoneViewModel.countriesState.collectAsState()
    val searchQuery by phoneViewModel.searchQuery.collectAsState()
    val validationInfo by phoneViewModel.validationInfo.collectAsState()

    val uiState by viewModel.uiState.collectAsState()

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
                    value = uiState.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    title = "Name",
                    isMandatory = false,
                    isSecure = false,
                    placeholder = "Enter Name"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // State Dropdown
                AppDropdown(
                    value = uiState.selectedState ?: "",
                    onValueChange = { viewModel.onSelectState(it) },
                    title = "State",
                    options = uiState.states,
                    getLabel = { it.name },
                    isMandatory = true,
                    placeholder = "Select State"
                )

                Spacer(modifier = Modifier.height(20.dp))

                AppPhoneField(
                    mobileNumber = uiState.phone,
                    onMobileNumberChange = {
                        viewModel.onPhoneChange(it)
                    },
                    selectedCountry = selectedCountry,
                    onCountrySelected = {

                    },
                    countriesState = countriesState,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { phoneViewModel.onSearchQueryChange(it) },
                    onRetry = { phoneViewModel.loadCountries() },
                    validationInfo = validationInfo,
                    title = "Mobile Number",
                    isMandatory = false,
                    placeholder = "0000000000"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Terms and Privacy Checkbox Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = uiState.isTermsAccepted,
                        onCheckedChange = { viewModel.onTermsAcceptedChange(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF6366F1),
                            uncheckedColor = Color(0xFFCBD5E1),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val termsText = remember {
                        buildAnnotatedString {
                            append("I've read and agree with the ")
                            pushStringAnnotation(tag = "TERMS", annotation = "terms")
                            withStyle(style = SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.SemiBold)) {
                                append("Terms and Conditions")
                            }
                            pop()
                            append(" and the ")
                            pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                            withStyle(style = SpanStyle(color = Color(0xFF6366F1), fontWeight = FontWeight.SemiBold)) {
                                append("Privacy Policy.")
                            }
                            pop()
                        }
                    }
                    @Suppress("DEPRECATION")
                    ClickableText(
                        text = termsText,
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            letterSpacing = 0.01.em
                        ),
                        modifier = Modifier.weight(1f),
                        onClick = { offset ->
                            val termsClicked = termsText.getStringAnnotations(tag = "TERMS", start = offset, end = offset).isNotEmpty()
                            val privacyClicked = termsText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset).isNotEmpty()
                            if (termsClicked) {
                                onNavigateToTermsAndPrivacy("terms")
                            } else if (privacyClicked) {
                                onNavigateToTermsAndPrivacy("privacy")
                            } else {
                                viewModel.onTermsAcceptedChange(!uiState.isTermsAccepted)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))


                AppPrimaryButton(
                    title = "Send OTP",
                    enabled = !uiState.isLoading,
                    isLoading = uiState.isLoading,
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        viewModel.onSignUpClick(onSignUpSuccess)
                    }
                )
            }
        }

        BaseToastHost(viewModel = viewModel)
    }
}
