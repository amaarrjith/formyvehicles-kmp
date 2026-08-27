package org.example.project

import androidx.compose.runtime.Composable

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Suppresses or handles back swipe gesture on iOS
}
