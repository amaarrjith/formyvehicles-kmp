package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.model.Country
import org.example.project.model.CountryPhoneInfo
import org.example.project.model.PhoneValidationState
import org.example.project.ui.CountryPickerModal
import org.example.project.viewmodel.CountryPickerUiState

@Composable
fun AppPhoneField(
    mobileNumber: String,
    onMobileNumberChange: (String) -> Unit,
    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,
    countriesState: CountryPickerUiState = CountryPickerUiState.Success(emptyList()),
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onRetry: () -> Unit = {},
    validationInfo: CountryPhoneInfo = CountryPhoneInfo(PhoneValidationState.Empty, "", "", true),
    title: String = "Mobile Number",
    isMandatory: Boolean = true,
    placeholder: String = "9876543210",
    modifier: Modifier = Modifier
) {
    var isPickerOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label with mandatory asterisk
        val labelText = buildAnnotatedString {
            append(title)
            if (isMandatory) {
                withStyle(style = SpanStyle(color = Color(0xFFEF4444))) {
                    append(" *")
                }
            }
        }

        Text(
            text = labelText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val isError = validationInfo.state == PhoneValidationState.Invalid || validationInfo.state == PhoneValidationState.ParsingError
        val isValid = validationInfo.state == PhoneValidationState.Valid

        OutlinedTextField(
            value = mobileNumber,
            onValueChange = onMobileNumberChange,
            modifier = Modifier.fillMaxWidth(),
            prefix = {
                Text(
                    text = "${selectedCountry.dialCode} ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1E293B)
                )
            },
            placeholder = { Text(text = placeholder, color = Color(0xFF94A3B8), fontSize = 15.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Color(0xFFEF4444) else if (isValid) Color(0xFF10B981) else Color(0xFF6366F1),
                unfocusedBorderColor = if (isError) Color(0xFFEF4444) else if (isValid) Color(0xFF10B981) else Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF1E293B),
                unfocusedTextColor = Color(0xFF1E293B),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color(0xFF6366F1)
            )
        )

        // Validation Feedback Message
        if (validationInfo.message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            val messageColor = when (validationInfo.state) {
                PhoneValidationState.Valid -> Color(0xFF10B981) // Emerald Green
                PhoneValidationState.Invalid, PhoneValidationState.ParsingError -> Color(0xFFEF4444) // Red
                PhoneValidationState.Empty -> Color(0xFF64748B)
            }
            Text(
                text = validationInfo.message,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = messageColor
            )
        }

        // Modal Bottom Sheet Picker
        CountryPickerModal(
            isOpen = isPickerOpen,
            onDismiss = { isPickerOpen = false },
            uiState = countriesState,
            selectedCountry = selectedCountry,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onCountrySelected = onCountrySelected,
            onRetry = onRetry
        )
    }
}
