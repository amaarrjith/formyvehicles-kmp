package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import formyvehiclesai.shared.generated.resources.Res
import formyvehiclesai.shared.generated.resources.ic_backbtn
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppBackButton(
    onBackButtonPressed: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.White, CircleShape)
            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            .clickable { onBackButtonPressed() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(16.dp)) {
            val path = Path().apply {
                moveTo(size.width * 0.6f, size.height * 0.2f)
                lineTo(size.width * 0.3f, size.height * 0.5f)
                lineTo(size.width * 0.6f, size.height * 0.8f)
            }
            drawPath(
                path = path,
                color = Color(0xFF1E293B),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}