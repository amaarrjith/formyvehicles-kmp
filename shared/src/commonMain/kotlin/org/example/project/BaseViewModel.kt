package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel

/**
 * Base ViewModel class that provides unified error handling and toast management
 * for all ViewModels across the application.
 */
open class BaseViewModel : ViewModel() {

    var errorMessage by mutableStateOf<String?>(null)
        protected set

    var showToast by mutableStateOf(false)
    var toastMessage by mutableStateOf("")
    var toastTitle by mutableStateOf("")
    var toastType by mutableStateOf(ToastType.INFO)
    var isErrorToast by mutableStateOf(false)

    /**
     * Displays an error toast and updates [errorMessage].
     *
     * @param message Error message to display.
     * @param title Title of the toast notification (defaults to "Error").
     */
    fun showErrorToast(message: String, title: String = "Error") {
        errorMessage = message
        toastMessage = message
        toastTitle = title
        toastType = ToastType.ERROR
        isErrorToast = true
        showToast = true
    }

    /**
     * Displays a success toast.
     *
     * @param message Success message to display.
     * @param title Title of the toast notification (defaults to "Success").
     */
    fun showSuccessToast(message: String, title: String = "Success") {
        toastMessage = message
        toastTitle = title
        toastType = ToastType.SUCCESS
        isErrorToast = false
        showToast = true
    }

    /**
     * Displays an informational toast.
     *
     * @param message Informational message to display.
     * @param title Title of the toast notification (defaults to "Info").
     */
    fun showInfoToast(message: String, title: String = "Info") {
        toastMessage = message
        toastTitle = title
        toastType = ToastType.INFO
        isErrorToast = false
        showToast = true
    }

    /**
     * Clears current error state and dismisses error toast if active.
     */
    open fun clearError() {
        errorMessage = null
        if (isErrorToast) {
            showToast = false
        }
    }

    /**
     * Dismisses the active toast notification.
     */
    fun dismissToast() {
        showToast = false
    }
}

/**
 * Reusable Composable helper to render toasts for any screen
 * powered by a [BaseViewModel].
 */
@Composable
fun BaseToastHost(
    viewModel: BaseViewModel,
    modifier: Modifier = Modifier
) {
    ToastHost(
        visible = viewModel.showToast,
        type = viewModel.toastType,
        title = viewModel.toastTitle.ifBlank {
            when (viewModel.toastType) {
                ToastType.ERROR -> "Error"
                ToastType.SUCCESS -> "Success"
                ToastType.INFO -> "Info"
            }
        },
        message = viewModel.toastMessage.ifBlank { viewModel.errorMessage ?: "" },
        onDismiss = { viewModel.dismissToast() },
        modifier = modifier
    )
}
