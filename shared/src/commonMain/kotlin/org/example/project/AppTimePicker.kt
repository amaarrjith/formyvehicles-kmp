package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePicker(
    value: String,
    onTimeSelected: (String) -> Unit,
    title: String = "Schedule Time",
    isMandatory: Boolean = true,
    placeholder: String = "Ex. 10:30 PM",
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val isPm = hour >= 12
                    val displayHour = when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                    }
                    val amPm = if (isPm) "PM" else "AM"
                    val formatted = "${displayHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} $amPm"
                    onTimeSelected(formatted)
                    showTimePicker = false
                }) { Text("OK", color = Color(0xFF6366F1), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel", color = Color(0xFF64748B)) }
            },
            title = { Text("Select Alert Time", color = Color(0xFF1E293B), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
            text = { TimePicker(state = timePickerState) },
            containerColor = Color.White
        )
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
                    val c = Color(0xFF64748B)
                    drawCircle(
                        color = c,
                        radius = size.minDimension * 0.42f,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawLine(
                        c,
                        Offset(size.width * 0.5f, size.height * 0.5f),
                        Offset(size.width * 0.5f, size.height * 0.28f),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        c,
                        Offset(size.width * 0.5f, size.height * 0.5f),
                        Offset(size.width * 0.72f, size.height * 0.5f),
                        strokeWidth = 1.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showTimePicker = true }
        )
    }
}
