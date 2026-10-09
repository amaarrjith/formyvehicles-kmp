package org.example.project

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.viewmodel.PrivacyPolicyViewModel
import org.example.project.viewmodel.PrivacyUiState
import org.koin.compose.koinInject

enum class LegalTab {
    PRIVACY, TERMS
}

/**
 * Unified Privacy Policy and Terms & Conditions screen.
 * Seamlessly toggles between "Privacy Policy" and "Terms of Service".
 * Connects to [PrivacyPolicyViewModel] to dynamically fetch API contents,
 * while providing comprehensive fallback documents if network is offline.
 *
 * @param initialTab "privacy" or "terms" to pre-select upon navigation.
 * @param onBackClick Callback invoked when back arrow is clicked.
 * @param modifier Layout modifier.
 * @param viewModel Injected [PrivacyPolicyViewModel].
 */
@Composable
fun PrivacyPolicyScreen(
    initialTab: String = "privacy",
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacyPolicyViewModel = koinInject()
) {
    var selectedTab by remember(initialTab) {
        mutableStateOf(
            if (initialTab.equals("terms", ignoreCase = true)) LegalTab.TERMS else LegalTab.PRIVACY
        )
    }

    val privacyUiState by viewModel.uiState.collectAsState()
    val termsUiState by viewModel.termsUiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBackButton {
                    onBackClick()
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Legal & Policies",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "ForMyVehicles Agreements & Privacy",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Segmented Switcher Pill
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Privacy Tab
                    val isPrivacySelected = selectedTab == LegalTab.PRIVACY
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isPrivacySelected) Color(0xFF6366F1) else Color.Transparent)
                            .clickable { selectedTab = LegalTab.PRIVACY }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Privacy Policy",
                            fontSize = 14.sp,
                            fontWeight = if (isPrivacySelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isPrivacySelected) Color.White else Color(0xFF475569)
                        )
                    }

                    // Terms Tab
                    val isTermsSelected = selectedTab == LegalTab.TERMS
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isTermsSelected) Color(0xFF6366F1) else Color.Transparent)
                            .clickable { selectedTab = LegalTab.TERMS }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Terms of Service",
                            fontSize = 14.sp,
                            fontWeight = if (isTermsSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isTermsSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle & Last Updated Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (selectedTab == LegalTab.PRIVACY) "Privacy & Data Protection" else "Terms & Conditions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Updated Oct 2026",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4F46E5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tab Content with Smooth Animation
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f)
            ) { tab ->
                when (tab) {
                    LegalTab.PRIVACY -> {
                        LegalTabContent(
                            uiState = privacyUiState,
                            onRetry = { viewModel.getPrivacyContent() }
                        )
                    }
                    LegalTab.TERMS -> {
                        LegalTabContent(
                            uiState = termsUiState,
                            onRetry = { viewModel.getTermsContent() }
                        )
                    }
                }
            }
        }

        BaseToastHost(viewModel = viewModel)
    }
}

@Composable
private fun LegalTabContent(
    uiState: PrivacyUiState,
    onRetry: () -> Unit
) {
    when (uiState) {
        is PrivacyUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF6366F1),
                    modifier = Modifier.size(36.dp),
                    strokeWidth = 3.dp
                )
            }
        }
        is PrivacyUiState.Success -> {
            val content = uiState.content.trim()
            if (content.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = content,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No content available", color = Color(0xFF64748B), fontSize = 15.sp)
                }
            }
        }
        is PrivacyUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.errorMessage,
                    color = Color(0xFFEF4444),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }
        is PrivacyUiState.Idle -> Unit
    }
}