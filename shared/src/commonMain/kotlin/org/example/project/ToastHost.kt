
package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

enum class ToastType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

data class ToastData(
    val id: Long = 0L,
    val type: ToastType,
    val title: String,
    val message: String
)

@Composable
fun ToastHost(
    visible: Boolean,
    type: ToastType,
    title: Any,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(visible, type, title, message) {
        if (visible) {
            delay(3500)
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it }
            ) + fadeOut()
        ) {
            ToastCard(
                toast = ToastData(
                    type = type,
                    title = title as String,
                    message = message
                ),
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
fun ToastCard(
    toast: ToastData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor: Color
    val backgroundColor: Color
    val borderColor: Color

    when (toast.type) {
        ToastType.SUCCESS -> {
            accentColor = Color(0xFF4DCE78)
            backgroundColor = Color(0xFFF0F9F2)
            borderColor = Color(0xFF4ADE80)
        }

        ToastType.INFO -> {
            accentColor = Color(0xFF378BE5)
            backgroundColor = Color(0xFFEDF5FF)
            borderColor = Color(0xFF378BE5)
        }

        ToastType.WARNING -> {
            accentColor = Color(0xFFF4BF22)
            backgroundColor = Color(0xFFFFFAE9)
            borderColor = Color(0xFFEFC52A)
        }

        ToastType.ERROR -> {
            accentColor = Color(0xFFED5C60)
            backgroundColor = Color(0xFFFFF0EE)
            borderColor = Color(0xFFF16A6A)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 10.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ToastIcon(
                type = toast.type,
                modifier = Modifier.size(44.dp),
                accentColor = accentColor
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = toast.message,
                    color = Color(0xFF666666),
                    fontSize = 12.sp,
                )
            }

//            Box(
//                modifier = Modifier
//                    .size(36.dp)
//                    .clickable(onClick = onDismiss),
//                contentAlignment = Alignment.Center
//            ) {
//                Canvas(
//                    modifier = Modifier.size(18.dp)
//                ) {
//                    val inset = size.minDimension * 0.2f
//
//                    drawLine(
//                        color = Color(0xFF777777),
//                        start = Offset(inset, inset),
//                        end = Offset(
//                            size.width - inset,
//                            size.height - inset
//                        ),
//                        strokeWidth = 1.8.dp.toPx(),
//                        cap = StrokeCap.Round
//                    )
//
//                    drawLine(
//                        color = Color(0xFF777777),
//                        start = Offset(
//                            size.width - inset,
//                            inset
//                        ),
//                        end = Offset(
//                            inset,
//                            size.height - inset
//                        ),
//                        strokeWidth = 1.8.dp.toPx(),
//                        cap = StrokeCap.Round
//                    )
//                }
//            }
        }
    }
}

@Composable
fun ToastIcon(
    type: ToastType,
    modifier: Modifier = Modifier,
    accentColor: Color
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = accentColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                val strokeWidth = 2.2.dp.toPx()

                when (type) {
                    ToastType.SUCCESS -> {
                        val path = Path().apply {
                            moveTo(
                                size.width * 0.16f,
                                size.height * 0.50f
                            )
                            lineTo(
                                size.width * 0.40f,
                                size.height * 0.74f
                            )
                            lineTo(
                                size.width * 0.84f,
                                size.height * 0.26f
                            )
                        }

                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    ToastType.ERROR -> {
                        drawLine(
                            color = Color.White,
                            start = Offset(
                                size.width * 0.28f,
                                size.height * 0.28f
                            ),
                            end = Offset(
                                size.width * 0.72f,
                                size.height * 0.72f
                            ),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )

                        drawLine(
                            color = Color.White,
                            start = Offset(
                                size.width * 0.72f,
                                size.height * 0.28f
                            ),
                            end = Offset(
                                size.width * 0.28f,
                                size.height * 0.72f
                            ),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }

                    ToastType.WARNING -> {
                        drawLine(
                            color = Color.White,
                            start = Offset(
                                size.width / 2f,
                                size.height * 0.20f
                            ),
                            end = Offset(
                                size.width / 2f,
                                size.height * 0.56f
                            ),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )

                        drawCircle(
                            color = Color.White,
                            radius = 1.5.dp.toPx(),
                            center = Offset(
                                size.width / 2f,
                                size.height * 0.78f
                            )
                        )
                    }

                    ToastType.INFO -> {
                        // Lightbulb-style information icon.
                        val bulbPath = Path().apply {
                            moveTo(
                                size.width * 0.32f,
                                size.height * 0.43f
                            )
                            cubicTo(
                                size.width * 0.32f,
                                size.height * 0.15f,
                                size.width * 0.68f,
                                size.height * 0.15f,
                                size.width * 0.68f,
                                size.height * 0.43f
                            )
                            lineTo(
                                size.width * 0.60f,
                                size.height * 0.58f
                            )
                            lineTo(
                                size.width * 0.60f,
                                size.height * 0.65f
                            )
                            lineTo(
                                size.width * 0.40f,
                                size.height * 0.65f
                            )
                            lineTo(
                                size.width * 0.40f,
                                size.height * 0.58f
                            )
                            close()
                        }

                        drawPath(
                            path = bulbPath,
                            color = Color.White,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        drawLine(
                            color = Color.White,
                            start = Offset(
                                size.width * 0.42f,
                                size.height * 0.78f
                            ),
                            end = Offset(
                                size.width * 0.58f,
                                size.height * 0.78f
                            ),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    }
}

