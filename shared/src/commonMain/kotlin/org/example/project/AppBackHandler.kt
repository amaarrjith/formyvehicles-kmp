package org.example.project

import androidx.compose.runtime.Composable

/**
 * Multiplatform Back Handler to intercept or disable back gestures / back button presses.
 *
 * @param enabled Set to true to intercept back actions (e.g., disable back swipe).
 * @param onBack Callback invoked when back gesture/button is triggered.
 */
@Composable
expect fun AppBackHandler(enabled: Boolean = true, onBack: () -> Unit = {})
