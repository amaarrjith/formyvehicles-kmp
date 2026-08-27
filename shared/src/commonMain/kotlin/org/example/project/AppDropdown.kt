package org.example.project

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * An ultra-modern, custom reusable dropdown selector styled according to premium app design guidelines.
 *
 * @param value The currently selected value.
 * @param onValueChange Callback invoked when an option is selected.
 * @param title The title/label displayed above the dropdown.
 * @param options List of string options to display in the dropdown menu.
 * @param isMandatory If true, appends a red asterisk to the title.
 * @param modifier The modifier for this dropdown layout.
 * @param placeholder The placeholder text when no option is selected.
 */
@Composable
fun AppDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    options: List<String>,
    isMandatory: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "Select Option"
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 250)
    )

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
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B) // Dark slate from Figma
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            // Dropdown Input Pill Container matching Figma
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (expanded) Color(0xFF6366F1) else Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        focusManager.clearFocus()
                        expanded = !expanded
                    }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Selected Value or Placeholder
                Text(
                    text = if (value.isNotEmpty()) value else placeholder,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (value.isNotEmpty()) Color(0xFF1E293B) else Color(0xFF94A3B8),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Clean Downward Chevron Arrow (matching Figma)
                Canvas(
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(rotationAngle)
                ) {
                    val path = Path().apply {
                        moveTo(size.width * 0.2f, size.height * 0.38f)
                        lineTo(size.width * 0.5f, size.height * 0.68f)
                        lineTo(size.width * 0.8f, size.height * 0.38f)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF1E293B),
                        style = Stroke(
                            width = 1.8.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // Modern Dropdown Popup Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(Color.White)
            ) {
                options.forEach { option ->
                    val isSelected = option == value
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF6366F1) else Color(0xFF334155)
                                )

                                if (isSelected) {
                                    Canvas(modifier = Modifier.size(16.dp)) {
                                        val checkPath = Path().apply {
                                            moveTo(size.width * 0.2f, size.height * 0.5f)
                                            lineTo(size.width * 0.45f, size.height * 0.75f)
                                            lineTo(size.width * 0.85f, size.height * 0.25f)
                                        }
                                        drawPath(
                                            path = checkPath,
                                            color = Color(0xFF6366F1),
                                            style = Stroke(
                                                width = 2.5.dp.toPx(),
                                                cap = StrokeCap.Round,
                                                join = StrokeJoin.Round
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        onClick = {
                            focusManager.clearFocus()
                            onValueChange(option)
                            expanded = false
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFFEEF2FF) else Color.Transparent)
                    )
                }
            }
        }
    }
}
