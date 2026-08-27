package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.sp
import formyvehiclesai.shared.generated.resources.Res
import formyvehiclesai.shared.generated.resources.ic_search
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String? = null,
    isMandatory: Boolean = false,
    isSecure: Boolean = false,
    keyboardType: KeyboardType? = null,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = {
        Image(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = "Search"
        )
    },
    trailingIcon: @Composable (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label with an optional red asterisk indicating a mandatory field
        if (!title.isNullOrEmpty()) {
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
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(text = placeholder, color = Color(0xFF94A3B8), fontSize = 14.sp) }
            } else null,
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
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedBorderColor = Color(0xFFCBD5E1),
                disabledBorderColor = Color(0xFFE2E8F0),
                errorBorderColor = Color(0xFFEF4444),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color(0xFF1E293B),
                unfocusedTextColor = Color(0xFF1E293B)
            )
        )
    }
}