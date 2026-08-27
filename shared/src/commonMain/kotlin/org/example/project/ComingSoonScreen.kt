package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import formyvehiclesai.shared.generated.resources.Res
import formyvehiclesai.shared.generated.resources.img_coming_soon
import org.jetbrains.compose.resources.painterResource

import androidx.compose.foundation.layout.statusBarsPadding

/**
 * Reusable AppComingSoon composable accepting title and description,
 * featuring a sleek "COMING SOON" badge line and custom AI-generated vehicle icon.
 */
@Composable
fun AppComingSoon(
    title: String = "Feature Under Development",
    description: String = "We are working hard to bring you exciting new features for your vehicles. Stay tuned!",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(top = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // AI Generated Vehicle App ic_filter Container
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = Color(0x336366F1))
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.img_coming_soon),
                    contentDescription = "Coming Soon ic_filter",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Coming Soon Badge Pill Line
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEEF2FF))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "— COMING SOON —",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1),
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description text line
            Text(
                text = description,
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun AppComingSoonScreen(
    title: String = "Feature Under Development",
    description: String = "We are working hard to bring you exciting new features for your vehicles. Stay tuned!",
    modifier: Modifier = Modifier
) {
    AppComingSoon(
        title = title,
        description = description,
        modifier = modifier
    )
}

@Composable
fun ComingSoonScreen(
    title: String = "Feature Under Development",
    description: String = "We are working hard to bring you exciting new features for your vehicles. Stay tuned!",
    modifier: Modifier = Modifier
) {
    AppComingSoon(
        title = title,
        description = description,
        modifier = modifier
    )
}
