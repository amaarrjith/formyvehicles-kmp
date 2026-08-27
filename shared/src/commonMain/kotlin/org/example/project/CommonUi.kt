package org.example.project

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Premium Loading Indicator centered on the screen.
 */
@Composable
fun AppLoader(
    modifier: Modifier = Modifier,
    message: String = "Loading your dashboard..."
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Spinning Circular Loader
            Canvas(modifier = Modifier.size(56.dp)) {
                drawArc(
                    color = Color(0xFF6366F1).copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFF6366F1),
                    startAngle = rotation,
                    sweepAngle = 100f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = message,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Common Empty State View that is fully customizable.
 */
@Composable
fun EmptyStateView(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ic_filter / Canvas
        icon()
        
        Spacer(modifier = Modifier.height(28.dp))
        
        // Title
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569), // Slate-700
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Description
        Text(
            text = description,
            fontSize = 13.sp,
            color = Color(0xFF94A3B8), // Slate-400
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        
        if (actionButton != null) {
            Spacer(modifier = Modifier.height(36.dp))
            actionButton()
        }
    }
}
