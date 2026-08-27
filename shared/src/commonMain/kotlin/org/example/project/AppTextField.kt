package org.example.project

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * A custom reusable text field styled according to premium design guidelines.
 *
 * @param value The text input value.
 * @param onValueChange Callback invoked when the input changes.
 * @param title The title/label displayed above the input.
 * @param isMandatory If true, appends a red asterisk to the title.
 * @param isSecure If true, masks the text and displays a visibility toggle trailing icon.
 * @param keyboardType Optional KeyboardType parameter (defaults to KeyboardType.Text).
 * @param modifier The modifier for this text field layout.
 * @param placeholder The placeholder text when the field is empty.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    isMandatory: Boolean,
    isSecure: Boolean,
    keyboardType: KeyboardType? = null,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label with an optional red asterisk indicating a mandatory field
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
            letterSpacing = 0.01.em,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B) // Dark slate color from Figma
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(text = placeholder, color = Color(0xFF94A3B8), fontSize = 15.sp) }
            } else {
                null
            },
            singleLine = true,
            readOnly = readOnly,
            visualTransformation = if (isSecure && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType ?: KeyboardType.Text
            ),
            trailingIcon = trailingIcon ?: (if (isSecure) {
                {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "Hide" else "Show",
                            color = Color(0xFF6366F1), // Elegant Indigo color
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else null),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6366F1),     // Indigo focus border
                unfocusedBorderColor = Color(0xFFE2E8F0),   // Light grey border from Figma
                disabledBorderColor = Color(0xFFE2E8F0),
                errorBorderColor = Color(0xFFEF4444),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF1E293B),
                unfocusedTextColor = Color(0xFF1E293B),
                cursorColor = Color(0xFF6366F1)
            )
        )
    }
}
