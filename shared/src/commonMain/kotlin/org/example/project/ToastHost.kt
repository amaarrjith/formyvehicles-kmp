package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

/**
 * Supported toast types.
 */
enum class ToastType {
    INFO, ERROR, SUCCESS
}

/**
 * Data model for holding toast content.
 */
data class ToastData(
    val id: Long = Random.nextLong(),
    val type: ToastType,
    val title: String,
    val message: String
)

/**
 * Composable container that hosts the Toast notifications and animates them
 * into the bottom-left corner of the screen when [visible] is true.
 *
 * @param visible If true, the toast is visible.
 * @param type The type of toast (INFO, ERROR, SUCCESS).
 * @param title The bold title string.
 * @param message The details body string.
 * @param onDismiss Callback invoked when the user dismisses the toast.
 * @param modifier Layout modifier.
 */
@Composable
fun ToastHost(
    visible: Boolean,
    type: ToastType,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomStart
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
            ToastCard(
                toast = ToastData(type = type, title = title, message = message),
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
    Surface(
        modifier = modifier
            .widthIn(max = 340.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F172A) // Slate-900 dark background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Self-contained custom Canvas vector icon
            ToastIcon(type = toast.type, modifier = Modifier.padding(top = 2.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toast.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = toast.message,
                    color = Color(0xFF94A3B8), // Slate-400
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            // Close button (X) drawn using Canvas
            Canvas(
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onDismiss() }
                    .padding(4.dp)
            ) {
                drawLine(
                    color = Color(0xFF64748B), // Slate-500
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color(0xFF64748B),
                    start = Offset(size.width, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun ToastIcon(type: ToastType, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(24.dp)) {
        val color = when (type) {
            ToastType.SUCCESS -> Color(0xFF10B981) // Emerald Green
            ToastType.ERROR -> Color(0xFFEF4444)   // Rose Red
            ToastType.INFO -> Color(0xFF3B82F6)    // Indigo Blue
        }
        
        // Draw background accent circle
        drawCircle(color = color, radius = size.minDimension / 2f)

        // Draw foreground details
        when (type) {
            ToastType.SUCCESS -> {
                val checkPath = Path().apply {
                    moveTo(size.width * 0.3f, size.height * 0.5f)
                    lineTo(size.width * 0.45f, size.height * 0.65f)
                    lineTo(size.width * 0.7f, size.height * 0.35f)
                }
                drawPath(
                    path = checkPath,
                    color = Color.White,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            ToastType.ERROR -> {
                drawLine(
                    color = Color.White,
                    start = Offset(size.width * 0.35f, size.height * 0.35f),
                    end = Offset(size.width * 0.65f, size.height * 0.65f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = Color.White,
                    start = Offset(size.width * 0.65f, size.height * 0.35f),
                    end = Offset(size.width * 0.35f, size.height * 0.65f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            ToastType.INFO -> {
                drawCircle(
                    color = Color.White, 
                    radius = 1.5.dp.toPx(), 
                    center = Offset(size.width * 0.5f, size.height * 0.32f)
                )
                drawLine(
                    color = Color.White,
                    start = Offset(size.width * 0.5f, size.height * 0.45f),
                    end = Offset(size.width * 0.5f, size.height * 0.7f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
