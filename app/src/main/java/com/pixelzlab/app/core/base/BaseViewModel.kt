package com.pixelzlab.app.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelzlab.app.core.navigation.AppDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel : ViewModel() {

    private var loadingCount: Int = 0

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    protected val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> = _error

    protected val _navigator = MutableSharedFlow<AppDestination>()
    val navigator: SharedFlow<AppDestination> = _navigator

    /**
     * To show loading manually, should call `hideLoading` after
     */
    protected fun showLoading() {
        if (loadingCount == 0) {
            _isLoading.value = true
        }
        loadingCount++
    }

    /**
     * To hide loading manually, should be called after `showLoading`
     */
    protected fun hideLoading() {
        loadingCount--
        if (loadingCount == 0) {
            _isLoading.value = false
        }
    }

    protected fun launch(context: CoroutineContext = EmptyCoroutineContext, job: suspend () -> Unit) =
        viewModelScope.launch(context) {
            try {
                job.invoke()
            } catch (e: Exception) {
                Timber.e(e, "Uncaught exception in coroutine")
                _error.value = e
            }
        }

    protected fun <T> Flow<T>.loading(): Flow<T> = this
        .onStart { showLoading() }
        .onCompletion { hideLoading() }

    /**
     * Converts Flow to StateFlow with proper error handling and automatic collection
     * Based on Now in Android pattern
     */
    protected fun <T> Flow<T>.stateWithLoading(
        initialValue: T,
        started: SharingStarted = SharingStarted.WhileSubscribed(5000L)
    ): StateFlow<T> = this
        .loading()
        .catch { e -> 
            Timber.e(e, "Error in stateWithLoading")
            _error.value = e
        }
        .stateIn(
            scope = viewModelScope,
            started = started,
            initialValue = initialValue
        )

    suspend fun <T> Flow<T>.async(
        action: suspend (T) -> Unit
    ) {
        try {
            this.collect { value ->
                action(value)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error in async flow")
            _error.value = e
        }
    }
}
