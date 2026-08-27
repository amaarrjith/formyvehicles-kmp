package org.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.em
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource
import formyvehiclesai.shared.generated.resources.*

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToGuest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .background(Color.White)
        ) {
            // Background Grid of Images matching Figma layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SplashImage(Res.drawable.ic_welcome_1, 160.dp)
                    SplashImage(Res.drawable.ic_welcome_1_2, 140.dp)
                    SplashImage(Res.drawable.ic_welcome_1_3, 170.dp)
                }

                // Middle Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SplashImage(Res.drawable.ic_welcome_1_4, 210.dp)
                    SplashImage(Res.drawable.ic_welcome_1_5, 140.dp)
                    SplashImage(Res.drawable.ic_welcome_1_6, 170.dp)
                }

                // Right Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SplashImage(Res.drawable.ic_welcome_1_7, 210.dp)
                    SplashImage(Res.drawable.hc_1, 140.dp)
                    SplashImage(Res.drawable.oth_1, 170.dp)
                }
            }

            // Gradient overlay to smoothly fade images to white at the bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.38f to Color.Transparent,
                            0.58f to Color.White,
                            1.0f to Color.White
                        )
                    )
            )

            // Text content and buttons over the gradient matching Figma
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Welcome to",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ForMyVehicles ",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "AI",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6338F6)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "An application to manage your vehicle's\nhealth and emergency support.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B),
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(36.dp))
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6338F6)
                    )
                ) {
                    Text(
                        text = "Login / Sign Up",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.01.em
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onNavigateToGuest() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Continue as Guest",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155)
                    )
                }
            }
        }
    }
}

@Composable
fun SplashImage(
    image: DrawableResource,
    height: Dp
) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(18.dp)),
        contentScale = ContentScale.Crop
    )
}


@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onNavigateToLogin = {})
}