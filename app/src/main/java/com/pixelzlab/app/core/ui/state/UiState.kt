package com.pixelzlab.app.core.ui.state

/**
 * Generic UI state pattern for handling loading, success, and error states
 * @param T The type of data wrapped in this UI state
 */
sealed interface UiState<out T> {
    /**
     * Represents a UI state with a loading indicator
     */
    object Loading : UiState<Nothing>
    
    /**
     * Represents a successful UI state with data
     * @param data The data loaded in this state
     */
    data class Success<T>(val data: T) : UiState<T>
    
    /**
     * Represents an error UI state
     * @param error The exception or error that occurred
     * @param data Optional data that might be available even when an error occurred
     */
    data class Error(val error: Throwable, val data: Nothing? = null) : UiState<Nothing>
}

/**
 * Extension function to handle UI state with a builder pattern
 */
inline fun <T, R> UiState<T>.fold(
    onLoading: () -> R,
    onSuccess: (data: T) -> R,
    onError: (error: Throwable, data: Nothing?) -> R
): R {
    return when (this) {
        is UiState.Loading -> onLoading()
        is UiState.Success -> onSuccess(data)
        is UiState.Error -> onError(error, data)
    }
} 