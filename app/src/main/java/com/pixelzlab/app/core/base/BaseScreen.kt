package com.pixelzlab.app.core.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pixelzlab.app.designsystem.component.AppAlertDialog
import com.pixelzlab.app.designsystem.component.AppLoading
import com.pixelzlab.app.designsystem.theme.AppThemePreview
import com.pixelzlab.app.designsystem.theme.Colors

/**
 * Created by pixelzlab on 12/10/2023.
 */

@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    showLoading: Boolean = false,
    refresh: Boolean = false,
    refreshState: PullRefreshState? = null,
    errorMessage: String? = null,
    clearErrorMessage: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        content()
        AnimatedVisibility(
            visible = showLoading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {
                    },
            ) {
                AppLoading()
            }
        }
        refreshState?.let {
            PullRefreshIndicator(
                refreshing = refresh,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = Colors.Primary
            )
        }
        if (!errorMessage.isNullOrEmpty()) {
            AppAlertDialog(
                title = "Error",
                content = errorMessage,
                onClickButtonPrimary = clearErrorMessage,
                onDismissRequest = clearErrorMessage
            )
        }
    }
}

@Preview
@Composable
fun BaseScreenPreview() {
    AppThemePreview {
        BaseScreen(
            showLoading = true
        ) {
        }
    }
}
