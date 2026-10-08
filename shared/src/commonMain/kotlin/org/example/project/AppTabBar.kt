package org.example.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import formyvehiclesai.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Root Tab Bar screen hosting all major modules.
 *
 * @param onAddVehicleClick Callback forwarded to HomeScreen.
 * @param modifier Layout modifier.
 */
@Composable
fun AppTabBar(
    onAddVehicleClick: () -> Unit,
    onVehicleClick: (Vehicle) -> Unit,
    onLogoutClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val isGuest = isGuestUser()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            BottomTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        AppBackHandler(enabled = true) {
            // Do nothing on back gesture to prevent returning to previous screen
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            if (isGuest) {
                when (selectedTab) {
                    0 -> AppGuestPlaceholder(
                        title = "My Vehicles",
                        onLoginClick = onLoginClick
                    )
                    1 -> AppGuestPlaceholder(
                        title = "Emergency Services",
                        onLoginClick = onLoginClick
                    )
                    2 -> ComingSoonScreen(title = "Shops & Services")
                    3 -> org.example.project.ui.OthersScreen(onLogoutClick = onLogoutClick)
                }
            } else {
                when (selectedTab) {
                    0 -> HomeScreen(
                        onAddVehicleClick = onAddVehicleClick,
                        onVehicleClick = onVehicleClick,
                        onLogoutClick = onLogoutClick,
                        onProfileClick = onProfileClick,
                        onNotificationClick = onNotificationClick,
                        onViewAllClick = onViewAllClick
                    )
                    1 -> ComingSoonScreen(title = "Emergency Services")
                    2 -> ComingSoonScreen(title = "Shops & Services")
                    3 -> org.example.project.ui.OthersScreen(onLogoutClick = onLogoutClick)
                }
            }
        }
    }
}

@Composable
fun BottomTabBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
        ) {
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 25.dp),
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabItem(
                    title = "My Vehicles",
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    icon = Res.drawable.ic_home1,
                    Res.drawable.ic_home_selected,
                    modifier = Modifier.weight(1f)
                )

                TabItem(
                    title = "Emergency",
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    icon = Res.drawable.ic_emergency,
                    Res.drawable.ic_emergency_selected,
                    modifier = Modifier.weight(1f)
                )

                TabItem(
                    title = "Shops",
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    icon = Res.drawable.ic_shop,
                    Res.drawable.ic_shop_selected,
                    modifier = Modifier.weight(1f)
                )

                TabItem(
                    title = "Others",
                    isSelected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    icon = Res.drawable.ic_others,
                    Res.drawable.ic_others_selected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: org.jetbrains.compose.resources.DrawableResource,
    selectedIcon: org.jetbrains.compose.resources.DrawableResource,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFF682BF7) // Indigo/Purple

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(if (isSelected) {selectedIcon} else {icon}),
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = title,
            color = if (isSelected) activeColor else Color(0xFF64748B),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * Modifier helper to draw shadow circles.
 */
fun Modifier.shadowCircle(color: Color): Modifier = this.then(
    Modifier
        .background(color, CircleShape)
        .clip(CircleShape)
        .padding(2.dp)
)

@Composable
fun SettingsScreen(
    onLogoutClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    onTermsAndPrivacyClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val user = remember { getLoggedInUser() }
    val userName = remember(user) {
        user?.name?.takeIf { it.isNotBlank() } ?: getPersistedString("logged_in_user_name")?.takeIf { it.isNotBlank() } ?: "User Profile"
    }
    val userMobile = remember(user) {
        val num = user?.mobileNumber?.takeIf { it.isNotBlank() } ?: getPersistedString("logged_in_user_mobile")?.takeIf { it.isNotBlank() }
        if (num != null) {
            val code = user?.countryCode?.takeIf { it.isNotBlank() } ?: "+91"
            "$code $num"
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (onBackClick != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBackButton {
                    onBackClick()
                }
            }
        }

        Text(
            text = "Profile & Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(18.dp))

        // Profile Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFEEF2F6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = remember(userName) {
                        val trimmed = userName.trim()
                        if (trimmed.isEmpty()) "U"
                        else {
                            val parts = trimmed.split("\\s+".toRegex())
                            if (parts.size == 1) parts[0].take(2).uppercase()
                            else (parts[0].take(1) + parts[1].take(1)).uppercase()
                        }
                    }
                    Text(
                        text = initials,
                        color = Color(0xFF6366F1),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    if (userMobile != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userMobile,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Legal & Policies",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingMenuItem(
                title = "Terms & Conditions",
                subtitle = "Read our terms of service and usage rules",
                onClick = { onTermsAndPrivacyClick("terms") }
            )

            SettingMenuItem(
                title = "Privacy Policy",
                subtitle = "Learn how we protect and manage your data",
                onClick = { onTermsAndPrivacyClick("privacy") }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Red Logout button card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogoutClick() },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFA))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        // Draw a simple exit door/arrow symbol
                        val path = Path().apply {
                            moveTo(size.width * 0.3f, size.height * 0.1f)
                            lineTo(size.width * 0.8f, size.height * 0.1f)
                            lineTo(size.width * 0.8f, size.height * 0.9f)
                            lineTo(size.width * 0.3f, size.height * 0.9f)
                        }
                        drawPath(path, color = Color(0xFFEF4444), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                        
                        // Arrow
                        drawLine(Color(0xFFEF4444), Offset(size.width * 0.5f, size.height * 0.5f), Offset(0f, size.height * 0.5f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFFEF4444), Offset(0f, size.height * 0.5f), Offset(size.width * 0.2f, size.height * 0.3f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(Color(0xFFEF4444), Offset(0f, size.height * 0.5f), Offset(size.width * 0.2f, size.height * 0.7f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Log Out",
                            color = Color(0xFFEF4444),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sign out from this account safely",
                            color = Color(0xFFFDA4AF),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF1E293B),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
            // Chevron right
            Canvas(modifier = Modifier.size(12.dp)) {
                val path = Path().apply {
                    moveTo(size.width * 0.3f, size.height * 0.1f)
                    lineTo(size.width * 0.8f, size.height * 0.5f)
                    lineTo(size.width * 0.3f, size.height * 0.9f)
                }
                drawPath(path, color = Color(0xFF94A3B8), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}
