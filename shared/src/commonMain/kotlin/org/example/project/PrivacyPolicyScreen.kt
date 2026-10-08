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
                            onRetry = { viewModel.getPrivacyContent() },
                            fallbackContent = { FallbackPrivacyContent() }
                        )
                    }
                    LegalTab.TERMS -> {
                        LegalTabContent(
                            uiState = termsUiState,
                            onRetry = { viewModel.getTermsContent() },
                            fallbackContent = { FallbackTermsContent() }
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
    onRetry: () -> Unit,
    fallbackContent: @Composable () -> Unit
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
                fallbackContent()
            }
        }
        is PrivacyUiState.Error, PrivacyUiState.Idle -> {
            // Render built-in fallback content so screen is always readable
            fallbackContent()
        }
    }
}

// Built-in Terms Content
@Composable
private fun FallbackTermsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        LegalSectionCard(
            number = "1",
            title = "Acceptance of Terms",
            body = "By accessing or using the ForMyVehicles application (\"App\"), you agree to be bound by these Terms and Conditions. If you do not agree to these terms, please refrain from using our services."
        )

        LegalSectionCard(
            number = "2",
            title = "Vehicle Management & Profiles",
            body = "Users may register one or multiple vehicles under their account. You agree to provide accurate and authentic vehicle information (such as registration numbers, vehicle brands, models, fuel types, and categories). You are solely responsible for keeping your vehicle data current."
        )

        LegalSectionCard(
            number = "3",
            title = "Alerts & Expiry Notifications",
            body = "ForMyVehicles provides automated reminders and tracking for insurance, pollution (PUC), service intervals, and token taxes. While we endeavor to send timely notifications, vehicle owners remain ultimately responsible for statutory compliance and regulatory dues."
        )

        LegalSectionCard(
            number = "4",
            title = "Prohibited Conduct",
            body = "You agree not to use the platform for fraudulent purposes, submit false vehicle documentation, reverse engineer the application, or interfere with network security protocols. Violation of these rules may lead to immediate account suspension."
        )

        LegalSectionCard(
            number = "5",
            title = "Intellectual Property",
            body = "All logos, branding, user interface designs, and intellectual property within ForMyVehicles remain the exclusive property of ForMyVehicles and its licensors. No unauthorized duplication is allowed."
        )

        LegalSectionCard(
            number = "6",
            title = "Limitation of Liability",
            body = "ForMyVehicles provides this service on an 'as-is' and 'as-available' basis. We are not liable for incidental, indirect, or consequential damages resulting from missed reminders, network outages, or unauthorized third-party device access."
        )

        LegalSectionCard(
            number = "7",
            title = "Termination",
            body = "We reserve the right to restrict or terminate your access to the App at any time without prior notice if you violate these terms or engage in activity detrimental to the community."
        )

        LegalSectionCard(
            number = "8",
            title = "Customer Support & Inquiries",
            body = "For questions or concerns regarding our terms, reach out to our legal department at support@formyvehicles.com."
        )
    }
}

// Built-in Privacy Policy Content
@Composable
private fun FallbackPrivacyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        LegalSectionCard(
            number = "1",
            title = "Information We Collect",
            body = "We collect the following personal and vehicle data to power your dashboard:\n• Account info: Full Name and Mobile Number for OTP verification.\n• Vehicle info: Registration numbers, fuel type, transmission, color, brand, and model.\n• Location data: Device location to suggest local workshops, towing, FASTag, and emergency services.\n• Document expiry records: Insurance, PUC, and service milestone dates."
        )

        LegalSectionCard(
            number = "2",
            title = "How We Use Your Information",
            body = "Your information is used strictly to:\n• Authenticate your account securely.\n• Track vehicle health and generate maintenance & renewal reminders.\n• Connect you with verified emergency mechanics, tyre shops, and utility providers.\n• Continuously enhance app usability, uptime, and performance."
        )

        LegalSectionCard(
            number = "3",
            title = "Data Security",
            body = "We utilize industry-standard TLS encryption for network transport and secure persistent storage on your mobile device. Authentication credentials and refresh tokens are safeguarded with strict access controls."
        )

        LegalSectionCard(
            number = "4",
            title = "Third-Party Services",
            body = "We only share necessary location or emergency details with third-party service providers (e.g. towing operators or repair shops) when you explicitly request a roadside assistance or shop booking."
        )

        LegalSectionCard(
            number = "5",
            title = "Your Rights & Data Control",
            body = "You have complete control over your data:\n• Edit or remove vehicle records at any time directly in 'My Vehicles'.\n• Update profile details in Settings.\n• Request complete account and vehicle data deletion by contacting privacy@formyvehicles.com."
        )

        LegalSectionCard(
            number = "6",
            title = "Retention Period",
            body = "We retain vehicle information as long as your account remains active. If you choose to delete your account, your personal identification records will be permanently purged within 30 days."
        )

        LegalSectionCard(
            number = "7",
            title = "Data Protection Officer",
            body = "If you have any questions or requests concerning your privacy, please contact our Data Protection Officer at privacy@formyvehicles.com."
        )
    }
}

@Composable
private fun LegalSectionCard(
    number: String,
    title: String,
    body: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(Color(0xFFEEF2FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF475569)
            )
        }
    }
}