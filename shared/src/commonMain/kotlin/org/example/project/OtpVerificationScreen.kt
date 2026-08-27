package org.example.project

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    emailOrPhone: String = "lincoln_dokidis43@email.com",
    onVerifyClick: (String) -> Unit,
    onResendClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OtpVerificationViewModel = org.koin.compose.koinInject()
) {
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester1.requestFocus()
    }

    LaunchedEffect(viewModel.showToast) {
        if (viewModel.showToast) {
            delay(3000)
            viewModel.onToastDismissed()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.White,
            topBar = {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        AppBackButton {
                            onBackClick()
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "OTP Verification",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A 4-digit code was sent to your email\n$emailOrPhone",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OtpDigitBox(
                            value = viewModel.otp1,
                            onValueChange = { newVal ->
                                viewModel.otp1 = newVal
                                if (newVal.isNotEmpty()) focusRequester2.requestFocus()
                            },
                            focusRequester = focusRequester1,
                            onBackspace = {}
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        OtpDigitBox(
                            value = viewModel.otp2,
                            onValueChange = { newVal ->
                                viewModel.otp2 = newVal
                                if (newVal.isNotEmpty()) {
                                    focusRequester3.requestFocus()
                                } else {
                                    focusRequester1.requestFocus()
                                }
                            },
                            focusRequester = focusRequester2,
                            onBackspace = { focusRequester1.requestFocus() }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        OtpDigitBox(
                            value = viewModel.otp3,
                            onValueChange = { newVal ->
                                viewModel.otp3 = newVal
                                if (newVal.isNotEmpty()) {
                                    focusRequester4.requestFocus()
                                } else {
                                    focusRequester2.requestFocus()
                                }
                            },
                            focusRequester = focusRequester3,
                            onBackspace = { focusRequester2.requestFocus() }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        OtpDigitBox(
                            value = viewModel.otp4,
                            onValueChange = { newVal ->
                                viewModel.otp4 = newVal
                                if (newVal.isEmpty()) {
                                    focusRequester3.requestFocus()
                                }
                            },
                            focusRequester = focusRequester4,
                            onBackspace = { focusRequester3.requestFocus() }
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    val formattedTime = viewModel.formatTimer(viewModel.timerSeconds)
                    val isTimerRunning = viewModel.timerSeconds > 0

                    if (isTimerRunning) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "You can resend OTP after ",
                                fontSize = 15.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = formattedTime,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6338F6)
                            )
                        }
                    } else {
                        Text(
                            text = "Resend code",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6338F6),
                            modifier = Modifier.clickable {
                                viewModel.onResendClick { onResendClick() }
                            }
                        )
                    }
                }

                val otpCode = viewModel.getOtp()
                val isOtpComplete = otpCode.length == 4

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (isOtpComplete) {
                            viewModel.verifyOtp { verifiedOtp ->
                                onVerifyClick(verifiedOtp)
                            }
                        }
                    },
                    enabled = isOtpComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6338F6),
                        disabledContainerColor = Color(0xFFE2E8F0),
                        contentColor = Color.White,
                        disabledContentColor = Color(0xFF94A3B8)
                    )
                ) {
                    Text(
                        text = "Verify & Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        ToastHost(
            visible = viewModel.showToast,
            type = if (viewModel.isErrorToast) ToastType.ERROR else ToastType.SUCCESS,
            title = if (viewModel.isErrorToast) "Verification Failed" else "OTP Verification",
            message = viewModel.toastMessage,
            onDismiss = { viewModel.onToastDismissed() }
        )
    }
}

@Composable
fun OtpDigitBox(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 1) {
                val filtered = newValue.filter { it.isDigit() }
                onValueChange(filtered)
            }
        },
        modifier = modifier
            .size(58.dp)
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace) {
                    if (value.isEmpty()) {
                        onBackspace()
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            },
        textStyle = LocalTextStyle.current.copy(
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF0F172A)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0F172A),
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color(0xFF0F172A),
            unfocusedTextColor = Color(0xFF0F172A),
            cursorColor = Color(0xFF0F172A)
        )
    )
}
