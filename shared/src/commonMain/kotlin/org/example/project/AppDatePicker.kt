package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePicker(
    value: String,
    onDateSelected: (String) -> Unit,
    title: String = "Schedule Date",
    isMandatory: Boolean = true,
    placeholder: String = "Ex. 16 Jan 2025",
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val epochDay1970 = (millis / 86400000L).toInt()
                        val customEpochDays = epochDay1970 - 18262 + 1
                        val formatted = dateStringFromEpochDays(customEpochDays)
                        onDateSelected(formatted)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        AppTextField(
            value = value,
            onValueChange = {},
            title = title,
            isMandatory = isMandatory,
            isSecure = false,
            placeholder = placeholder,
            readOnly = true,
            trailingIcon = {
                Canvas(modifier = Modifier.size(20.dp)) {
                    val s = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    val c = Color(0xFF64748B)
                    drawRect(
                        color = c,
                        topLeft = Offset(size.width * 0.1f, size.height * 0.25f),
                        size = Size(size.width * 0.8f, size.height * 0.65f),
                        style = s
                    )
                    drawLine(
                        c,
                        Offset(size.width * 0.1f, size.height * 0.4f),
                        Offset(size.width * 0.9f, size.height * 0.4f),
                        strokeWidth = 1.5.dp.toPx()
                    )
                    drawLine(
                        c,
                        Offset(size.width * 0.3f, size.height * 0.15f),
                        Offset(size.width * 0.3f, size.height * 0.35f),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        c,
                        Offset(size.width * 0.7f, size.height * 0.15f),
                        Offset(size.width * 0.7f, size.height * 0.35f),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePicker = true }
        )
    }
}
